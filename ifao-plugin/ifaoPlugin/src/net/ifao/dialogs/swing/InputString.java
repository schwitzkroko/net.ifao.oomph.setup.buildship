package net.ifao.dialogs.swing;


import java.awt.Frame;
import javax.swing.JOptionPane;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class InputString
{
   /**
    * Method getString
    *
    * @param frame
    * @param sTitle
    * @param sDefault
    *
    * @return
    * @author Andreas Brod
    */
   public static String getString(Frame frame, String sTitle, String sDefault)
   {

      String showInputDialog = JOptionPane.showInputDialog(frame, sTitle, sDefault);

      if (showInputDialog == null)
         showInputDialog = "";

      return showInputDialog;
   }

}
