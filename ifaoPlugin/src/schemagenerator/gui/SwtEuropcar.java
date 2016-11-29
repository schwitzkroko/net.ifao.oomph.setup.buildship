package schemagenerator.gui;


import ifaoplugin.*;

import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;


/**
 * Class SwtCaro
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtEuropcar
   extends SwtBase
{

   private static final String ECDIRECTORY = "net/ifao/providerdata/europcar/xml"; //  @jve:decl-index=0:
   private Text textAreaInfo = null;
   private Text textArea = null;

   /**
    * Constructor SwtCaro
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtEuropcar(Composite pParent, int pStyle)
   {
      super(pParent, pStyle);
      initialize();
   }

   /**
    * This method initializes this
    * 
    */
   private void initialize()
   {
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      this.setLayout(new GridLayout());
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo.setLayoutData(gridData);
      textAreaInfo.setText("Ensure, that within the Europcar directory " + ECDIRECTORY
            + ", all eurocar *.xsd files and the dtds.txt contains "
            + "the latest files (from the latest Europcar document).");
      textArea = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textArea.setFont(new Font(Display.getDefault(), "Courier New", 8, SWT.NORMAL));
      textArea.setLayoutData(gridData1);
   }

   /**
    * Method loadValuesFrom
    * overrides @see schemagenerator.gui.SwtBase#loadValuesFrom(net.ifao.xml.XmlObject)
    *
    * @param pSettings
    *
    * @author kaufmann
    */
   @Override
   public void loadValuesFrom(XmlObject pSettings)
   {
      // nothing to add
   }

   /**
    * Method saveValuesTo
    * overrides @see schemagenerator.gui.SwtBase#saveValuesTo(net.ifao.xml.XmlObject)
    *
    * @param pSettings
    *
    * @author kaufmann
    */
   @Override
   public void saveValuesTo(XmlObject pSettings)
   {
      // nothing to add
   }

   /**
    * Method start
    * overrides @see schemagenerator.gui.SwtBase#start(schemagenerator.Generator)
    *
    * @param pGenerator
    *
    * @author kaufmann
    */
   @Override
   public void start(Generator pGenerator)
   {

      File file = Util.getProviderDataFile(pGenerator.sBaseArctic, ECDIRECTORY);
      if (file.exists()) {
         //         ImportEuropcar importEuropcar = new ImportEuropcar(file.getAbsolutePath());
         //         textArea.setText(importEuropcar.start());
         ImportDirectory importDirectory = new ImportDirectory(file.getAbsolutePath());
         textArea.setText(importDirectory.start());
         infoFinished("Created files within\n" + file.getAbsolutePath(), ECDIRECTORY);
      }

   }

}
