package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;

import schemagenerator.Generator;
import schemagenerator.actions.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


public class SwtSixt
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite composite = null;
   private Label labelWsdl = null;
   private Text textWsdlFile = null;
   private Label labelSelected = null;
   private List list = null;
   private Composite compositeJAvaCom = null;
   private Label labelJavaCom = null;
   private Text textJavaCom = null;
   private Button buttonOpen = null;
   private Button buttonLoadFromURL = null;

   public SwtSixt(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessVerticalSpace = true;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo.setText("Enter a list of valid WsdlFiles");
      textAreaInfo.setLayoutData(gridData);
      createComposite();
      setSize(new Point(300, 200));
      setLayout(new GridLayout());
      labelSelected = new Label(this, SWT.NONE);
      labelSelected.setText("Selected Wsdl files:");
      list = new List(this, SWT.NONE);
      list.setLayoutData(gridData2);
      createCompositeJAvaCom();
      list.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            // make nothing
         }
      });
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      composite = new Composite(this, SWT.NONE);
      composite.setLayoutData(gridData1);
      composite.setLayout(gridLayout);
      labelWsdl = new Label(composite, SWT.NONE);
      labelWsdl.setText("Sixt url (with wsdl files)");
      textWsdlFile = new Text(composite, SWT.BORDER);
      textWsdlFile.setText("https://www.sixt.de/webservices/reservation/wsdl/prod/");
      textWsdlFile.setLayoutData(gridData3);
      buttonLoadFromURL = new Button(composite, SWT.NONE);
      buttonLoadFromURL.setText("Load from URL");
      buttonLoadFromURL.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sBaseUrl = textWsdlFile.getText();
            try {
               String sHtmlResponse = UtilSwt.loadFromURL(sBaseUrl);
               list.removeAll();
               int iHighestNumber = 0;
               int iPos = sHtmlResponse.indexOf("<a href");
               while (iPos > 0) {
                  int iStart = sHtmlResponse.indexOf("=\"", iPos) + 2;
                  int iEnd = sHtmlResponse.indexOf("\"", iStart);
                  String sRequest = sHtmlResponse.substring(iStart, iEnd);
                  int iNumber = getNumber(sRequest);
                  if (iNumber > iHighestNumber) {
                     list.removeAll();
                     iHighestNumber = iNumber;
                     list.add(sBaseUrl + sRequest);
                  } else if (iNumber == iHighestNumber) {
                     list.add(sBaseUrl + sRequest);
                  }
                  iPos = sHtmlResponse.indexOf("<a href", iPos + 1);
               }

            }
            catch (Exception e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
            // try to find the java communication
            File file =
               new File(Generator.getDefaultPath(),
                     "src/net/ifao/arctic/agents/sixt/wsdl/framework/communication/SixtCommunication.java");
            if (file.exists())
               textJavaCom.setText(file.getAbsolutePath());
         }
      });
   }

   protected int getNumber(String sRequest)
   {
      if (!sRequest.endsWith(".wsdl"))
         return 0;
      StringTokenizer st = new StringTokenizer(sRequest, "_.");
      if (st.countTokens() > 3)
         try {
            st.nextToken();
            return Integer.parseInt(st.nextToken()) * 1000 + Integer.parseInt(st.nextToken());
         }
         catch (Exception ex) {
            // error 
         }
      return 0;
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      list.removeAll();
      XmlObject createObject = settings.createObject("Wsdl");
      XmlObject[] objects = createObject.getObjects("File");
      for (int i = 0; i < objects.length; i++) {
         list.add(objects[i].getAttribute("url"));
      }
      if (objects.length > 0) {
         list.select(0);
      }
      textJavaCom.setText(createObject.getAttribute("javaClass"));
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject wsdl = settings.createObject("Wsdl");
      String[] items = list.getItems();
      wsdl.deleteObjects("");
      for (int i = 0; i < items.length; i++) {
         wsdl.createObject("File", "url", items[i], true);
      }
      wsdl.setAttribute("javaClass", textJavaCom.getText());
   }

   @Override
   public void start(Generator generator)
   {
      String javaCom = textJavaCom.getText();
      if (!(new File(javaCom)).exists()) {
         errorMsg("ERROR JavaFile not existent\n" + javaCom);
         return;
      }
      if (list.getItemCount() == 0) {
         errorMsg("No wsdl Files within list");
         return;

      }
      DataWsdl data = new DataWsdl();
      String[] sUrl = list.getItems();
      HashSet<String> hsJavaCommunication = new HashSet<String>();
      String sJavaCommunication = "";
      String sImportDirs = "";
      for (int i = 0; i < sUrl.length; i++) {
         data.setUrl(sUrl[i]);
         data.setJavaCommunication(javaCom);
         ImportWsdls importWsdls = new ImportWsdls(data);
         StringTokenizer st = new StringTokenizer(importWsdls.saveJavaCommunication(sUrl[i]), "\n");
         while (st.hasMoreTokens()) {
            String nextToken = st.nextToken().trim();
            if (nextToken.startsWith("*") && nextToken.endsWith("/data.xsd")) {
               String sNewDir = nextToken.substring(1, nextToken.lastIndexOf("/")).trim() + "\n";
               if (sImportDirs.indexOf(sNewDir) < 0)
                  sImportDirs += sNewDir;
            } 
            hsJavaCommunication.add(nextToken);
         }
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
    * This method initializes compositeJAvaCom	
    *
    */
   private void createCompositeJAvaCom()
   {
      GridData gridData5 = new GridData();
      gridData5.grabExcessHorizontalSpace = true;
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 3;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      compositeJAvaCom = new Composite(this, SWT.NONE);
      compositeJAvaCom.setLayoutData(gridData4);
      compositeJAvaCom.setLayout(gridLayout1);
      labelJavaCom = new Label(compositeJAvaCom, SWT.NONE);
      labelJavaCom.setText("Java Communication");
      textJavaCom = new Text(compositeJAvaCom, SWT.BORDER);
      textJavaCom.setLayoutData(gridData5);
      buttonOpen = new Button(compositeJAvaCom, SWT.NONE);
      buttonOpen.setText("Open");
      buttonOpen.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            if (openFile(textJavaCom, "*Communication.java,*.java")) {
               String[] lstUrls = ImportWsdls.getUrlsFromCommunication(textJavaCom.getText());
               if (lstUrls.length > 0) {
                  list.removeAll();
                  for (int i = 0; i < lstUrls.length; i++) {
                     list.add(lstUrls[i]);
                  }
               }
            }
         }
      });
   }

}
