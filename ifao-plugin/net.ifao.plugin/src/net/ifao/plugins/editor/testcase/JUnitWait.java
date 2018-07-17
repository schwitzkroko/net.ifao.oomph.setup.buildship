package net.ifao.plugins.editor.testcase;


import ifaoplugin.Util;

import java.io.*;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;


/**
 * The class JUnitWait was automatically genereated with Xml2Swt.
 * It uses the related JUnitWaitAdapter, which will be recreated
 * whith Xml2Swt. Change should only be done within JUnitWait.java
 * <p>
 * Copyright &copy; 2006, i:FAO
 *
 * @author generator
 */
public class JUnitWait
   extends JUnitWaitAdapter
{
   static final java.io.File fileExamDiff;
   static final String[] examDiffTempfiles;
   private static File tempJUnit;

   static {
      String sTempFolder = "C:\\temp";
      File foundFile = new java.io.File("C:\\Programme\\ExamDiff Pro\\ExamDiff.exe");
      for (String sKey : System.getenv().keySet()) {
         if (sKey.equalsIgnoreCase("temp") || sKey.equalsIgnoreCase("tmp")) {
            sTempFolder = System.getenv(sKey);
         } else if (sKey.toLowerCase().startsWith("programfiles")) {
            File fProgramme = new File(System.getenv(sKey));
            if (fProgramme.exists()) {
               for (File file : fProgramme.listFiles()) {
                  if (file.isDirectory()) {
                     File fileProg = new File(file, "ExamDiff.exe");
                     if (fileProg.exists()) {
                        foundFile = fileProg;
                        break;
                     }
                  }
               }
            }
         }
      }
      fileExamDiff = foundFile;
      sTempFolder += "\\JUnit";
      tempJUnit = new File(sTempFolder);
      if (!tempJUnit.exists()) {
         tempJUnit.mkdirs();
      }
      examDiffTempfiles =
         new String[]{ getTempFile("expected.txt").getAbsolutePath(),
               getTempFile("was.txt").getAbsolutePath() };
   }

   /**
    * Constructor for JUnitWait, which can be enhanced.
    *
    * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
    **/
   public JUnitWait(Class pAbstractUIPlugin, String psFileName)
   {
      super(pAbstractUIPlugin);
      psFileName = psFileName.replaceAll("\\\\", "/");
      // Call the initAdapter-method, which creates the components
      initAdapter();
      int iJUnit = psFileName.indexOf("/jUnitTest/");
      int lastIndexOf = psFileName.lastIndexOf("/");
      if (lastIndexOf > 0) {
         if (iJUnit > 0) {
            getTextPackage().setText(
                  psFileName.substring(iJUnit + 11, lastIndexOf).replaceAll("\\/", "."));
         }
         getTextTestcase().setText(psFileName.substring(lastIndexOf + 1));
      }
      getButtonExamDiff().setVisible(fileExamDiff.exists());
      getButtonExamDiff().setToolTipText("Compare with ExamDiff");

   }


   public static File getTempFile(String string)
   {
      return new File(tempJUnit, string);
   }


   /**
    * Main Method which starts JUnitWait in a SWT-window.
    *
    * @param args String arguments (from console)
    **/
   public static void main(String[] args)
   {
      new JUnitWait(null, "").show();
   }

   @Override
   protected void initTextDetail(Text pDetail)
   {
      try {
         GridData layoutData = (GridData) pDetail.getLayoutData();
         layoutData.minimumHeight = 200;
         layoutData.minimumWidth = 500;
      }
      catch (Exception e) {}
   }

   @Override
   protected void modifyTextPackage(Text pTextPackage)
   {
      // add modify functionallity
   }

   @Override
   protected void modifyTextTestcase(Text pTextTestcase)
   {
      // add modify functionallity
   }

   @Override
   protected void modifyTextDetail(Text pTextDetail)
   {
      // add modify functionallity
   }

   public void addText(String s)
   {
      getTextDetail().append(s + "\n");
   }

   public void finished()
   {
      getButtonOK().setEnabled(true);
      // get the text
      String sText = getTextDetail().getText();
      int iButWas = sText.lastIndexOf("> but was:<");
      try {
         if (iButWas > 0) {
            getButtonExamDiff().setEnabled(true);

            int iExpected = sText.lastIndexOf("expected:<") + 10;
            int iEnd = sText.lastIndexOf("******");
            iEnd = sText.lastIndexOf(">", iEnd);

            Util.writeToFile(examDiffTempfiles[0], sText.substring(iExpected, iButWas) + "\n");
            Util.writeToFile(examDiffTempfiles[1], sText.substring(iButWas + 11, iEnd) + "\n");

         }
      }
      catch (Exception e) {
         // make nothing
         Testcase.showError(e, sShell);
      }
   }


   @Override
   protected void clickButtonOK(Button pButtonOK)
   {
      close();
   }


   @Override
   protected void clickButtonExamDiff(Button pButtonExamDiff)
   {
      Runtime runtime = Runtime.getRuntime();
      Process process;
      try {
         process =
            runtime.exec("\"" + fileExamDiff.getAbsolutePath() + "\" \"" + examDiffTempfiles[0]
                  + "\" \"" + examDiffTempfiles[1] + "\"", null, fileExamDiff.getParentFile());
         process.waitFor();
      }
      catch (Exception e) {
         // print any exception
         e.printStackTrace();
         Testcase.showError(e, sShell);
      }

   }
}
