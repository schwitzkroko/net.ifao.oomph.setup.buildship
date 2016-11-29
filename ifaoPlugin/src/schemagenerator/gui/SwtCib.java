package schemagenerator.gui;


import ifaoplugin.*;

import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import schemagenerator.Generator;
import schemagenerator.actions.ImportCib;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;


public class SwtCib
   extends SwtBase
{

   private Text textArea = null;

   public SwtCib(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.grabExcessHorizontalSpace = true;
      textArea = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textArea
            .setText("Ensure, that there is a cib schema file within the directory "
                  + "net/ifao/newcib/wsdl.\nThis file will be 'transformed' "
                  + "into a valid data.xsd file.\n\nThe xsd file can be found at e.g. 'http://10.5.1.214:8888/cib-webservice/newcib.xsd'");
      textArea.setLayoutData(gridData);
      setSize(new Point(300, 200));
      setLayout(new GridLayout());
   }


   @Override
   public void loadValuesFrom(XmlObject settings)
   {

   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {

   }

   @Override
   public void start(Generator generator)
   {
      File ffDirectory = Util.getProviderDataFile(generator.sBaseArctic, "net/ifao/newcib/wsdl");
      String sText = "";
      if (ffDirectory.exists()) {
         StringBuilder sbText = new StringBuilder();
         ImportCib.start(ffDirectory, sbText);
         sbText.append("Finished");
         sText = sbText.toString();
      } else {
         sText += "Directory " + ffDirectory.getAbsolutePath() + " does not exist";
      }
      displayText(sText);
   }

}
