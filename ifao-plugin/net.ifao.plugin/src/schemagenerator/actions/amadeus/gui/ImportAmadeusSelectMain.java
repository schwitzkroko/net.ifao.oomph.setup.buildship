package schemagenerator.actions.amadeus.gui;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 * This is the main class for a select dialog
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class ImportAmadeusSelectMain
{

   /**
    * Inner class for  CheckBoxItem
    *
    * <p>
    * Copyright &copy; 2010, i:FAO
    *
    * @author brod
    */
   static class CheckBoxItem
   {
      private boolean _bAvail;
      private boolean _isChecked;
      private String _sPath;
      private String _sText;

      /**
       * Constructor CheckBoxItem
       *
       * @param psText Text
       * @param pbChecked true if Checked (default)
       * @param psVersion Version number
       *
       * @author brod
       */
      public CheckBoxItem(String psText, boolean pbChecked, String psVersion)
      {
         _bAvail = pbChecked;
         _isChecked = pbChecked;
         _sPath = psText;
         _sText = psText + psVersion;
      }

      /**
       * @return path
       *
       * @author brod
       */
      public String getPath()
      {
         return _sPath;
      }

      /**
       * @return true if aviable
       *
       * @author brod
       */
      public boolean isAvailable()
      {
         return _bAvail;
      }

      /**
       * @return true if checked
       *
       * @author brod
       */
      public boolean isChecked()
      {
         return _isChecked;
      }

      /**
       * This method sets the checked flag
       *
       * @param pbIsChecked true if checked
       *
       * @author brod
       */
      public void setChecked(boolean pbIsChecked)
      {
         _isChecked = pbIsChecked;
      }

      /**
       * @return Text
       *
       * @author brod
       */
      @Override
      public String toString()
      {
         return _sText;
      }
   }

   /**
    * Inner class CheckBoxJList
    *
    * <p>
    * Copyright &copy; 2010, i:FAO
    *
    * @author brod
    */
   static class CheckBoxJList
      extends JList
      implements ListSelectionListener
   {

      /**
       * generated serialVersionUID
       */
      private static final long serialVersionUID = 8223954439941091382L;


      /**
       * Constructor for CheckBoxJList
       */
      public CheckBoxJList()
      {
         super();
         setCellRenderer(new CheckBoxListCellRenderer());
         addListSelectionListener(this);
      }

      /**
       * The method valueChanged has to be implemented for
       * ListSelectionListener
       *
       * @param pListSelectionEvent ListSelectionEvent
       *
       * @author brod
       */
      @Override
      public void valueChanged(ListSelectionEvent pListSelectionEvent)
      {
         // make nothing
      }

   }

   /**
    * inner class CheckBoxListCellRenderer
    *
    * <p>
    * Copyright &copy; 2010, i:FAO
    *
    * @author brod
    */
   static class CheckBoxListCellRenderer
      extends JComponent
      implements ListCellRenderer
   {

      /**
       * generated serialVersionUID
       */
      private static final long serialVersionUID = -4488639410467259010L;

      private JCheckBox _checkbox;
      private DefaultListCellRenderer _defaultComp;

      /**
       * Constructor CheckBoxListCellRenderer
       *
       * @author brod
       */
      public CheckBoxListCellRenderer()
      {
         setLayout(new BorderLayout());
         _defaultComp = new DefaultListCellRenderer();
         _checkbox = new JCheckBox();
         add(_checkbox, BorderLayout.WEST);
         add(_defaultComp, BorderLayout.CENTER);
      }

      /**
       * This method returns the ListCellRendererComponent
       *
       * @param pJList list
       * @param pObject value object
       * @param piIndex index number
       * @param pbIsSelected true if is Selected
       * @param pbCellHasFocus true if cellHasFocus
       * @return Selected component
       *
       * @author brod
       */
      @Override
      public Component getListCellRendererComponent(JList pJList, Object pObject, int piIndex,
                                                    boolean pbIsSelected, boolean pbCellHasFocus)
      {
         _defaultComp.getListCellRendererComponent(pJList, pObject.toString(), piIndex,
               pbIsSelected, pbCellHasFocus);

         CheckBoxItem checkBoxItem = (CheckBoxItem) pObject;
         boolean checked = checkBoxItem.isChecked();
         _checkbox.setSelected(checked);
         Component[] comps = getComponents();
         for (Component comp : comps) {
            if (checked) {
               if (checkBoxItem.isAvailable()) {
                  comp.setForeground(_listForeground);
               } else {
                  comp.setForeground(_listForeground2);
               }
            } else {
               if (checkBoxItem.isAvailable()) {
                  comp.setForeground(_listForegroundX);
               } else {
                  comp.setForeground(_listForegroundX2);
               }
            }
            comp.setBackground(_listBackground);
         }
         return this;
      }
   }

   private static DefaultListModel _defModel;
   private static ImportAmadeusSelect _dialog;
   private static HashSet<String> _hsSelected = new HashSet<String>();
   private static Hashtable<File, File> _htCopyDirectories;
   private static JCheckBox _jCheckBoxViewAll;
   private static JList _jList;
   private static Color _listForeground, _listForegroundX, _listBackground, _listForeground2,
         _listForegroundX2;

   /**
    * static method to init the colors
    *
    * @author brod
    */
   static {
      _listForeground = Color.BLACK;
      _listForeground2 = Color.RED;
      _listForegroundX = new Color(200, 200, 200);
      _listForegroundX2 = new Color(255, 200, 200);
      _listBackground = Color.WHITE;
   }


   /**
    * This method closes the Dialog
    *
    * @author brod
    */
   protected static void closeDialog()
   {
      for (int i = 0; i < _defModel.size(); i++) {
         CheckBoxItem item = (CheckBoxItem) _defModel.get(i);
         if (item.isChecked()) {
            _hsSelected.add(item.getPath());
         }
      }
      File[] files = _htCopyDirectories.keySet().toArray(new File[0]);
      for (int i = 0; i < files.length; i++) {
         if (!_hsSelected.contains(getPath(files[i]))) {
            _htCopyDirectories.remove(files[i]);
         }
      }

      _dialog.setVisible(false);
      _dialog.dispose();
   }

   /**
    * This method retur the Path to a file
    *
    * @param pFile file
    * @return path to this file
    *
    * @author brod
    */
   private static String getPath(File pFile)
   {
      String absolutePath = pFile.getAbsolutePath().replaceAll("[\\\\/]+", ".");
      int lastIndexOf = absolutePath.lastIndexOf(".XML.");
      if (lastIndexOf > 0) {
         absolutePath = absolutePath.substring(lastIndexOf + 5);
      }
      return absolutePath;
   }

   /**
    * private method to returns the Version
    *
    * @param pFile file
    * @return proxy version of this file
    *
    * @author brod
    */
   private static String getVersion(File pFile)
   {
      if (!pFile.exists()) {
         return "???";
      }
      try {
         BufferedReader reader = new BufferedReader(new FileReader(pFile));
         String sLine;
         while ((sLine = reader.readLine()) != null) {
            if (sLine.contains("/Proxy")) {
               sLine = sLine.substring(sLine.indexOf("/Proxy") + 6) + "/";
               reader.close();
               return sLine.substring(0, sLine.indexOf("/"));
            }

            if (sLine.contains(":schema")) {
               reader.close();
               return "???";
            }
         }
         reader.close();
      }
      catch (Exception e) {
         // should never happen
         e.printStackTrace();
      }
      return "???";
   }

   /**
    * This is the main method "select"
    *
    * @param phtCopyDirectories Hashtable of CopyDirectories
    *
    * @author brod
    */
   public static void select(Hashtable<File, File> phtCopyDirectories)
   {
      _htCopyDirectories = phtCopyDirectories;
      _dialog = new ImportAmadeusSelect(null);

      // init the dialog
      _jList = _dialog.getJList();
      _jList.setCellRenderer(new CheckBoxListCellRenderer());

      // init the model
      // changes the model
      viewFiles(true);

      _jList.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent me)
         {
            int selectedIndex = _jList.locationToIndex(me.getPoint());
            if (selectedIndex < 0) {
               return;
            }
            CheckBoxItem item = (CheckBoxItem) _jList.getModel().getElementAt(selectedIndex);
            item.setChecked(!item.isChecked());
            _jList.setSelectedIndex(selectedIndex);
            _jList.repaint();
         }
      });

      _jCheckBoxViewAll = _dialog.getJCheckBoxViewAll();
      _jCheckBoxViewAll.addItemListener(new java.awt.event.ItemListener()
      {
         @Override
         public void itemStateChanged(java.awt.event.ItemEvent e)
         {
            viewFiles(!_jCheckBoxViewAll.isSelected());
         }
      });

      // add actions
      _dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      _dialog.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent we)
         {
            closeDialog();
         }
      });

      _dialog.getJButtonExit().addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent e)
         {
            closeDialog();
         }
      });

      _dialog.getJButtonClear().addActionListener(new java.awt.event.ActionListener()
      {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent e)
         {
            for (int i = 0; i < _defModel.size(); i++) {
               CheckBoxItem item = (CheckBoxItem) _defModel.get(i);
               item.setChecked(false);
            }
            //jList.setModel(defModel);
            _jList.repaint();
         }
      });
      // open the dialog
      _dialog.setVisible(true);
   }

   /**
    * private method to update the Files
    *
    * @param pbOnlyExisting if true, only existing files will be displayed
    *
    * @author brod
    */
   private static void viewFiles(boolean pbOnlyExisting)
   {
      _defModel = new DefaultListModel();
      File[] arrFiles = _htCopyDirectories.keySet().toArray(new File[0]);
      Arrays.sort(arrFiles);
      for (File arrFile : arrFiles) {
         String absolutePath = getPath(arrFile);
         File file2 = _htCopyDirectories.get(arrFile);
         if (!pbOnlyExisting || file2.exists()) {
            String sVersion = "";
            if (file2.exists()) {
               File file = new File(file2, "data.xsd");
               sVersion = " (" + getVersion(file) + ")";
            }
            _defModel.addElement(new CheckBoxItem(absolutePath, sVersion.length() > 0, sVersion));

         }
      }

      _jList.setModel(_defModel);

   }

}
