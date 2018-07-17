package schemagenerator.gui;


import java.io.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class SwtConsoleStream
   extends OutputStream
{

   boolean bActive = true;
   static Text text;
   private Display display;
   Shell shell;
   StringBuilder sbLine = new StringBuilder();

   public SwtConsoleStream()
   {
      this(new Display());
   }


   public SwtConsoleStream(Display pDisplay)
   {
      display = pDisplay;
      shell = new Shell(display);

      shell.addListener(SWT.Close, new Listener()
      {
         @Override
         public void handleEvent(Event event)
         {
            int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
            MessageBox messageBox = new MessageBox(shell, style);
            messageBox.setText("Information");
            messageBox.setMessage("Do you really want to close the console ?");
            boolean bClose = messageBox.open() == SWT.YES;
            event.doit = bClose;
            if (bClose) {
               bActive = false;
            }
         }
      });

      shell.setLayout(new FillLayout());
      shell.setText("Console");

      text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      text.setFont(new Font(display, "Courier", 10, SWT.NONE));
      text.setBackground(new Color(display, 0, 0, 80));
      text.setForeground(new Color(display, 255, 255, 80));

      shell.setSize(600, 400);
      shell.open();

   }


   @Override
   public void write(int b)
      throws IOException
   {
      synchronized (sbLine) {
         sbLine.append((char) b);
         if (b == '\n') {
            if (bActive) {
               final String sLine = sbLine.toString();
               text.append(sLine);
            }
            sbLine.setLength(0);
         }
      }
   }

   @Override
   public void close()
      throws IOException
   {
      if (!display.isDisposed()) {
         display.dispose();
      }
      super.close();
   }

   public static void main(String[] args)
   {
      SwtConsoleStream swtConsoleStream = new SwtConsoleStream();

      try {
         swtConsoleStream.run();

         swtConsoleStream.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }


   public void run()
   {
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
   }


}
