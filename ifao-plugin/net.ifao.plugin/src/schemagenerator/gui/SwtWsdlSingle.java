package schemagenerator.gui;


import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


public class SwtWsdlSingle
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite composite = null;
   private Label labelWsdl = null;
   private Button buttonAddWsdl = null;
   private Label label = null;
   private Text textNameSpace = null;
   private Label label1 = null;
   private Composite composite1 = null;
   private Label label2 = null;
   private Text textJavaProject = null;
   private Label label3 = null;
   private Text textCommunication = null;
   private Composite composite2 = null;
   private Button buttonAdd = null;
   private Text textWsdlFile = null;

   public SwtWsdlSingle(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo
            .setText("Enter a wsdl file and click on analyse. This will define the namespace.\n"
                  + "If you press start \n- the data.xsd will be created.\n"
                  + "- the Communication file within the arcticProject has to exist and will be modified\n"
                  + "\nPay attention, that the file /arctic/lib/build/BuildAllBatchFiles.bat has to be modified and started, to create related provider datas");
      textAreaInfo.setLayoutData(gridData);
      createComposite();
      setSize(new Point(300, 200));
      setLayout(new GridLayout());
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData7 = new GridData();
      gridData7.horizontalSpan = 2;
      gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      GridData gridData6 = new GridData();
      gridData6.grabExcessVerticalSpace = true;
      gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      composite = new Composite(this, SWT.NONE);
      composite.setLayoutData(gridData1);
      composite.setLayout(gridLayout);
      labelWsdl = new Label(composite, SWT.NONE);
      labelWsdl.setText("WsdlFile");
      createComposite2();
      label1 = new Label(composite, SWT.NONE);
      label1.setText("arctic Project");
      createComposite1();
      buttonAddWsdl = new Button(composite, SWT.NONE);
      buttonAddWsdl.setText("Analyse");
      buttonAddWsdl.setLayoutData(gridData7);
      label = new Label(composite, SWT.NONE);
      label.setText("NameSpace");
      textNameSpace = new Text(composite, SWT.BORDER);
      textNameSpace.setEditable(false);
      textNameSpace.setLayoutData(gridData2);
      label3 = new Label(composite, SWT.NONE);
      label3.setText("Communication");
      textCommunication = new Text(composite, SWT.BORDER);
      textCommunication.setEditable(false);
      textCommunication.setLayoutData(gridData6);
      buttonAddWsdl.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            analyse();
         }
      });
   }

   public void analyse()
   {
      String sWsdl = "";
      try {
         sWsdl = Utils.load(textWsdlFile.getText());
      }
      catch (IOException e1) {
         return;
      }
      if (sWsdl.length() > 0) {
         try {
            XmlObject xmlObject = new XmlObject(sWsdl).getFirstObject();
            String sTargetNamespace = xmlObject.getAttribute("targetNamespace");
            StringTokenizer st = new StringTokenizer(sTargetNamespace, "/");
            String sNamesSpace = null;
            while (st.hasMoreTokens()) {
               String nextToken = st.nextToken();
               if (sNamesSpace == null) {
                  sNamesSpace = "";
               } else if (sNamesSpace.length() == 0) {
                  StringTokenizer st2 = new StringTokenizer(nextToken, ".");
                  while (st2.hasMoreTokens()) {
                     String nextToken2 = st2.nextToken();
                     if (sNamesSpace.length() > 0) {
                        sNamesSpace = "." + sNamesSpace;
                     }
                     sNamesSpace = nextToken2 + sNamesSpace;
                  }
               } else {
                  sNamesSpace += "." + nextToken;
               }
            }
            if (sNamesSpace != null) {
               textNameSpace.setText(sNamesSpace.toLowerCase());
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("WsdlSingle");
      textWsdlFile.setText(createObject.getAttribute("wsdlFile"));
      textJavaProject.setText(createObject.getAttribute("javaProject"));
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject wsdl = settings.createObject("WsdlSingle");
      wsdl.setAttribute("wsdlFile", textWsdlFile.getText());
      wsdl.setAttribute("javaProject", textJavaProject.getText());
   }

   @Override
   public void start(Generator generator)
   {
      analyse();
      File frameworkCommunications =
         new File(generator.sBaseArctic, "src/net/ifao/arctic/agents/"
               + textJavaProject.getText().replaceAll("\\.", "/") + "/framework/communication");
      if (!frameworkCommunications.exists()) {
         errorMsg("ERROR frameworkCommunication not existent\n"
               + frameworkCommunications.getAbsolutePath());
         return;
      }

      String javaCom = "";
      File[] listFiles = frameworkCommunications.listFiles();
      for (int i = 0; i < listFiles.length; i++) {
         if (listFiles[i].getName().endsWith("Communication.java")) {
            javaCom = listFiles[i].getAbsolutePath();
         }
      }

      if (javaCom.length() == 0) {
         errorMsg("ERROR *Communication.java not found\n"
               + frameworkCommunications.getAbsolutePath());
         return;
      }
      textCommunication.setText(javaCom);

      DataWsdl data = new DataWsdl();
      String sUrl = textWsdlFile.getText();
      HashSet<String> hsJavaCommunication = new HashSet<String>();
      String sJavaCommunication = "";
      String sImportDirs = "";
      data.setUrl(sUrl);
      data.setJavaCommunication(javaCom);
      ImportWsdls importWsdls = new ImportWsdls(data);
      String saveJavaCommunication = importWsdls.saveJavaCommunication(sUrl);
      StringTokenizer st = new StringTokenizer(saveJavaCommunication, "\n");
      while (st.hasMoreTokens()) {
         String nextToken = st.nextToken().trim();
         if (nextToken.startsWith("*") && nextToken.endsWith("/data.xsd")) {
            String sNewDir = nextToken.substring(1, nextToken.lastIndexOf("/")).trim() + "\n";
            if (sImportDirs.indexOf(sNewDir) < 0) {
               sImportDirs += sNewDir;
            }
         }
         hsJavaCommunication.add(nextToken);
      }


      Object[] array = hsJavaCommunication.toArray();
      Arrays.sort(array);
      for (int i = 0; i < array.length; i++) {
         sJavaCommunication += array[i] + "\n";
      }
      StringTokenizer stImportDirs = new StringTokenizer(sImportDirs, "\n");
      while (stImportDirs.hasMoreTokens()) {
         String sImportDir = stImportDirs.nextToken();
         if (sImportDir.length() > 0) {

            ImportXml.clearImportDir(sImportDir);
            ImportXml.startBuild(sImportDir, false, true, "", true, true);

            ImportWsdls.correctCommunication(sImportDir, javaCom);
         }
      }

      displayText(sJavaCommunication);
   }

   /**
    * Method buildDirectoryExistsBelowLib checks, if a build directory exists below the lib
    * directory. If yes, the new providerData concept is used (no generation of java-files, no
    * generation of batch files)
    *
    * @param psJavaCom path to the java communication
    * @return true, if the build directoy exists below the lib directory
    *
    * @author kaufmann
    */
   private boolean buildDirectoryExistsBelowLib(String psJavaCom)
   {
      int iSrcStart = psJavaCom.indexOf("src/net/ifao") + psJavaCom.indexOf("src\\net\\ifao") + 1;

      if (iSrcStart > 0) {
         String sBuildDir = psJavaCom.substring(0, iSrcStart) + "lib" + File.separator + "build";
         if (new File(sBuildDir).exists()) {
            return true;
         }
      }

      return false;
   }

   /**
    * This method initializes composite1	
    *
    */
   private void createComposite1()
   {
      GridData gridData5 = new GridData();
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData5.grabExcessHorizontalSpace = true;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 2;
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.grabExcessHorizontalSpace = true;
      composite1 = new Composite(composite, SWT.NONE);
      composite1.setLayoutData(gridData4);
      composite1.setLayout(gridLayout1);
      label2 = new Label(composite1, SWT.NONE);
      label2.setText("net.ifao.arctic.agents.");
      textJavaProject = new Text(composite1, SWT.BORDER);
      textJavaProject.setLayoutData(gridData5);
   }

   /**
    * This method initializes composite2	
    *
    */
   private void createComposite2()
   {
      GridData gridData8 = new GridData();
      gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData8.grabExcessHorizontalSpace = true;
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
      GridLayout gridLayout2 = new GridLayout();
      gridLayout2.numColumns = 2;
      composite2 = new Composite(composite, SWT.NONE);
      composite2.setLayout(gridLayout2);
      composite2.setLayoutData(gridData3);
      textWsdlFile = new Text(composite2, SWT.BORDER);
      textWsdlFile.setLayoutData(gridData8);
      buttonAdd = new Button(composite2, SWT.NONE);
      buttonAdd.setText("Open");
      buttonAdd.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textWsdlFile, "*.wsdl");
         }
      });
   }

}
