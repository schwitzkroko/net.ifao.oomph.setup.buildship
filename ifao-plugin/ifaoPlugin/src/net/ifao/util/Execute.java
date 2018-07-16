package net.ifao.util;


import ifaoplugin.*;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.*;

import net.ifao.plugin.preferences.PreferenceConstants;


public class Execute
{

   /**
    * Starts an external arctic tool and returns the complete console output
    *
    * @param psClass Fully qualified name of the tool class
    * @param psPathToArctic  path to the arctic/arcticTools project
    * @param psParameter additional command line parameters
    * @param pbArcticTools if true, the tool is in an arcticTools project
    * @return output of the console as String
    * @throws Exception
    *
    * @author kaufmann
    */
   public static String startAndGetWholeOutput(String psClass, String psPathToArctic,
                                               String psParameter, boolean pbArcticTools)
      throws Exception
   {
      return start(psClass, psPathToArctic, null, psParameter, pbArcticTools, true);
   }

   /**
    * Starts an external arctic tool and returns only the last (100) lines of the console output.
    * The path to the project is taken from the plugin's settings (window/preferences/IfaoPlugin Preferences)
    *
    * @param psClass Fully qualified name of the tool class
    * @param psParameter additional command line parameters
    * @param pbArcticTools if true, the tool is in an arcticTools project
    * @return the last (100) lines of the console output
    * @throws Exception
    *
    * @author kaufmann
    */
   public static String start(String psClass, String psParameter, boolean pbArcticTools)
      throws Exception
   {
      String sArcticToolsPath =
         Activator
               .getDefault()
               .getPreferenceStore()
               .getString(
                     pbArcticTools ? PreferenceConstants.P_PATH_ARCTICTOOLS
                           : PreferenceConstants.P_PATH_ARCTIC);

      String sAdditionalClasspath =
         Activator.getDefault().getPreferenceStore()
               .getString(PreferenceConstants.P_PATH_ADDITIONAL_CLASSES);

      return start(psClass, sArcticToolsPath, sAdditionalClasspath, psParameter, pbArcticTools,
            false);
   }


   /**
    * Starts an external arctic tool and collects the console output
    *
    * @param psClass Fully qualified name of the tool class
    * @param psArcticToolsPath path to the arctic/arcticTools project
    * @param psAdditionalClasspath
    * @param psParameter
    * @param pbArcticTools
    * @param pbWholeOutput if true, the complete console output is returned, if false only the
    * last (100) lines are returned
    * @return
    * @throws Exception
    *
    * @author kaufmann
    */
   private static String start(String psClass, String psArcticToolsPath,
                               String psAdditionalClasspath, String psParameter,
                               boolean pbArcticTools, boolean pbWholeOutput)
      throws Exception
   {
      if (psArcticToolsPath == null || psArcticToolsPath.length() == 0) {
         JOptionPane.showMessageDialog(null,
               "No directory for 'arctic Tools' defined within settings", "Configuration Error",
               JOptionPane.WARNING_MESSAGE);
      } else {

         // create a tempFile
         String sTempDirectory = System.getenv("TEMP");
         if (sTempDirectory == null || !new File(sTempDirectory).exists()) {
            sTempDirectory = System.getenv("TEMP");
            if (sTempDirectory == null || !new File(sTempDirectory).exists()) {
               sTempDirectory = "C:\\temp";
               if (!new File(sTempDirectory).exists()) {
                  new File(sTempDirectory).mkdirs();
               }
            }
         }

         // create a temp File
         File f = new File(sTempDirectory, "Start-" + psClass.replaceAll("\\.", "_") + ".bat");
         if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
         }
         StringBuilder sb = new StringBuilder();
         sb.append("@echo off\n");
         if (psArcticToolsPath.charAt(1) == ':') {
            sb.append(psArcticToolsPath.substring(0, 2) + "\n");
         }
         sb.append("CD " + psArcticToolsPath.substring(2) + "\n");
         // search java Files
         String sJavaBin = getJavaBin(System.getenv("ProgramFiles"));
         if (sJavaBin == null) {
            sJavaBin = getJavaBin(System.getenv("ProgramFiles(x86)"));
         }
         if (sJavaBin != null) {
            sb.append("\"" + sJavaBin + "\"");
         } else {
            sb.append("java");
         }

         sb.append(" -classpath \"");
         sb.append("classes;class;bin");
         if (psAdditionalClasspath != null && psAdditionalClasspath.length() > 0) {
            sb.append(";");
            sb.append(psAdditionalClasspath);
         }
         ArrayList<File> listFiles = new ArrayList<File>();
         if (pbArcticTools) {
            addFiles(listFiles, new File(psArcticToolsPath, "extFiles" + File.separator + "lib"));
            addFiles(listFiles, new File(psArcticToolsPath, "extFiles" + File.separator + "lib"
                  + File.separator + "swt"));
         } else {
            addFiles(listFiles, new File(psArcticToolsPath, "extFiles" + File.separator + "lib"));
            addFiles(listFiles, new File(psArcticToolsPath, "lib"));
            addFiles(listFiles, new File(psArcticToolsPath, "lib" + File.separator
                  + "providerdataJar"));
         }
         for (File listFile : listFiles) {
            String sFileName = listFile.getName();
            if (sFileName.endsWith(".jar")) {
               String absolutePath =
                  listFile.getAbsolutePath().substring(psArcticToolsPath.length() + 1);
               sb.append(";" + absolutePath.replaceAll(Pattern.quote(File.separator), "/"));
            }
         }
         sb.append("\" ");

         sb.append(psClass);
         if (psParameter.length() > 0) {
            sb.append(" \"" + psParameter + "\"\n");
         }
         sb.append("\nIF ERRORLEVEL 1 EXIT 1\n");
         BufferedOutputStream bufferedOutputStream =
            new BufferedOutputStream(new FileOutputStream(f));
         bufferedOutputStream.write(sb.toString().getBytes());
         bufferedOutputStream.close();

         try {
            return executeBatch(f, psClass, pbWholeOutput);
         }
         catch (Exception ex) {
            Util.showException(ex);
         }
      }

      return null;
   }

   public static String executeBatch(File f, String psTitle, boolean pbWholeOutput)
      throws Exception
   {
      if (!f.exists()) {
         return "";
      }
      OutputStream out = null;
      try {

         ProcessBuilder processBuilder =
            new ProcessBuilder("cmd.exe", "/c", "call", "\"" + f.getAbsolutePath() + "\"");

         File parentFile = f.getParentFile();
         if (parentFile != null) {
            processBuilder.directory(parentFile);
         }

         Process process = processBuilder.start();

         out = TextAreaOutputStream.createOutputStream(psTitle, pbWholeOutput);

         // any error message?
         StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", out);

         // any output?
         StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", out);

         // kick them off
         errorGobbler.start();
         outputGobbler.start();

         outputGobbler.join();
         if (process.waitFor() == 0) {
            out.close();
            return out.toString();
         }
         throw new RuntimeException("Error during execution:\n" + out.toString());
      }
      catch (Exception e) {
         throw e;
      }
      finally {
         if (out != null) {
            try {
               out.close();
            }
            catch (IOException e) {
               // could not close
            }
         }
      }
   }

   private static void addFiles(ArrayList<File> listFiles, File pFile)
   {
      if (pFile.isDirectory()) {
         for (File file : pFile.listFiles()) {
            listFiles.add(file);
         }
      }
   }

   private static String getJavaBin(String getenv)
   {
      if (getenv != null && getenv.length() > 0) {
         File javaRoot = new File(getenv, "Java");
         File lastJavaExe = null;
         if (javaRoot.exists() && javaRoot.isDirectory()) {
            for (File javaInst : javaRoot.listFiles()) {
               File javaExe = new File(javaInst, "bin" + File.separator + "java.exe");
               if (javaExe.exists()) {
                  if (lastJavaExe == null || lastJavaExe.lastModified() < javaExe.lastModified()) {
                     lastJavaExe = javaExe;
                  }
               }
            }
         }
         if (lastJavaExe != null) {
            return lastJavaExe.getAbsolutePath();
         }
      }
      return null;
   }


}
