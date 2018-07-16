package schemagenerator.gui;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import schemagenerator.Generator;
import schemagenerator.actions.ImportSabre;


public class SwtSabre
   extends SwtBase
{

   private Text textAreaSabre = null;
   private Composite compositeSabreWsdl = null;
   private Label labelSabreWsdl = null;
   private Text textSabreWsdl = null;
   private Text soapAction;

   public SwtSabre(Composite parent, int style)
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
      this.setLayout(gridLayout);
      setSize(new Point(300, 200));
      textAreaSabre = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaSabre.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaSabre.setLayoutData(gridData);
      textAreaSabre
            .setText("Please enter the location of the current Sabre Wsdl or xsd file.\n"
                  + "Xsd files will be stored in an specific directory and for this you have to add the soap action !");
      createCompositeSabreWsdl();
   }

   /**
    * This method initializes compositeSabreWsdl
    *
    */
   private void createCompositeSabreWsdl()
   {
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData19 = new GridData();
      gridData19.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData19.grabExcessHorizontalSpace = true;
      GridLayout gridLayout8 = new GridLayout();
      gridLayout8.numColumns = 2;
      compositeSabreWsdl = new Composite(this, SWT.NONE);
      compositeSabreWsdl.setLayout(gridLayout8);
      compositeSabreWsdl.setLayoutData(gridData1);
      labelSabreWsdl = new Label(compositeSabreWsdl, SWT.NONE);
      labelSabreWsdl.setText("Sabre Wsdl/Xsd File");
      textSabreWsdl = new Text(compositeSabreWsdl, SWT.BORDER);
      textSabreWsdl.setLayoutData(gridData19);

      Label lblSpecificSoapAction = new Label(compositeSabreWsdl, SWT.NONE);
      lblSpecificSoapAction.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lblSpecificSoapAction.setText("Specific Soap Action");

      soapAction = new Text(compositeSabreWsdl, SWT.BORDER);
      soapAction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      //textSabreDefaultVersion.setLayoutData(gridData19);

   }

   @Override
   public void start(Generator generator)
   {
      String sWsdl = textSabreWsdl.getText();
      if (sWsdl.length() > 0) {
         String sText;
         try {
            sText = ImportSabre.importWsdl(generator.sBaseArctic, sWsdl, soapAction.getText());
         }
         catch (Exception ex) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream s = new PrintStream(out);
            ex.printStackTrace(s);
            sText = out.toString();
         }
         infoFinished(sText);
      } else {
         errorMsg("Please enter valid Wsdl url");
      }
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Sabre");
      textSabreWsdl.setText(createObject.getAttribute("wsdl"));
      soapAction.setText(createObject.getAttribute("soapAction"));
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Sabre");
      createObject.setAttribute("wsdl", textSabreWsdl.getText());
      createObject.setAttribute("soapAction", soapAction.getText());
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaSabre.setEnabled(b);
   //      textSabreWsdl.setEnabled(b);
   //      checkBoxCreateJar.setEnabled(b);
   //   }

   public void setWsdlFile(String psWsdlFile)
   {
      textSabreWsdl.setText(psWsdlFile);
   }
}
