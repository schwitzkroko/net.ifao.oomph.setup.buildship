package schemagenerator.gui;


import java.io.*;

import org.eclipse.swt.widgets.*;


public class SwtOutputStream
   extends OutputStream
{

   private Text text;
   private StringBuilder sb = new StringBuilder();
   private Display display;

   public SwtOutputStream(Text text)
   {
      display = text.getDisplay();
      this.text = text;
      // clear the text
      text.setText("");
   }

   @Override
   public void write(int iChar)
      throws IOException
   {
      if (iChar < ' ') {
         if (sb.length() > 0) {
            sb.append("\n");
            final String sText = sb.toString();
            if (!display.isDisposed()) {
               display.syncExec(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     text.append(sText);
                  }
               });
            }
            // clear the length
            sb.setLength(0);
         }
      } else {
         sb.append((char) iChar);
      }
   }

}
