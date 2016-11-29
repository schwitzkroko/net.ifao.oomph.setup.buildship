package schemagenerator.gui;


import ifaoplugin.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import schemagenerator.Generator;
import schemagenerator.actions.ImportWsdlFile;
import schemagenerator.actions.Utils;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class SwtWsdlJaxB
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite composite_1;
   private Label labelWsdl = null;
   private Button buttonAddWsdl = null;
   private Label label = null;
   private Text textNameSpace = null;
   private Composite composite2 = null;
   private Button buttonAdd = null;
   private Text textWsdlFile = null;
   private Combo combo;
   private Label lblVersionPattern;
   private Text versionPattern;
   private Object _sLastNamesSpace;
   private Composite composite;
   private Button btnUse;

   public SwtWsdlJaxB(Composite parent, int style)
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
            .setText("Enter a wsdl file and click on analyse. This will define the namespace, list other schema files (for this namespace) "
                  + "and shows the entered version pattern. With this pattern, it is possible, to remove version numbers, from the file names. "
                  + "(This is usedfull, if you want to update different versions, to one schema)\n\n"
                  + "If you press 'start' the wsdl file will be loaded and the related schema files will be created.\n"
                  + "After this, you may continue to create the ProviderData.jar file.");
      textAreaInfo.setLayoutData(gridData);
      createComposite();
      setSize(new Point(518, 418));
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
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      GridData gridData1 = new GridData();
      gridData1.heightHint = 221;
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      composite_1 = new Composite(this, SWT.NONE);
      composite_1.setLayoutData(gridData1);
      composite_1.setLayout(gridLayout);
      labelWsdl = new Label(composite_1, SWT.NONE);
      labelWsdl.setText("WsdlFile");
      createComposite2();
      createComposite1();
      buttonAddWsdl = new Button(composite_1, SWT.NONE);
      buttonAddWsdl.setText("Analyse");
      buttonAddWsdl.setLayoutData(gridData7);
      label = new Label(composite_1, SWT.NONE);
      label.setText("NameSpace");
      textNameSpace = new Text(composite_1, SWT.BORDER);
      textNameSpace.setEditable(false);
      textNameSpace.setLayoutData(gridData2);

      Label lblOtherSchemaFiles = new Label(composite_1, SWT.NONE);
      lblOtherSchemaFiles.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lblOtherSchemaFiles.setText("Other Schema files");

      composite = new Composite(composite_1, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

      combo = new Combo(composite, SWT.NONE);
      combo.setEnabled(false);
      combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

      btnUse = new Button(composite, SWT.NONE);
      btnUse.setEnabled(false);
      btnUse.addSelectionListener(new SelectionAdapter()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            int selectionIndex = combo.getSelectionIndex();
            if (selectionIndex >= 0) {
               String sItem = combo.getItem(selectionIndex);
               textWsdlFile.setText(sItem);
            }
         }
      });
      btnUse.setText("Use");

      lblVersionPattern = new Label(composite_1, SWT.NONE);
      lblVersionPattern.setToolTipText("The following pattern will be executed to the filenames.");
      lblVersionPattern.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
      lblVersionPattern.setText("Version Pattern");

      versionPattern = new Text(composite_1, SWT.BORDER | SWT.MULTI);
      GridData gd_versionPattern = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
      gd_versionPattern.heightHint = 64;
      versionPattern.setLayoutData(gd_versionPattern);
      buttonAddWsdl.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            analyse(null);
         }
      });
   }

   public void analyse(String sBaseArctic)
   {
      String sWsdl = "";
      String sUrl = textWsdlFile.getText();
      try {
         sWsdl = Utils.load(sUrl);
      }
      catch (IOException e1) {
         return;
      }
      if (sWsdl.length() > 0) {
         try {
            Matcher matcher = Pattern.compile("https?://([^:^/]*).*?").matcher(sUrl);
            if (matcher.find()) {
               String sNamesSpace = "";

               StringTokenizer st2 = new StringTokenizer(matcher.group(1), ".");
               while (st2.hasMoreTokens()) {
                  String nextToken2 = st2.nextToken();
                  if (sNamesSpace.length() > 0) {
                     sNamesSpace = "." + sNamesSpace;
                  }
                  sNamesSpace = nextToken2 + sNamesSpace;
               }

               if (sNamesSpace != null) {
                  textNameSpace.setText(sNamesSpace.toLowerCase());

                  // import the files
                  File targetDirectory =
                     Util.getProviderDataFile(Generator.getDefaultPath(), sNamesSpace.toLowerCase()
                           .replace(".", "/"));
                  combo.removeAll();
                  try {
                     XmlObject wsdlInfo =
                        new XmlObject(new File(targetDirectory, "wsdl.info.xml")).getFirstObject();
                     HashSet<String> hs = new HashSet<String>();
                     hs.add(sUrl);
                     for (XmlObject urlObject : wsdlInfo.getObjects("url")) {
                        String sName = urlObject.getAttribute("name");
                        if (hs.add(sName)) {
                           combo.add(sName);
                        }
                     }
                     StringBuilder sbVersions = new StringBuilder();
                     for (XmlObject version : wsdlInfo.createObject("pattern")
                           .getObjects("version")) {
                        if (sbVersions.length() > 0)
                           sbVersions.append("\n");
                        sbVersions.append(version.getAttribute("value"));
                     }
                     if (!sNamesSpace.equals(_sLastNamesSpace)) {
                        _sLastNamesSpace = sNamesSpace;
                        versionPattern.setText(sbVersions.toString());
                     }
                  }
                  catch (Exception ex) {
                     // invalid
                  }
                  boolean bEnabled = combo.getItemCount() > 0;
                  btnUse.setEnabled(bEnabled);
                  combo.setEnabled(bEnabled);
                  if (bEnabled) {
                     combo.select(0);
                  }

                  if (sBaseArctic != null) {
                     ArrayList<String> lstVersions = new ArrayList<String>();
                     StringTokenizer st = new StringTokenizer(versionPattern.getText(), "\n\r");
                     while (st.hasMoreTokens()) {
                        lstVersions.add(st.nextToken());
                     }
                     new ImportWsdlFile(targetDirectory, sNamesSpace.toLowerCase(),
                           lstVersions.toArray(new String[0])).importUrl(sUrl);
                  }
               }
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
      XmlObject createObject = settings.createObject("WsdlJaxB");
      textWsdlFile.setText(createObject.getAttribute("wsdlFile"));
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject wsdl = settings.createObject("WsdlJaxB");
      wsdl.setAttribute("wsdlFile", textWsdlFile.getText());
   }

   @Override
   public void start(Generator generator)
   {

      analyse(generator.sBaseArctic);

      // analyse the wsdl file


      displayText("Finished");
   }

   /**
    * This method initializes composite1	
    *
    */
   private void createComposite1()
   {}

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
      composite2 = new Composite(composite_1, SWT.NONE);
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
