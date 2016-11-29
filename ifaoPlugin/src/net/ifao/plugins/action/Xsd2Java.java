package net.ifao.plugins.action;


import ifaoplugin.Util;

import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;
import schemagenerator.actions.Utils;


public class Xsd2Java
   extends ActionAdapter
{
   private boolean bRefreshDirectory;

   public Xsd2Java()
   {
      super();
   }

   @Override
   protected boolean refreshDirectory()
   {
      return bRefreshDirectory;
   }

   @Override
   public boolean doAction(String psFileName, InputStream pisStream, String psEclipseRoot,
                           File pXsdFile)
   {
      bRefreshDirectory = true;
      try {
         String canonicalPath = pXsdFile.getCanonicalPath();
         String sNetIfao = File.separator + "net" + File.separator + "ifao" + File.separator;
         if (canonicalPath.contains(sNetIfao)) {
            String sRoot = canonicalPath.substring(0, canonicalPath.lastIndexOf(sNetIfao));
            String sFile = canonicalPath.substring(sRoot.length() + 1);
            String sPackage =
               sFile.substring(0, sFile.lastIndexOf(File.separator)).replaceAll("\\\\", ".")
                     .replaceAll("\\/", ".");
            // create a batch File (to generate the classes

            // validate if this file is jaxB class
            File baseDirectory = pXsdFile.getParentFile();
            File fileBindings = new File(baseDirectory, "bindings.xjb");

            if (!fileBindings.exists()) {
               if (openConfirm("JaxB binding file 'bindings.xjb' not found !!!\nDo you want to create the file ?")) {
                  ByteArrayOutputStream out = new ByteArrayOutputStream();
                  PrintStream p = new PrintStream(out);
                  p.println("<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\"");
                  p.println("      xmlns=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
                  p.println("      xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"");
                  p.println("      jaxb:version=\"1.0\" jaxb:extensionBindingPrefixes=\"xjc\">");
                  p.println("   <jxb:bindings schemaLocation=\"data.xsd\">");
                  p.println("      <jxb:schemaBindings>");
                  p.println("         <jxb:package name=\"" + sPackage + "\" />");
                  p.println("         <!-- jxb:nameXmlTransform>");
                  p.println("            <jxb:typeName prefix=\"Log\" />");
                  p.println("            <jxb:elementName prefix=\"Log\" />");
                  p.println("         </jxb:nameXmlTransform -->");
                  p.println("      </jxb:schemaBindings>");
                  p.println("   </jxb:bindings>");
                  p.println("</jxb:bindings>");

                  Util.writeToFile(fileBindings, out.toByteArray());
               }
            }
            if (fileBindings.exists()) {
               // validate the schema file
               XmlObject xjb = new XmlObject(fileBindings).getFirstObject();
               XmlObject bindings =
                  xjb.findSubObject("bindings", "schemaLocation", pXsdFile.getName());
               if (bindings == null) {
                  openError("schemaLocation for " + pXsdFile.getName()
                        + " not found\nwithin file bindings.xjb");
                  return false;
               }
               XmlObject schemaBindings = bindings.findSubObject("schemaBindings");
               String sPackageName = "";
               if (schemaBindings != null) {
                  XmlObject p = schemaBindings.findSubObject("package");
                  sPackageName = p.getAttribute("name");
               }
               if (!sPackage.equalsIgnoreCase(sPackageName)) {
                  openError("package ("
                        + sPackage
                        + ") for "
                        + pXsdFile.getName()
                        + " does not match the package,\ndefined within binding file bindings.xjb ("
                        + sPackageName + ") !!!");
                  return false;
               }

               StringBuilder sb = new StringBuilder();
               sb.append("@echo off\n");
               sb.append(sRoot.substring(0, 2) + "\n");
               sb.append("CD \"" + sRoot + "\"\n");
               sb.append("xjc -b \"" + fileBindings.getCanonicalPath()
                     + "\" -extension -xmlschema \"" + canonicalPath + "\"\n");

               // delete all java files
               deleteJavaFiles(baseDirectory);
               // execute the batch File
               Utils.executeBat("Generate Classes for package " + sPackage, sb.toString());

               // correct the generated java files
               correctJavaFiles(baseDirectory);
            }

         } else {
            openError("The file has to be with a \"/net/ifao/\" directory !!!");
         }
      }
      catch (IOException e) {
         // should never happen on existing files
      }
      return true;
   }

   private void correctJavaFiles(File baseDirectory)
      throws IOException
   {
      File[] listFiles = baseDirectory.listFiles();
      for (File file : listFiles) {
         if (file.isDirectory()) {
            correctJavaFiles(file);
         } else if (file.getName().endsWith(".java")) {
            String[] lines = getLines(file, false);
            boolean bJaxB = false;
            StringBuilder sbOut = new StringBuilder();
            // validate if file is a JaxB File
            for (String sLine : lines) {
               if (sLine.contains("http://java.sun.com/xml/jaxb")) {
                  bJaxB = true;
               }
               if (sLine.startsWith("// Generated on:")) {
                  // ignore this line
               } else {
                  sbOut.append(sLine + "\n");
               }
            }
            if (bJaxB) {
               BufferedWriter writer = new BufferedWriter(new FileWriter(file));
               writer.write(sbOut.toString());
               writer.flush();
               writer.close();
            }
         }
      }
   }

   private void deleteJavaFiles(File baseDirectory)
      throws IOException
   {
      File[] listFiles = baseDirectory.listFiles();
      for (File file : listFiles) {
         if (file.isDirectory()) {
            deleteJavaFiles(file);
         } else if (file.getName().endsWith(".java")) {
            String[] header = getLines(file, true);
            boolean bJaxB = false;
            // validate if file is a JaxB File
            for (String sHeader : header) {
               if (sHeader.contains("http://java.sun.com/xml/jaxb")) {
                  bJaxB = true;
                  break;
               }
            }
            if (bJaxB) {
               file.delete();
            }
         }
      }
   }

   private String[] getLines(File file, boolean pbHeaderOnly)
      throws IOException
   {
      List<String> lst = new ArrayList<String>();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String sLine;
      if (pbHeaderOnly) {
         while ((sLine = reader.readLine()) != null) {
            if (!sLine.startsWith("//")) {
               break;
            }
            lst.add(sLine);
         }

      } else {
         while ((sLine = reader.readLine()) != null) {
            lst.add(sLine);
         }
      }
      reader.close();
      return lst.toArray(new String[lst.size()]);
   }
}
