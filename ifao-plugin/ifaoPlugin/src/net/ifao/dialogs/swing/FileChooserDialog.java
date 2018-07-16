package net.ifao.dialogs.swing;


import java.io.File;

import javax.swing.JFileChooser;


/**
 * Class FileChooserDialog
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class FileChooserDialog
{


   public static String getDirectory(String text)
   {
      JFileChooser fc = new JFileChooser();

      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.setSelectedFile(new File(text));
      int zustand = fc.showOpenDialog(null);

      if (zustand == JFileChooser.APPROVE_OPTION) {
         File verzeichnis = fc.getSelectedFile();
         return verzeichnis.getAbsolutePath();
      }

      return "";
   }
}
