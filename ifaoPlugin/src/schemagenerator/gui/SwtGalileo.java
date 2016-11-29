package schemagenerator.gui;


import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.ImportGalileo;


public class SwtGalileo
   extends SwtBase
{

   private Text textAreaGalileo = null;
   private Composite compositeGalileoHelp = null;
   private Label labelGalileoHelp = null;
   private Text textGalileoHelpDirectory = null;
   private Button buttonGalileoHelpDirectory = null;

   public SwtGalileo(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      textAreaGalileo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaGalileo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaGalileo.setLayoutData(gridData);
      textAreaGalileo.setText("Enter the \'TransactionHelp\' directory, from "
            + "Galileo Help with the current Version.\nThe data.xsd and dataBinding.xml files "
            + "within net/ifao/providerdata/galileo "
            + "will be created.\nIf you want to add a new request, "
            + "add the related element within data.xsd and start again.");
      this.setLayout(gridLayout);
      createCompositeGalileoHelp();
      setSize(new Point(482, 305));
   }

   /**
    * This method initializes compositeGalileoHelp	
    *
    */
   private void createCompositeGalileoHelp()
   {
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData16 = new GridData();
      gridData16.horizontalAlignment = GridData.FILL;
      gridData16.grabExcessHorizontalSpace = true;
      GridLayout gridLayout7 = new GridLayout();
      gridLayout7.numColumns = 3;
      compositeGalileoHelp = new Composite(this, SWT.NONE);
      compositeGalileoHelp.setLayout(gridLayout7);
      compositeGalileoHelp.setLayoutData(gridData1);
      labelGalileoHelp = new Label(compositeGalileoHelp, SWT.NONE);
      labelGalileoHelp.setText("Galileo Help Directory");
      textGalileoHelpDirectory = new Text(compositeGalileoHelp, SWT.BORDER);
      textGalileoHelpDirectory.setLayoutData(gridData16);
      buttonGalileoHelpDirectory = new Button(compositeGalileoHelp, SWT.NONE);
      buttonGalileoHelpDirectory.setText("Open");
      buttonGalileoHelpDirectory.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openDirectory(textGalileoHelpDirectory);
         }
      });
   }

   @Override
   public void start(Generator generator)
   {
      String sBase = textGalileoHelpDirectory.getText();

      if (!(new File(sBase)).exists()) {
         errorMsg("ERROR in Galileo Help Path\n" + sBase + " not exist");

         return;
      }
      if (!sBase.toLowerCase().endsWith("transactionhelp")) {
         errorMsg("ERROR no transactionhelp directory\n" + sBase + "");

         return;
      }

      String sBaseArctic = generator.sBaseArctic;
      if (sBaseArctic.length() == 0) {
         errorMsg("invalid base directory\n" + sBaseArctic + "");

         return;

      }
      if (!sBaseArctic.endsWith("\\") && !sBaseArctic.endsWith("/")) {
         sBaseArctic += File.separator;
      }

      sBaseArctic += "lib" + File.separator + "providerdata" + File.separator;


      String sFolder =
         "net" + File.separator + "ifao" + File.separator + "providerdata" + File.separator
               + "galileo";
      sBaseArctic += sFolder;

      if (!(new File(sBaseArctic)).exists()) {
         errorMsg("ERROR in ArcticBase Path\n" + sBaseArctic + " not exist");

         return;
      }

      try {
         ImportGalileo.start2Import(sBase, sBaseArctic);
         infoFinished("Creation of Galileo helpFiles finished.", sFolder);
      }
      catch (Exception ex) {
         errorMsg("ERROR\n" + ex.getMessage());
         ex.printStackTrace();
      }


   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Galileo");
      String helpDir = createObject.getAttribute("helpDir");
      if (helpDir.length() == 0) {
         helpDir = "C:/Programme/Galileo International/XML Select/SDK/Help/TransactionHelp";
      }
      textGalileoHelpDirectory.setText(helpDir);
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Galileo");
      createObject.setAttribute("helpDir", textGalileoHelpDirectory.getText());

   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaGalileo.setEnabled(b);
   //      textGalileoHelpDirectory.setEnabled(b);
   //      buttonGalileoHelpDirectory.setEnabled(b);
   //   }

} //  @jve:decl-index=0:visual-constraint="10,10"
