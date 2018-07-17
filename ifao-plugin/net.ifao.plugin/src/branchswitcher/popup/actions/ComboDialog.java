/**
 *
 */
package branchswitcher.popup.actions;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * ComboDialog
 *
 * <p>
 * Copyright &copy; 2017, i:FAO Group GmbH.
 *
 * @author
 */
public class ComboDialog
   extends Dialog
{
   private String value;

   protected ComboDialog(Shell parentShell)
   {
      super(parentShell);
   }

   /**
    * Makes the dialog visible.
    * @param text
    *
    * @return
    */
   public String open(String text, List<String> values)
   {
      Shell parent = getParent();
      final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
      shell.setText("Please select");

      shell.setLayout(new GridLayout(2, false));

      Label label = new Label(shell, SWT.NULL);
      label.setText(text);

      final Combo combo = new Combo(shell, SWT.SINGLE | SWT.BORDER);
      combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      // add the values
      values.forEach(combo::add);

      // add an empty label
      new Label(shell, SWT.NULL).setText("");

      Composite composite = new Composite(shell, SWT.NULL);
      composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
      composite.setLayout(new GridLayout(2, true));

      final Button buttonOK = newPushButton(composite);
      buttonOK.setText("   Ok   ");
      Button buttonCancel = newPushButton(composite);
      buttonCancel.setText("   Cancel   ");

      // add listeners
      combo.addListener(SWT.Modify, event -> value = combo.getText());
      buttonOK.addListener(SWT.Selection, event -> shell.dispose());
      buttonCancel.addListener(SWT.Selection, event -> actionCancel(shell));
      shell.addListener(SWT.Traverse, this::actionTraverse);

      // init
      if (!values.isEmpty()) {
         combo.setText(values.get(0));
      } else {
         combo.setText("");
      }

      // init the screen
      shell.pack();

      Display display = parent.getDisplay();
      centerShellOnScreen(shell, display.getPrimaryMonitor().getBounds());

      shell.open();

      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }

      return value;
   }


   private Button newPushButton(Composite composite)
   {
      Button button = new Button(composite, SWT.PUSH);
      button.setSize(new Point(200, 20));
      button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      return button;
   }

   private void centerShellOnScreen(final Shell shell, Rectangle bounds)
   {
      Rectangle rect = shell.getBounds();

      int x = bounds.x + (bounds.width - rect.width) / 2;
      int y = bounds.y + (bounds.height - rect.height) / 2;

      shell.setLocation(x, y);
   }

   private void actionCancel(final Shell shell)
   {
      value = null;
      shell.dispose();
   }

   public static void main(String[] args)
   {
      Shell shell = new Shell();
      ComboDialog dialog = new ComboDialog(shell);

      try {
         List<String> collect = SecureRandom.getInstanceStrong().ints(0, 1000).limit(10).sorted().distinct()
               .mapToObj(String::valueOf).collect(Collectors.toList());
         System.out.println(dialog.open("Please enter a valid number:", collect)); // NOSONAR
      }
      catch (NoSuchAlgorithmException e) { // NOSONAR
         // should not happen on default
      }

   }

   private void actionTraverse(Event event)
   {
      if (event.detail == SWT.TRAVERSE_ESCAPE) {
         event.doit = false;
      }
   }
}
