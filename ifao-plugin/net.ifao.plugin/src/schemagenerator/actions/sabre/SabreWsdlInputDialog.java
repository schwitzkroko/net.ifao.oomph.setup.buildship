package schemagenerator.actions.sabre;


import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;


public class SabreWsdlInputDialog
{

   private Shell sShell = null;
   private Composite composite = null;
   private Label label1 = null;
   private Label label2 = null;
   public Text textVersion = null;
   public Text textValue2 = null;
   private Text textArea = null;
   private Composite composite1 = null;
   private Button buttonOK = null;
   private Button buttonCancel = null;
   public boolean OK = false;

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      composite = new Composite(sShell, SWT.NONE);
      composite.setLayout(gridLayout);
      composite.setLayoutData(gridData2);
      label1 = new Label(composite, SWT.NONE);
      label1.setText("Version");
      textVersion = new Text(composite, SWT.BORDER);
      textVersion.setText("2003A.TsabreXML1.7.1");
      textVersion.setLayoutData(gridData1);
      label2 = new Label(composite, SWT.NONE);
      label2.setText("Label");
      textValue2 = new Text(composite, SWT.BORDER);
      textValue2.setLayoutData(gridData);
   }

   /**
    * This method initializes composite1	
    *
    */
   private void createComposite1()
   {
      GridLayout gridLayout2 = new GridLayout();
      gridLayout2.numColumns = 2;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.END;
      gridData4.grabExcessVerticalSpace = true;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      composite1 = new Composite(sShell, SWT.NONE);
      composite1.setLayoutData(gridData4);
      composite1.setLayout(gridLayout2);
      buttonOK = new Button(composite1, SWT.NONE);
      buttonOK.setText("OK");
      buttonOK.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            OK = true;
            sShell.dispose();
         }
      });
      buttonCancel = new Button(composite1, SWT.NONE);
      buttonCancel.setText("Cancel");
      buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            sShell.dispose();
         }
      });
   }

   /**
       * @param args
       */
   public static void main(String[] args)
   {
      SabreWsdlInputDialog show = SabreWsdlInputDialog.show();

      System.out.println("result:" + show.OK);
   }

   public static SabreWsdlInputDialog show()
   {

      /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
       * for the correct SWT library path in order to run with the SWT dlls. 
       * The dlls are located in the SWT plugin jar.  
       * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
       *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
       */

      Display display = Display.getDefault();
      SabreWsdlInputDialog thisClass = new SabreWsdlInputDialog();
      thisClass.createSShell();
      thisClass.sShell.open();
      while (!thisClass.sShell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
      return thisClass;
   }

   /**
    * This method initializes sShell
    */
   private void createSShell()
   {
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 1;
      sShell = new Shell();
      sShell.setText("Wsdl Input Parameters");
      sShell.setLayout(gridLayout1);
      sShell.setSize(new Point(300, 200));
      textArea = new Text(sShell, SWT.MULTI | SWT.WRAP);
      textArea.setText("Dies ist ein Test\nNeue Zeile");
      textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textArea.setLayoutData(gridData3);
      createComposite();
      createComposite1();
   }

}
