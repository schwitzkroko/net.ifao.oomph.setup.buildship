package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;


/**
 * Class DtdSchemaChanged
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
public class DtdSchemaChanged
   extends JDialog
{

   /**
    *
    */
   private static final long serialVersionUID = -8623295655316883057L;
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JButton jButton1 = new JButton();
   JPanel jPanel3 = new JPanel();
   BorderLayout borderLayout2 = new BorderLayout();
   JLabel jLabel1 = new JLabel();
   BorderLayout borderLayout3 = new BorderLayout();
   JButton jButtonXML = new JButton();
   JButton jButtonXSD = new JButton();
   JButton jButton4 = new JButton();
   boolean bContinue = false;
   String sArcticPnrElementInfosXml = "";
   String sArcticPnrElementInfosXsd = "";
   String sGeneratedPnrElementInfos = "";

   /**
    * Constructor DtdSchemaChanged
    *
    * @param frame
    * @param title
    * @param psArcticPnrElementInfos
    * @param psGeneratedPnrElementInfos
    */
   public DtdSchemaChanged(Frame frame, String title, String psArcticPnrElementInfosXml,
                           String psArcticPnrElementInfosXsd, String psGeneratedPnrElementInfos)
   {
      super(frame, title, true);

      sArcticPnrElementInfosXml = psArcticPnrElementInfosXml;
      sArcticPnrElementInfosXsd = psArcticPnrElementInfosXsd;
      sGeneratedPnrElementInfos = psGeneratedPnrElementInfos;

      try {
         jbInit();
         pack();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      setLocation(
            (int) Math.max(0, frame.getLocation().getX() + (frame.getWidth() - getWidth()) / 2),
            (int) Math.max(0, frame.getLocation().getY() + (frame.getHeight() - getHeight()) / 2));

   }

   /**
    * Method hasContinue
    *
    * @return
    * @author Andreas Brod
    */
   public boolean hasContinue()
   {
      return bContinue;
   }

   /**
    * Method show
    *
    * @param bXml
    * @param bXsd
    * @author $author$
    */
   public void show(boolean bXml, boolean bXsd)
   {
      jButtonXML.setEnabled(bXml);
      jButtonXSD.setEnabled(bXsd);
      setVisible(true);
   }

   /**
    * Constructor DtdSchemaChanged
    */
   public DtdSchemaChanged()
   {
      this(null, "", "", "", "");
   }

   /**
    * Method jbInit
    *
    * @throws Exception
    * @author $author$
    */
   private void jbInit()
      throws Exception
   {
      panel1.setLayout(borderLayout1);
      jButton1.setText("Continue");
      jButton1.addActionListener(new DtdSchemaChanged_jButton1_actionAdapter(this));
      jPanel2.setLayout(borderLayout2);
      jPanel1.setLayout(borderLayout3);
      jLabel1.setToolTipText("");
      jLabel1.setText("ArcticPnrElementInfos will be changed");
      jButtonXML.setText("Compare XML");
      jButtonXML.addActionListener(new DtdSchemaChanged_jButtonXML_actionAdapter(this));
      jButtonXSD.setText("Compare XSD");
      jButtonXSD.addActionListener(new DtdSchemaChanged_jButtonXSD_actionAdapter(this));
      jButton4.setText("Cancel");
      jButton4.addActionListener(new DtdSchemaChanged_jButton4_actionAdapter(this));
      getContentPane().add(panel1);
      panel1.add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jButtonXML, BorderLayout.NORTH);
      jPanel1.add(jButtonXSD, BorderLayout.SOUTH);
      panel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(jPanel3, BorderLayout.EAST);
      jPanel3.add(jButton1, null);
      jPanel3.add(jButton4, null);
      panel1.add(jLabel1, BorderLayout.NORTH);
   }

   /**
    * Method jButton1_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButton1_actionPerformed(ActionEvent e)
   {
      bContinue = true;

      setVisible(false);
   }

   /**
    * Method jButton4_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButton4_actionPerformed(ActionEvent e)
   {
      setVisible(false);
   }

   /**
    * Method jButtonXML_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButtonXML_actionPerformed(ActionEvent e)
   {
      diffPnrElementInfos("xml");
   }

   /**
    * Method jButtonXSD_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButtonXSD_actionPerformed(ActionEvent e)
   {
      diffPnrElementInfos("xsd");
   }

   /**
    * Method diffPnrElementInfos
    *
    * @param sExtension
    * @author Andreas Brod
    */
   public void diffPnrElementInfos(String sExtension)
   {
      String sArcticPnrElementInfos =
         sExtension.endsWith("xml") ? sArcticPnrElementInfosXml : sArcticPnrElementInfosXsd;

      // search the environment
      for (String sKey : System.getenv().keySet()) {
         if (sKey.toLowerCase().startsWith("programfiles")) {
            String cProgramme = System.getenv(sKey);
            if (new File(cProgramme + "\\ExamDiff Pro\\ExamDiff.exe").exists()) {
               Util.exec("start \"ExamDiff\" \"" + cProgramme + "\\ExamDiff Pro\\ExamDiff.exe\" \""
                     + sArcticPnrElementInfos + "\" \"" + sGeneratedPnrElementInfos + sExtension
                     + "\"", false);
               return;
            }
         }
      }
      // not found
      Util.exec("start notepad \"" + sGeneratedPnrElementInfos + sExtension + "\"", false);

   }


}


/**
 * Class DtdSchemaChanged_jButton1_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdSchemaChanged_jButton1_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdSchemaChanged adaptee;

   /**
    * Constructor DtdSchemaChanged_jButton1_actionAdapter
    *
    * @param adaptee
    */
   DtdSchemaChanged_jButton1_actionAdapter(DtdSchemaChanged adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButton1_actionPerformed(e);
   }
}


/**
 * Class DtdSchemaChanged_jButton4_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdSchemaChanged_jButton4_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdSchemaChanged adaptee;

   /**
    * Constructor DtdSchemaChanged_jButton4_actionAdapter
    *
    * @param adaptee
    */
   DtdSchemaChanged_jButton4_actionAdapter(DtdSchemaChanged adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButton4_actionPerformed(e);
   }
}


/**
 * Class DtdSchemaChanged_jButtonXML_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdSchemaChanged_jButtonXML_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdSchemaChanged adaptee;

   /**
    * Constructor DtdSchemaChanged_jButtonXML_actionAdapter
    *
    * @param adaptee
    */
   DtdSchemaChanged_jButtonXML_actionAdapter(DtdSchemaChanged adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonXML_actionPerformed(e);
   }
}


/**
 * Class DtdSchemaChanged_jButtonXSD_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdSchemaChanged_jButtonXSD_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdSchemaChanged adaptee;

   /**
    * Constructor DtdSchemaChanged_jButtonXSD_actionAdapter
    *
    * @param adaptee
    */
   DtdSchemaChanged_jButtonXSD_actionAdapter(DtdSchemaChanged adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonXSD_actionPerformed(e);
   }
}
