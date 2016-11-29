package schemagenerator.gui;


import java.io.*;
import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.ImportAmadeusWsdl;


public class SwtAmadeusWsdl
   extends SwtBase
{

   private Text textAreaAmadeus = null;
   private Composite compositeAmadeusVersion = null;

   private Text text;


   public SwtAmadeusWsdl(Composite parent, int style)
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
            .setText("With this page you may extract the zip file (which should be within the directory lib/provider...data/com/amadeus).\n\n"
                  + "- The related data.xsd files (within the subdirectories) will be created.\n"
                  + "- The bindings.xjb files will be created.\n"
                  + "- Common data.xsd files will be created (for same elements)");
      this.setLayout(gridLayout);
      createCompositeAmadeusVersion();
   }

   /**
    * This method initializes compositeAmadeusVersion
    *
    */
   private void createCompositeAmadeusVersion()
   {
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout6 = new GridLayout();
      compositeAmadeusVersion = new Composite(this, SWT.NONE);
      compositeAmadeusVersion.setLayout(gridLayout6);
      compositeAmadeusVersion.setLayoutData(gridData1);

      text =
         new Text(compositeAmadeusVersion, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL
               | SWT.CANCEL | SWT.MULTI);
      text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
   }

   @Override
   public void start(Generator generator)
   {
      try {

         PrintStream outputStream = new PrintStream(new SwtOutputStream(text));
         ImportAmadeusWsdl importAmadeusWsdl =
            new ImportAmadeusWsdl(generator.sBaseArctic, outputStream);
         importAmadeusWsdl.run();
         infoFinished("Related data.xsd files generated", "com.amadeus.xml");
      }
      catch (Exception ex) {
         errorMsg(ex);
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      settings.createObject("AmadeusWsdl");

   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      settings.createObject("AmadeusWsdl");

   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaAmadeus.setEnabled(b);
   //      textAmadeusVersion.setEnabled(b);
   //   }

} //  @jve:decl-index=0:visual-constraint="10,10"
