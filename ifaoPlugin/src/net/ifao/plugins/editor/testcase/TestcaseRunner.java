package net.ifao.plugins.editor.testcase;


import ifaoplugin.Activator;

import java.io.*;

import net.ifao.plugin.preferences.PreferenceConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class TestcaseRunner
   extends Thread
{
   private String sFile2Run;
   boolean bDeleteFile2Run;
   JUnitWait unitWait;
   private Display display;


   public TestcaseRunner(Display pDisplay, String psFile2Run, boolean pbDeleteFile2Run,
                         JUnitWait pUnitWait)
   {
      sFile2Run = psFile2Run;
      bDeleteFile2Run = pbDeleteFile2Run;
      unitWait = pUnitWait;
      display = pDisplay;
   }

   public void newMessageBox(final String s)
   {
      display.asyncExec(new Runnable()
      {
         @Override
         public void run()
         {
            MessageBox messageBox =
               new MessageBox(display.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
            messageBox.setText("JUnit-Process");
            messageBox.setMessage(s);
            messageBox.open();
         }
      });

   }

   @Override
   public void run()
   {
      int indexOfJUnit = sFile2Run.indexOf("/jUnitTest/");

      String sClassPaths = "classes;bin;../common/classes;../common/bin";
      try {
         sClassPaths += ";" + Activator.getDefault().getPreferenceStore()
               .getString(PreferenceConstants.P_PATH_ADDITIONAL_CLASSES).trim();
      }
      catch (Exception ex) {
         // invalid configuration
      }
      String sBase = null;
      // ensure class for TestCaseRunner is available
      if (indexOfJUnit >= 0) {
         sBase = sFile2Run.substring(0, indexOfJUnit);
         String sClass = sBase + "/jUnitTest/net/ifao/" + TestcaseData._sBaseDir4Components
               + "/framework/TestCaseRunner.java";
         if (!new File(sClass).exists()) {
            newMessageBox("File net.ifao." + TestcaseData._sBaseDir4Components
                  + ".framework.TestCaseRunner not found\n" + "within /jUnitTest directory");
            sBase = null;
         } else {
            // validate the classPaths
            for (String sClassPath : sClassPaths.split(";")) {
               if (sClassPath.length() > 0) {
                  if (sClassPath.contains(":")) {
                     sClass = sClassPath;
                  } else {
                     sClass = sBase + "/" + sClassPath;
                  }
                  if (new File(sClass).exists()) {
                     break;
                  }
               }
            }

            if (!new File(sClass).exists()) {
               sBase = null;
               newMessageBox("classes directory not found\n" + "Ensure, that classes"
                     + " directory is set as default output (or is defined within the ifao settings)\n"
                     + "for class files within your project.");
            } else {
               sClass += "/net/ifao/" + TestcaseData._sBaseDir4Components
                     + "/framework/TestCaseRunner.class";
               if (!new File(sClass).exists()) {
                  sBase = null;
                  newMessageBox("compiled class for TestCaseRunner not found\n"
                        + "Ensure, that directory /jUnitTest is added to your build-path\n\n"
                        + "- Right click on directory /jUnitTest\n"
                        + "- Select (within popUpMenu): Build Path - Use as source folder.");
               }
            }
         }

      }
      if (sBase != null) {
         try {
            File workDir = new File(sBase).getCanonicalFile();
            StringBuilder fileOfDir = new StringBuilder();
            fileOfDir.append(getFilesOfDir(new File(workDir, "lib"), ";lib"));
            fileOfDir.append(getFilesOfDir(new File(workDir, "lib/provider"), ";lib/provider"));
            fileOfDir.append(
                  getFilesOfDir(new File(workDir, "lib/providerdataJar"), ";lib/providerdataJar"));
            fileOfDir.append(getFilesOfDir(new File(workDir, "extFiles/lib"), ";extFiles/lib"));

            fileOfDir.append(getFilesOfDir(new File(workDir, "../common/lib"), ";../common/lib"));
            fileOfDir.append(getFilesOfDir(new File(workDir, "../common/extFiles/lib"),
                  ";../common/extFiles/lib"));

            String sCommand = "@echo off\ncd \"" + workDir.getCanonicalPath()
                  + "\"\njava -classpath \"" + sClassPaths + fileOfDir + "\" " + "net.ifao."
                  + TestcaseData._sBaseDir4Components + ".framework.TestCaseRunner "
                  + "ui=text test="
                  + sFile2Run.substring(sFile2Run.indexOf("/net/ifao/arctic/agents/") + 24) + "";

            File f = JUnitWait.getTempFile("RunJUnitTest.bat");
            FileWriter fileWriter = new FileWriter(f);
            fileWriter.write(sCommand);
            fileWriter.flush();
            fileWriter.close();

            Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(f.getAbsolutePath(), null, workDir);

            ListenerThread threadListener =
               new ListenerThread(display, process.getInputStream(), unitWait);
            ListenerThread threadListenerErr =
               new ListenerThread(display, process.getErrorStream(), unitWait);

            threadListener.start();
            threadListenerErr.start();

            process.waitFor();
            threadListener.interrupt();
            threadListenerErr.interrupt();
         }
         catch (Exception e) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream s = new PrintStream(byteArrayOutputStream);
            e.printStackTrace(s);
            unitWait.addText(byteArrayOutputStream.toString());
         }
      }
      display.asyncExec(new Runnable()
      {
         @Override
         public void run()
         {
            unitWait.finished();
         }
      });
      if (bDeleteFile2Run) {
         new File(sFile2Run).delete();
      }
   }

   private String getFilesOfDir(File f, String sPre)
   {
      StringBuffer sRet = new StringBuffer();
      if (f.exists() && f.isDirectory()) {
         File[] files = f.listFiles();
         for (File file : files) {
            if (file.getName().endsWith("jar")) {
               sRet.append(sPre + "/" + file.getName());
            }
         }
      }
      return sRet.toString();
   }

   class ListenerThread
      extends Thread
   {
      private InputStream in;
      private JUnitWait unitWait;
      private Display display;

      public ListenerThread(Display pDisplay, InputStream pIn, JUnitWait pUnitWait)
      {
         in = pIn;
         unitWait = pUnitWait;
         display = pDisplay;
      }

      @Override
      public void run()
      {
         BufferedReader isr = new BufferedReader(new InputStreamReader(in));
         String line;
         try {
            while (!isInterrupted() && (line = isr.readLine()) != null) {
               addText(line);
            }
         }
         catch (Exception e) {
            addText("Exception: " + e.getMessage());
         }
      }

      void addText(final String sText)
      {
         display.asyncExec(new Runnable()
         {

            @Override
            public void run()
            {
               unitWait.addText(sText);
            }
         });

      }

   }


}
