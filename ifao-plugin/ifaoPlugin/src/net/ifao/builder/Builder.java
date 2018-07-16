package net.ifao.builder;


import java.io.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.*;


public class Builder
   extends IncrementalProjectBuilder
{

   private static boolean bInvalidArcticRequestResponse = false;

   @Override
   protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
   {
      // validate if batch file exists
      try {
         File buildSchemaFilesBat =
            getProject().getFile("conf/build/BuildSchemaFiles.bat").getRawLocation().toFile();
         if (!buildSchemaFilesBat.exists()) {
            // try other ant location
            buildSchemaFilesBat = getProject().getFile("update/config/updateSchemaFiles.ant")
                  .getRawLocation().toFile();
         }
         if (buildSchemaFilesBat.exists()) {
            Hashtable<String, String> htFiles = new Hashtable<String, String>();
            if (kind == IncrementalProjectBuilder.FULL_BUILD) {
               fullBuild(htFiles);
            } else {
               IResourceDelta delta = getDelta(getProject());
               if (delta == null) {
                  fullBuild(htFiles);
               } else {
                  incrementalBuild(delta, htFiles);
               }
            }

            String[] sarrFiles = htFiles.keySet().toArray(new String[0]);
            int iAmountOfSchemaFiles = sarrFiles.length;
            if (iAmountOfSchemaFiles > 0) {

               HashSet<String> hsPackages = new HashSet<String>();
               for (String sKey : sarrFiles) {
                  hsPackages.add(htFiles.get(sKey));
               }
               monitor.beginTask("Build Schema files",
                     iAmountOfSchemaFiles + 1 + hsPackages.size());
               Arrays.sort(sarrFiles);
               for (int i = 0; (i < iAmountOfSchemaFiles) && !monitor.isCanceled(); i++) {
                  String sFile = sarrFiles[i];
                  monitor.subTask("generate java classes for " + sFile);
                  execute(buildSchemaFilesBat, sFile, i + 1 == iAmountOfSchemaFiles);
                  monitor.worked(1);
               }
               try {
                  if (!monitor.isCanceled()) {
                     monitor.subTask("refresh conf definitions");
                     // update the definitions
                     IFolder folder = getProject().getFolder("conf/definitions");
                     folder.refreshLocal(IResource.DEPTH_ZERO, monitor);
                     monitor.worked(1);

                     // scan the packages
                     for (String sPackage : hsPackages) {
                        if (monitor.isCanceled())
                           break;
                        monitor.subTask("refresh classes for package " + sPackage);
                        // try to refresh the files
                        folder =
                           getProject().getFolder("src-gen/" + sPackage.replaceAll("\\.", "/"));
                        folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                        monitor.worked(1);
                     }
                  }
               }
               catch (Exception ex) {
                  // could not refresh the folder
               }
            }
            monitor.done();
         }
      }
      catch (Exception ex) {
         // ignore this
      }
      return null;
   }

   private static void execute(File pBatchFile, String psFile, boolean pbCleanup)
      throws Exception
   {
      // execute
      File buildDir = pBatchFile.getParentFile();
      File file = new File(buildDir, "temp/generate." + psFile + ".bat");
      // generate temp dir
      if (!file.getParentFile().exists()) {
         file.getParentFile().mkdirs();
      }
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write("@echo off\n");
      String sBuildPath = buildDir.getCanonicalPath();
      writer.write(sBuildPath.substring(0, 2) + "\n");
      writer.write("cd \"" + sBuildPath + "\"\n");
      writer.write("ECHO ... generate java classes ...\n");
      if (pBatchFile.getName().endsWith(".bat")) {
         writer.write(
               "call BuildSchemaFiles.bat " + psFile + (!pbCleanup ? " -nocleanup\n" : "\n"));
      } else if (pBatchFile.getName().endsWith(".ant")) {
         String newFile = psFile.replaceAll("\\..+", ".xsd");
         writer.write("ant -f " + pBatchFile.getName() + " " + newFile + "\n");
      }
      writer.close();

      Process exec =
         Runtime.getRuntime().exec(new String[]{ "cmd", "/c", file.getCanonicalPath() });

      PrintStream out = new PrintStream(
            new FileOutputStream(new File(buildDir, "temp/generate." + psFile + ".out")));
      new ListenThread(exec.getInputStream(), out).start();
      new ListenThread(exec.getErrorStream(), out).start();

      if (exec.waitFor() == 0) {
         out.println("... finished");
      } else {
         out.println("... error");
      }
      out.close();

   }

   static class ListenThread
      extends Thread
   {
      private BufferedInputStream bIn;
      private PrintStream out;

      ListenThread(InputStream in, PrintStream pOut)
      {
         bIn = new BufferedInputStream(in);
         out = pOut;
      }

      @Override
      public void run()
      {
         int c;
         try {
            StringBuilder line = new StringBuilder();
            while ((c = bIn.read()) != -1) {
               line.append((char) c);
               if (c == '\n') {
                  out.print(line.toString());
                  line.setLength(0);
               }

            }
            bIn.close();
            out.print(line.toString());
         }
         catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   private void incrementalBuild(IResourceDelta delta, final Hashtable<String, String> htFiles)
   {
      try {
         delta.accept(new IResourceDeltaVisitor()
         {
            @Override
            public boolean visit(IResourceDelta pDelta)
            {
               try {
                  IResource resource = pDelta.getResource();
                  IPath rawLocation = resource.getRawLocation();
                  if (rawLocation != null) {
                     update(rawLocation.toFile(), htFiles);
                  }
               }
               catch (Exception ex) {
                  ex.printStackTrace();
               }
               return true; // visit children too
            }
         });
      }
      catch (CoreException e) {
         e.printStackTrace();
      }
   }

   private void fullBuild(Hashtable<String, String> htFiles)
   {
      System.out.println("full build");
      try {
         IFile rawDefinitions = getProject().getFile("conf/definitions");
         File definitions = rawDefinitions.getRawLocation().toFile();
         // get the segments
         File[] segments = definitions.listFiles();
         for (File file : segments) {
            if (file.getName().endsWith(".xsd") || file.getName().endsWith(".dtd")) {
               update(file, htFiles);
            }
         }
      }
      catch (Exception ex) {
         // invalid directory
      }
   }

   void update(File file, Hashtable<String, String> htFiles)
   {
      File definitions = file.getParentFile();
      if (!definitions.getName().equals("definitions")
            && !definitions.getParent().endsWith("conf")) {
         return;
      }
      String sName = file.getName();
      if (htFiles.contains(sName)) {
         // ignore this file
      } else if (sName.endsWith(".xsd")) {
         File dtd = new File(definitions, sName.substring(0, sName.indexOf(".") + 1) + "dtd");
         // if the dtdFile exists
         if (dtd.exists()) {
            // ignore this file
            return;
         }
         String sPackage = getPackage(file);
         if (sPackage != null)
            htFiles.put(sName, sPackage);

      } else if (sName.endsWith(".dtd")) {
         String sLowerName = sName.substring(0, sName.lastIndexOf(".")).toLowerCase();
         if (sName.equals("ArcticRequest.dtd") || sName.equals("ArcticResponse.dtd")) {
            // remove arctic
            sLowerName = sLowerName.substring(6);
            if (!compareArcticRequestResponse(definitions, sName)) {
               // ignore this file
               bInvalidArcticRequestResponse = true;
               return;
            }
            // if the last time, there was a difference
            if (bInvalidArcticRequestResponse) {
               bInvalidArcticRequestResponse = false;
               // re-compile both files
               htFiles.put("ArcticRequest.dtd", "net.ifao.arctic.xml.request");
               htFiles.put("ArcticResponse.dtd", "net.ifao.arctic.xml.response");
            }
         }

         // update
         htFiles.put(sName, "net.ifao.arctic.xml." + sLowerName);
      }
   }

   private String getPackage(File pXsdFile)
   {
      String sPackage = null;

      String sName = pXsdFile.getName();
      sName = sName.substring(0, sName.lastIndexOf(".") + 1);
      // validate castor 
      File bindXml = new File(pXsdFile.getParentFile(), sName + "bind.xml");
      if (bindXml.exists()) {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(bindXml));
            String sLine;
            while ((sLine = reader.readLine()) != null) {
               if (sLine.contains("<name>") && sLine.contains("</name>")) {
                  sPackage = sLine.substring(sLine.indexOf("<name>") + 6, sLine.indexOf("</name>"));
                  break;
               }
            }
            reader.close();
         }
         catch (Exception e) {
            // could not read
         }
         return sPackage;
      } else {
         File xjbFile = new File(pXsdFile.getParentFile(), sName + "xjb");
         if (xjbFile.exists()) {
            try {
               BufferedReader reader = new BufferedReader(new FileReader(xjbFile));
               String sLine;
               while ((sLine = reader.readLine()) != null) {
                  if (sLine.contains("package name=\"")) {
                     int iStart = sLine.indexOf("package name=\"") + 14;
                     sPackage = sLine.substring(iStart, sLine.indexOf("\"", iStart));
                     break;
                  }
               }
               reader.close();
            }
            catch (Exception e) {
               // could not read
            }
            return sPackage;
         }
      }

      return sPackage;
   }

   private boolean compareArcticRequestResponse(File definitions, String sName)
   {
      String sVersionReq = getVersion(new File(definitions, "ArcticRequest.dtd"));
      String sVersionRes = getVersion(new File(definitions, "ArcticResponse.dtd"));

      if (sVersionReq.equals(sVersionRes)) {
         return true;
      }

      final String sMessage =
         "Don't compile " + sName + " because version does not match !\nRequest:" + sVersionReq
               + "\nResponse:" + sVersionRes;
      Display.getDefault().asyncExec(new Runnable()
      {
         @Override
         public void run()
         {
            MessageDialog.openError(new Shell(), "ErrorDialog", sMessage);
         }
      });
      System.out.println(sMessage);
      return false;
   }

   private String getVersion(File file)
   {
      BufferedReader reader = null;
      try {
         reader = new BufferedReader(new FileReader(file));
         String sLine;
         while ((sLine = reader.readLine()) != null) {
            if (sLine.contains("version %Version; \"")) {
               return sLine.substring(sLine.indexOf("\""), sLine.lastIndexOf("\"") + 1);
            }
         }
      }
      catch (Exception e) {
         return "Error:" + file.getName() + ":" + e.getLocalizedMessage();
      }
      finally {
         try {
            if (reader != null) {
               reader.close();
            }
         }
         catch (IOException e) {
            // could not close
         }
      }
      return "NoVersion:" + file.getName();
   }
}
