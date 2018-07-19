package dtdinfo.guiswt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class DtdStatus
{

   public DtdStatus()
   {
      createSShell();
      sShell.open();
   }

   private Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
   private Label labelInfo = null;

   /**
    * This method initializes sShell
    */
   private void createSShell()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      sShell = new Shell();
      sShell.setText("Information");
      sShell.setSize(new Point(300, 64));
      sShell.setLayout(new GridLayout());
      labelInfo = new Label(sShell, SWT.NONE);
      labelInfo.setText("");
      labelInfo.setLayoutData(gridData);
   }

   public void setText(String psText)
   {
      // progressBar.setSelection(0);
      labelInfo.setText(psText);

   }

   public void close()
   {
      sShell.dispose();
   }

}
