package net.ifao.dialogs.swing;


import java.awt.Window;

import javax.swing.*;


/**
 * Class ConfimDialog
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class ConfimDialog
   extends JDialog
{


   /**
    * 
    */
   private static final long serialVersionUID = 6233532906996481246L;

   /**
    * Method getString
    *
    * @param frame
    * @param sTitle
    *
    * @return
    * @author Andreas Brod
    */
   public static boolean getBoolean(Window frame, String sTitle)
   {
      int showConfirmDialog = JOptionPane.showConfirmDialog(frame, sTitle, "Question",
            JOptionPane.OK_CANCEL_OPTION);

      return showConfirmDialog == JOptionPane.OK_OPTION;
   }

   public static void info(Window frame, String sTitle)
   {
      JOptionPane.showMessageDialog(frame, sTitle);
   }

}
