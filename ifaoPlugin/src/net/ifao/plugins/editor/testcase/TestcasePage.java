package net.ifao.plugins.editor.testcase;


import ifaoplugin.*;

import java.io.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;


public class TestcasePage
   extends EditorPart
{

   private TestcaseData composite = null;
   private IFile _iFileTsc, _iFileTscJava;
   private boolean _dirty = false;
   private IFile _iFileSrc;
   private boolean _bIsDirectTsc = false;

   @Override
   public void doSave(IProgressMonitor monitor)
   {
      report("doSave()");
      if (composite == null || !_dirty)
         return;

      String xmlString = composite.getXmlString(true);
      UtilSwt.writeFile(_iFileTsc, xmlString, monitor);
      if (!_iFileTscJava.exists()) {
         UtilSwt.writeFile(_iFileTscJava, getTscJava(), monitor);


      }
      validateTestCase(_iFileTscJava, monitor);

      setDirty(false);
   }

   private IFile getPath2AllTests(IFile iFile, File parentFile)
   {
      // validate if "AllTests" exists
      File[] files = parentFile.listFiles();
      String sAllTests = "";
      for (int i = 0; sAllTests.length() == 0 && i < files.length; i++) {
         if (files[i].getName().startsWith("AllTests")) {
            sAllTests = files[i].getName();
         }
      }
      String sParent = "/" + parentFile.getName() + "/";
      String sPath = "/" + iFile.getProjectRelativePath().toString();
      sPath = sPath.substring(1, sPath.lastIndexOf(sParent) + sParent.length());
      String sProvider = "";
      int iProvider = sPath.indexOf("net/ifao/arctic/agents/");
      if (iProvider > 0) {
         sProvider = sPath.substring(iProvider + 23) + "/";
         sProvider = sProvider.substring(0, sProvider.indexOf("/"));
      }
      if (sAllTests.length() == 0) {
         // create a AllTestsFile
         if (sProvider.length() == 0) {
            sAllTests = "AllTests.java";
         } else if (parentFile.getParentFile().getName().equals("agents")) {
            sAllTests = "AllTests" + Util.getCamelCase(sProvider) + ".java";
         } else {
            sAllTests = "AllTests" + Util.getCamelCase(sProvider)
                  + Util.getCamelCase(parentFile.getName()) + ".java";
         }
      }
      sPath += sAllTests;

      return iFile.getProject().getFile(sPath);
   }

   private void validateTestCase(IFile iFile, IProgressMonitor monitor)
   {
      File parentFile = iFile.getLocation().toFile().getParentFile();

      IFile fileDetailProvider = getPath2AllTests(iFile, parentFile);

      String sDetail = readAllTests(fileDetailProvider, iFile, false);

      // find detail Provider
      if (sDetail.length() > 0)

         UtilSwt.writeFile(fileDetailProvider, sDetail, monitor);

      // add this fileDetailProvider to root-ProviderSuite
      while (parentFile != null && parentFile.getParentFile() != null
            && !parentFile.getName().equals("agents")
            && !parentFile.getParentFile().getName().equals("agents")) {
         parentFile = parentFile.getParentFile();
      }

      IFile fileRootProvider = getPath2AllTests(fileDetailProvider, parentFile);
      sDetail = readAllTests(fileRootProvider, fileDetailProvider, true);
      if (sDetail.length() > 0)
         UtilSwt.writeFile(fileRootProvider, sDetail, monitor);

      while (parentFile != null && !parentFile.getName().equals("net")) {

         parentFile = parentFile.getParentFile();
      }
      if (parentFile != null) {
         parentFile = parentFile.getParentFile();

         IFile fileRootMain = getPath2AllTests(fileRootProvider, parentFile);
         sDetail = readAllTests(fileRootMain, fileRootProvider, true);
         if (sDetail.length() > 0)
            UtilSwt.writeFile(fileRootMain, sDetail, monitor);


      }


   }

   private String readAllTests(IFile iFile, IFile iRoot, boolean runSuite)
   {
      String sDetail = "";
      if (iFile.exists()) {
         try {
            StringBuffer sb = new StringBuffer();
            BufferedInputStream in = new BufferedInputStream(iFile.getContents());
            byte[] ch = new byte[4096];
            int count;
            while ((count = in.read(ch)) > 0) {
               sb.append(new String(ch, 0, count));
            }
            in.close();
            sDetail = sb.toString();
         }
         catch (Exception e) {}
      }


      if (sDetail.length() == 0) {
         sDetail = getTscAllTests(iFile);
      }

      String sName = iRoot.getName();
      sName = sName.substring(0, sName.lastIndexOf("."));

      String sPath = iRoot.getFullPath().toString().replaceAll("\\\\", "/");
      sPath = sPath.substring(sPath.indexOf("/net/") + 1, sPath.lastIndexOf("/"));
      String sPackage = sPath.replaceAll("\\/", ".");
      int unitEnd = sDetail.indexOf("//$JUnit-END$");
      if (sDetail.indexOf(sName) < 0 && unitEnd > 0) {
         if (runSuite) {
            // unitEnd = sDetail.indexOf("return ", unitEnd);
            sDetail = sDetail.substring(0, unitEnd) + "suite.addTest(" + sPackage + "." + sName
                  + ".suite());" + "\n      " + sDetail.substring(unitEnd);
         } else {
            sDetail = sDetail.substring(0, unitEnd) + "suite.addTestSuite(" + sPackage + "."
                  + sName + ".class);" + "\n      " + sDetail.substring(unitEnd);

         }
         return sDetail;
      }

      return "";

   }

   private String getTscJava()
   {
      StringBuffer sb = new StringBuffer();
      String sPath = _iFileTscJava.getFullPath().toString().replaceAll("\\\\", "/");
      if (sPath.indexOf("jUnitTest/") >= 0) {
         String sName = _iFileTscJava.getName();
         sName = sName.substring(0, sName.lastIndexOf("."));
         sPath = sPath.substring(sPath.indexOf("jUnitTest/"), sPath.lastIndexOf("/"));
         // ignore "jUnitTest/" from path for package and replace /
         String sPackage = sPath.substring(10).replaceAll("\\/", ".");
         sb.append("package " + sPackage + ";\n");
         sb.append("\n");
         sb.append("\n");
         sb.append("import java.io.File;\n");
         sb.append("\n");
         sb.append("\n");
         sb.append("public class " + sName + "\n");
         sb.append("   extends net.ifao.ruletest.framework.TestCaseRunner\n");
         sb.append("{\n");
         sb.append("   private File _file = null;\n");
         sb.append("\n");
         sb.append("   @Override\n");
         sb.append("   public File getTsc()\n");
         sb.append("   {\n");
         sb.append("      if (_file == null)\n");
         sb.append("         _file = new File(\"" + sPath + "/" + _iFileTsc.getName() + "\");\n");
         sb.append("      return _file;\n");
         sb.append("   }\n");
         sb.append("}\n");

      }
      return sb.toString();

   }

   private String getTscAllTests(IFile iFile)
   {
      StringBuffer sb = new StringBuffer();
      String sPath = iFile.getFullPath().toString().replaceAll("\\\\", "/");
      if (sPath.indexOf("jUnitTest/") >= 0) {
         String sName = iFile.getName();
         sName = sName.substring(0, sName.lastIndexOf("."));
         sPath = sPath.substring(sPath.indexOf("jUnitTest/"), sPath.lastIndexOf("/"));
         String sPackage = sPath.length() > 9 ? sPath.substring(10).replaceAll("\\/", ".") : "";
         if (sPackage.length() > 0) {
            sb.append("package " + sPackage + ";\n");
            sPackage = "all new arctic tests";
         }
         sb.append("\n");
         sb.append("import junit.framework.Test;\n");
         sb.append("import junit.framework.TestSuite;\n");
         sb.append("\n");
         sb.append("public class " + sName + "\n");
         sb.append("{\n");
         sb.append("\n");
         sb.append("   public static Test suite()\n");
         sb.append("   {\n");
         sb.append("      TestSuite suite = new TestSuite(\n");
         sb.append("         \"Test for " + sPackage + "\");\n");
         sb.append("      //$JUnit-BEGIN$\n");
         sb.append("      //$JUnit-END$\n");
         sb.append("      return suite;\n");
         sb.append("   }\n");
         sb.append("}\n");

      }
      return sb.toString();

   }

   public void setDirty(boolean bDirty)
   {
      report("setDirty()");
      if (_dirty != bDirty) {
         _dirty = bDirty;
         firePropertyChange(PROP_DIRTY);
      }
   }

   @Override
   public void dispose()
   {
      if (composite != null)
         composite.dispose();
      super.dispose();
   }

   @Override
   public void doSaveAs()
   {
      report("doSaveAs()");
      // setDirty(true);
      doSave(null);
   }

   @Override
   public void init(IEditorSite site, IEditorInput input)
      throws PartInitException
   {
      report("init()");
      setSite(site);

      FileEditorInput fileEditorInput = ((FileEditorInput) input);
      // switch to tsc-file (if java is selected)
      IPath path = fileEditorInput.getPath();

      _iFileTsc = fileEditorInput.getFile();

      IProject project = _iFileTsc.getProject();
      String sPath = _iFileTsc.getProjectRelativePath().toString();

      if (path.getFileExtension().equalsIgnoreCase("java")) {
         sPath = "jUnitTest" + sPath.substring(sPath.indexOf("/"), sPath.length() - 4) + "tsc";
         _iFileTsc = project.getFile(sPath);
      } else {
         _bIsDirectTsc = true;
      }

      String sPathSrc = "src" + sPath.substring(sPath.indexOf("/"), sPath.lastIndexOf(".") + 1)
            + "java";

      _iFileSrc = project.getFile(sPathSrc);

      _iFileTscJava = project.getFile(sPath.substring(0, sPath.lastIndexOf(".")) + "TscTest.java");

      ISelectionProvider provider = new ISelectionProvider()
      {

         private ISelection selection;

         @Override
         public void addSelectionChangedListener(ISelectionChangedListener listener)
         {
            report("addSelectionChangedListener()");

         }

         @Override
         public ISelection getSelection()
         {
            report("getSelection()");
            return selection;
         }

         @Override
         public void removeSelectionChangedListener(ISelectionChangedListener listener)
         {
            report("removeSelectionChangedListener()");

         }

         @Override
         public void setSelection(ISelection selection)
         {
            report("setSelection()");
            this.selection = selection;

         }

      };

      provider.setSelection(new ISelection()
      {

         @Override
         public boolean isEmpty()
         {
            return false;
         }

      });
      site.setSelectionProvider(provider);
      getSite();
   }

   @Override
   public boolean isDirty()
   {
      return _dirty;
   }

   @Override
   public boolean isSaveAsAllowed()
   {
      return false;
   }

   @Override
   public void createPartControl(Composite parent)
   {
      report("createPartControl()");
      if (_iFileTsc != null) {
         File file = _iFileTsc.getLocation().toFile();
         composite = new TestcaseData(this, parent, ifaoplugin.Activator.class, file, _bIsDirectTsc);
      }

   }

   @Override
   public void setFocus()
   {
      report("setFocus()");
      if (composite == null)
         return;
      composite.setFocus();

   }

   public void reload()
   {
      report("reload()");
      if (composite == null)
         return;
      composite.reload();
   }

   private void report(String psText)
   {
   // System.out.println(psText);
   }


   public void openSrc()
   {
      try {
         IJavaElement el = (IJavaElement) _iFileSrc.getAdapter(IJavaElement.class);
         org.eclipse.jdt.ui.JavaUI.openInEditor(el);
      }
      catch (Exception e) {
         e.printStackTrace();

      }
   }

}
