package net.ifao.plugins.editor.testcase;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class TestcaseRunner
   extends Thread
{
   private String sFile2Run;
   boolean bDeleteFile2Run;
   JUnitWait unitWait;
   private Display display;
   private IProject project;


   public TestcaseRunner(IProject project, Display pDisplay, String psFile2Run, boolean pbDeleteFile2Run, JUnitWait pUnitWait)
   {
      sFile2Run = psFile2Run;
      this.project = project;
      bDeleteFile2Run = pbDeleteFile2Run;
      unitWait = pUnitWait;
      display = pDisplay;
   }

   public void newMessageBox(final String s)
   {
      display.asyncExec(() -> {
         Shell activeShell = display.getActiveShell();
         if (activeShell == null) {
            activeShell = Display.getCurrent().getActiveShell();
         }
         MessageBox messageBox = new MessageBox(activeShell, SWT.ICON_ERROR | SWT.OK);
         messageBox.setText("JUnit-Process");
         messageBox.setMessage(s);
         messageBox.open();
      });

   }

   @Override
   public void run()
   {
      int indexOfJUnit = sFile2Run.indexOf("/jUnitTest/");


      String sBase = null;
      // ensure class for TestCaseRunner is available
      if (indexOfJUnit >= 0) {

         sBase = sFile2Run.substring(0, indexOfJUnit);

         String sClass = sBase + "/jUnitTest/net/ifao/" + TestcaseData._sBaseDir4Components + "/framework/TestCaseRunner.java";
         if (!new File(sClass).exists()) {
            newMessageBox("File net.ifao." + TestcaseData._sBaseDir4Components + ".framework.TestCaseRunner not found\n"
                  + "within /jUnitTest directory");
            sBase = null;
         } else {


            List<File> classesFolder = ClassPathBuilder.getClassesFolders(project);

            File baseFolder = new File(sBase);
            try {
               baseFolder = baseFolder.getCanonicalFile();
            }
            catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }

            Set<String> duplicate = new HashSet<>();

            String sClassPaths = classesFolder.stream().distinct().filter(f -> f.isDirectory() || duplicate.add(f.getName()))
                  .map(f -> f.getAbsolutePath()).collect(Collectors.joining(";"));

            sClassPaths =
               sClassPaths.replace(baseFolder.getAbsolutePath(), ".").replace(baseFolder.getParentFile().getAbsolutePath(), "..");

            if (!classesFolder.stream().filter(f -> f.isDirectory()
                  && new File(f, "net/ifao/" + TestcaseData._sBaseDir4Components + "/framework/TestCaseRunner.class").exists())
                  .findFirst().isPresent()) {
               sBase = null;
               newMessageBox("compiled class for TestCaseRunner not found\n"
                     + "Ensure, that directory /jUnitTest is added to your build-path\n\n"
                     + "- Right click on directory /jUnitTest\n"
                     + "- Select (within popUpMenu): Build Path - Use as source folder.");
            } else if (sBase != null) {
               try {
                  File workDir = baseFolder.getCanonicalFile();

                  String sCommand = "@echo off\ncd \"" + workDir.getCanonicalPath() + "\"\njava -classpath \"" + sClassPaths
                        + "\" " + "net.ifao." + TestcaseData._sBaseDir4Components + ".framework.TestCaseRunner " + "ui=text test="
                        + sFile2Run.substring(sFile2Run.indexOf("/net/ifao/arctic/agents/") + 24) + "";

                  File f = JUnitWait.getTempFile("RunJUnitTest.bat");
                  FileWriter fileWriter = new FileWriter(f);
                  fileWriter.write(sCommand);
                  fileWriter.flush();
                  fileWriter.close();

                  Runtime runtime = Runtime.getRuntime();
                  final Process process = runtime.exec(f.getAbsolutePath(), null, workDir);

                  ListenerThread threadListener = new ListenerThread(display, process.getInputStream(), unitWait);
                  ListenerThread threadListenerErr = new ListenerThread(display, process.getErrorStream(), unitWait);

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
