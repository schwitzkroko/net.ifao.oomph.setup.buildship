package schemagenerator.gui;


import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.amadeus.*;


public class SwtAmadeus
   extends SwtBase
{

   private Text textAreaAmadeus = null;
   private Composite compositeAmadeusVersion = null;
   private Label labelAdmadeusVersion = null;
   private Text textAmadeusVersion = null;
   private Text textArea = null;

   public SwtAmadeus(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      this.setSize(new Point(620, 296));
      GridData gridData11 = new GridData();
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = true;
      gridData11.grabExcessVerticalSpace = false;
      gridData11.verticalSpan = 2;
      gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      textAreaAmadeus = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaAmadeus.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaAmadeus.setLayoutData(gridData);
      textAreaAmadeus
            .setText("The help files from the amdeus homepage \""
                  + ImportAmadeus.getBaseHtmlPage()
                  + "\" will "
                  + "be loaded and ALL data.xsd(s) will be generated within the directory "
                  + "h:\\cytric_development\\new-arctic\\projects\\AmadeusHelp .\n"
                  + "After this the data.xsd files (which are allready \'known\' ... in "
                  + "directory ..\\net\\ifao\\providerdata\\amadeus) are "
                  + "copied.\nIf the request dosn\'t exist, you can copy the related data.xsd manually "
                  + "from H: to ..\\net\\ifao\\providerdata\\amadeus.\n"
                  + "Finally the changes, described within ...\\net\\ifao\\providerdata"
                  + "\\amadeus\\poweredfare\\README.txt will be executed automatically.\n\n"
                  + "The releaseVersions can be found at http://api.dev.amadeus.net/api/Marketing/APIPreviousVersions.htm");
      this.setLayout(gridLayout);
      createCompositeAmadeusVersion();
   }

   /**
    * This method initializes compositeAmadeusVersion
    *
    */
   private void createCompositeAmadeusVersion()
   {
      GridData gridData3 = new GridData();
      gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.grabExcessVerticalSpace = true;
      gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData14 = new GridData();
      gridData14.grabExcessHorizontalSpace = false;
      gridData14.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      GridLayout gridLayout6 = new GridLayout();
      gridLayout6.numColumns = 3;
      compositeAmadeusVersion = new Composite(this, SWT.NONE);
      compositeAmadeusVersion.setLayout(gridLayout6);
      compositeAmadeusVersion.setLayoutData(gridData1);
      labelAdmadeusVersion = new Label(compositeAmadeusVersion, SWT.NONE);
      labelAdmadeusVersion.setText("Amadeus Version");
      labelAdmadeusVersion.setLayoutData(gridData3);
      textAmadeusVersion = new Text(compositeAmadeusVersion, SWT.BORDER);
      textAmadeusVersion.setText("29");
      textAmadeusVersion.setLayoutData(gridData14);
      textArea = new Text(compositeAmadeusVersion, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textArea.setLayoutData(gridData2);
   }

   @Override
   public void start(Generator generator)
   {
      try {
         PrintStream outputStream = new PrintStream(new OutputStream()
         {
            StringBuilder sbLine = new StringBuilder();
            boolean bUpdate = true;

            @Override
            public void write(int b)
               throws IOException
            {
               sbLine.append((char) b);
               if (b == '\n' || b == '\r') {
                  if (bUpdate) {
                     bUpdate = false;
                     Runnable runnable = new Runnable()
                     {
                        @Override
                        public void run()
                        {
                           textArea.setText(sbLine.toString());
                           textArea.setSelection(sbLine.length());
                           bUpdate = true;
                        }
                     };
                     Display disaplayDefault = Display.getDefault();
                     if (disaplayDefault != null) {
                        disaplayDefault.asyncExec(runnable);
                     }
                  }
               }
            }
         });
         ImportAmadeus.startWindow(generator.sBaseArctic,
               Integer.parseInt(textAmadeusVersion.getText()), outputStream);
      }
      catch (Exception ex) {}

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Amadeus");
      String sText = createObject.getAttribute("version");
      if (sText.length() == 0) {
         sText = "29";
      }
      textAmadeusVersion.setText(sText);
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Amadeus");
      String version = textAmadeusVersion.getText();
      createObject.setAttribute("version", version);
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaAmadeus.setEnabled(b);
   //      textAmadeusVersion.setEnabled(b);
   //   }

} //  @jve:decl-index=0:visual-constraint="10,10"
