package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.*;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.*;


public abstract class SwtBase
   extends Composite
{

   private String sBaseDir;

   public SwtBase(Composite parent, int style)
   {
      super(parent, style);
   }

   public abstract void start(Generator pGenerator);

   public abstract void loadValuesFrom(XmlObject settings);

   public abstract void saveValuesTo(XmlObject settings);

   public void setActive(boolean b)
   {
      Control[] children = getChildren();
      for (Control element : children) {
         setActive(element, b);
      }
   }


   private void setActive(Control control, boolean b)
   {
      control.setEnabled(b);
      if (control instanceof Composite) {
         Control[] children = ((Composite) control).getChildren();
         for (Control element : children) {
            setActive(element, b);
         }
      }

   }

   public static void openDirectory(Text textImportDirectory2)
   {
      DirectoryDialog fd = new DirectoryDialog(textImportDirectory2.getShell(), SWT.OPEN);
      fd.setText("Open");
      String text = textImportDirectory2.getText();
      if (text.length() > 0 && new File(text).exists()) {
         fd.setFilterPath(text);
      }
      fd.setMessage("Select Directory");
      String selected = fd.open();
      if (selected != null && selected.length() > 0) {
         textImportDirectory2.setText(selected);
      }
   }

   public static boolean openFile(Text textImportDirectory2, String psFilter)
   {
      FileDialog fd = new FileDialog(textImportDirectory2.getShell(), SWT.OPEN);
      fd.setText("Open");
      String text = textImportDirectory2.getText();
      if (text.length() > 0 && new File(text).exists()) {
         fd.setFilterPath(text);
      }
      StringTokenizer st = new StringTokenizer(psFilter, ",");
      String[] filterExt = new String[st.countTokens()];
      for (int i = 0; i < filterExt.length; i++) {
         filterExt[i] = st.nextToken().trim();
      }
      if (filterExt.length > 0) {
         fd.setFilterExtensions(filterExt);
      }
      String selected = fd.open();
      if (selected != null && selected.length() > 0) {
         textImportDirectory2.setText(selected);
         return true;
      }
      return false;
   }

   void info(String string)
   {
      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
      messageBox.setText("Info");
      messageBox.setMessage(string);
      messageBox.open();
   }

   void infoFinished(String string)
   {
      infoFinished(string, "");
   }

   void infoFinished(String string, String psPackage)
   {
      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
      messageBox.setText("Info");
      String sPackage = "";
      if (psPackage.length() > 0)
         psPackage += "Package '" + psPackage.replaceAll("[\\\\/]", ".") + "'\n";
      messageBox.setMessage(sPackage + "-------------------------------------------------------\n"
            + string + "\n-------------------------------------------------------\n"
            + "You can now build the ProviderData.jar File.");
      messageBox.open();
   }

   void errorMsg(String string)
   {
      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
      messageBox.setText("Error");
      messageBox.setMessage(string);
      messageBox.open();

   }

   void errorMsg(Exception ex)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ex.printStackTrace(new PrintStream(out));
      errorMsg(out.toString());
   }

   boolean getBoolean(String string)
   {
      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
      messageBox.setText("Select");
      messageBox.setMessage(string);
      if (messageBox.open() == SWT.OK) {
         return true;
      }
      return false;
   }

   public void displayText(String psText)
   {
      Shell shell = getShell();
      Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE);

      dialog.setText("Result");
      dialog.setLayout(new GridLayout());
      dialog.setImage(UtilSwt.getImage("schemagenerator/builderBig.png", shell.getDisplay()));

      Text textAreaInfo = new Text(dialog, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaInfo.setLayoutData(gridData);

      textAreaInfo.setText(psText);

      dialog.setSize(500, 400);
      dialog.open();

   }

   public void setBaseDir(String psBaseDir)
   {
      sBaseDir = psBaseDir;
   }

   public String getBaseDir()
   {
      return sBaseDir;
   }

   public void setActive(SchemaGeneratorSwt schemaGeneratorSwt)
   {
      schemaGeneratorSwt.setActive(true);
   }
}
