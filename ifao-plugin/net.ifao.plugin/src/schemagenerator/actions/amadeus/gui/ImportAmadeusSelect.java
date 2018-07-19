package schemagenerator.actions.amadeus.gui;


import java.awt.*;

import javax.swing.*;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.Dimension;


public class ImportAmadeusSelect
   extends JDialog
{

   private static final long serialVersionUID = 1L;
   private JPanel jContentPane = null; //  @jve:decl-index=0:visual-constraint="10,10"
   private JLabel jLabel = null;
   private JList jList = null;
   private JPanel jPanel = null;
   private JPanel jPanel1 = null;
   private JPanel jPanel2 = null;
   private JButton jButtonClear = null;
   private JButton jButtonExit = null;
   private JCheckBox jCheckBoxViewAll = null;
   private JScrollPane jScrollPane = null;

   /**
    * @param owner
    */
   public ImportAmadeusSelect(Frame owner)
   {
      super(owner, true);
      initialize();
   }


   /**
    * This method initializes this
    *
    * @return void
    */
   private void initialize()
   {
      this.setSize(800, 600);
      this.setTitle("Select Amadeus Requests");
      this.setContentPane(getJContentPane());
   }

   /**
    * This method initializes jContentPane
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJContentPane()
   {
      if (jContentPane == null) {
         jLabel = new JLabel();
         jLabel.setText("Please select the requests, which have to be copied to the amadeus lib directory");
         jContentPane = new JPanel();
         jContentPane.setLayout(new BorderLayout());
         jContentPane.setSize(new Dimension(484, 241));
         jContentPane.add(jLabel, BorderLayout.NORTH);
         jContentPane.add(getJPanel(), BorderLayout.SOUTH);
         jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
      }
      return jContentPane;
   }


   /**
    * This method initializes jList
    *
    * @return javax.swing.JList
    */
   JList getJList()
   {
      if (jList == null) {
         jList = new JList();

      }
      return jList;
   }


   /**
    * This method initializes jPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel()
   {
      if (jPanel == null) {
         jPanel = new JPanel();
         jPanel.setLayout(new BorderLayout());
         jPanel.add(getJPanel1(), BorderLayout.CENTER);
         jPanel.add(getJPanel2(), BorderLayout.EAST);
      }
      return jPanel;
   }


   /**
    * This method initializes jPanel1
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel1()
   {
      if (jPanel1 == null) {
         jPanel1 = new JPanel();
         jPanel1.setLayout(new BorderLayout());
         jPanel1.add(getJButtonClear(), BorderLayout.WEST);
         jPanel1.add(getJCheckBoxViewAll(), BorderLayout.CENTER);
      }
      return jPanel1;
   }


   /**
    * This method initializes jPanel2
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel2()
   {
      if (jPanel2 == null) {
         jPanel2 = new JPanel();
         jPanel2.setLayout(new BorderLayout());
         jPanel2.add(getJButtonExit(), BorderLayout.NORTH);
      }
      return jPanel2;
   }


   /**
    * This method initializes jButtonClear
    *
    * @return javax.swing.JButton
    */
   public JButton getJButtonClear()
   {
      if (jButtonClear == null) {
         jButtonClear = new JButton();
         jButtonClear.setText("Clear all requests");
      }
      return jButtonClear;
   }


   /**
    * This method initializes jButtonExit
    *
    * @return javax.swing.JButton
    */
   public JButton getJButtonExit()
   {
      if (jButtonExit == null) {
         jButtonExit = new JButton();
         jButtonExit.setText("Copy selected resources");

      }
      return jButtonExit;
   }


   /**
    * This method initializes jCheckBoxViewAll
    *
    * @return javax.swing.JCheckBox
    */
   public JCheckBox getJCheckBoxViewAll()
   {
      if (jCheckBoxViewAll == null) {
         jCheckBoxViewAll = new JCheckBox();

         jCheckBoxViewAll.setText("View all possible requests");
      }
      return jCheckBoxViewAll;
   }


   /**
    * This method initializes jScrollPane
    *
    * @return javax.swing.JScrollPane
    */
   private JScrollPane getJScrollPane()
   {
      if (jScrollPane == null) {
         jScrollPane = new JScrollPane();
         jScrollPane.setViewportView(getJList());
      }
      return jScrollPane;
   }

}
