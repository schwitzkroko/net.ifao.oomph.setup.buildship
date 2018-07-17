package schemagenerator.gui;


import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import schemagenerator.Generator;
import schemagenerator.actions.ImportNewCib;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;


public class SwtNewCib
   extends SwtBase
{

   private Text textArea = null;
   private Label label = null;
   private Text textWsdlFile = null;
   private Text textAreaConsole = null;

   public SwtNewCib(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData11 = new GridData();
      gridData11.horizontalSpan = 2;
      gridData11.grabExcessVerticalSpace = true;
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = true;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      GridData gridData = new GridData();
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.horizontalSpan = 2;
      gridData.grabExcessHorizontalSpace = true;
      textArea = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textArea.setText("Import the new cib schema file ");
      textArea.setLayoutData(gridData);
      label = new Label(this, SWT.NONE);
      label.setText("Label");
      textWsdlFile = new Text(this, SWT.BORDER);
      textWsdlFile.setLayoutData(gridData1);
      textAreaConsole = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textAreaConsole.setEnabled(false);
      textAreaConsole.setLayoutData(gridData11);
      this.setLayout(gridLayout);
      setSize(new Point(300, 200));
   }


   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("NewCIB");

      String sWsdl = createObject.getAttribute("url");
      if (sWsdl.length() == 0) {
         sWsdl = "http://10.5.1.214:8888/cib-webservice/NewCibSoapHttpPort?wsdl";
      }
      textWsdlFile.setText(sWsdl);
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("NewCIB");

      createObject.setAttribute("url", textWsdlFile.getText());
   }

   @Override
   public void start(Generator generator)
   {

      ImportNewCib importNewCib = new ImportNewCib(generator.sBaseArctic, textWsdlFile.getText());

      textAreaConsole.setText("");
      OutputStream swtConsoleStream = new SwtOutputStream(textAreaConsole);

      String sText = importNewCib.start(swtConsoleStream);

      if (sText.length() > 0) {
         displayText(sText);
      }

   }

}
