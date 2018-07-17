package dtdinfo;


import ifaoplugin.Util;
import dtdinfo.gui.*;


/**
 * Class DtdMain
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
public class DtdMain
{
   boolean packFrame = false;

   // Construct the application

   /**
    * Constructor DtdMain
    */
   public DtdMain()
   {
      DtdFrame frame = new DtdFrame(null);

      // Validate frames that have preset sizes
      // Pack frames that have useful preferred size info, e.g. from their layout
      if (packFrame) {
         frame.pack();
      } else {
         frame.validate();
      }

      frame.setVisible(true);
   }

   private static WaitThread _wt = null;

   /**
    * Method startWaitThread
    *
    * @param sTitle
    * @author $author$
    */
   public static synchronized void startWaitThread(String sTitle)
   {
      stopWaitThread();

      _wt = new WaitThread(sTitle);

      _wt.start();
   }

   /**
    * Method stopWaitThread
    * @author $author$
    */
   public static synchronized void stopWaitThread()
   {
      if (_wt != null) {
         _wt.exit();
         _wt.interrupt();

         _wt = null;
      }
   }

   // Main method

   /**
    * Method main
    *
    * @param args
    * @author $author$
    */
   public static void main(String[] args)
   {
      Util.initSwing();

      new DtdMain();
   }

   /**
    * Method strTran
    *
    * @param sReplace
    * @param sWith
    * @param sInString
    *
    * @return
    * @author Andreas Brod
    */
   public static String strTran(String sReplace, String sWith, String sInString)
   {
      String s = new String(sInString);
      int i = sInString.indexOf(sReplace);

      while (i >= 0) {
         String s1 = "";
         String s2 = "";

         if (i > 0) {
            s1 = s.substring(0, i);
         }

         if (s.length() >= i + sReplace.length()) {
            s2 = s.substring(i + sReplace.length());
         }

         s = s1 + sWith + s2;
         i = s.indexOf(sReplace);
      }

      return s;
   }

}


/**
 * Class WaitThread
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class WaitThread
   extends Thread
{
   private String text;

   private boolean bStopped = false;

   /**
    * Constructor WaitThread
    *
    * @param sText
    */
   public WaitThread(String sText)
   {
      text = sText;
   }

   public void exit()
   {
      bStopped = true;
   }

   /**
    * Method run
    * @author $author$
    */
   @Override
   public void run()
   {
      WaitDialog wd = new WaitDialog(null, text, false);

      wd.setVisible(true);

      try {
         while (!interrupted() && !bStopped) {
            sleep(100);
            wd.showTime();
         }
      }
      catch (InterruptedException ex) {}

      wd.stop();

   }
}
