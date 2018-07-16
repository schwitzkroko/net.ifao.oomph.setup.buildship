package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Class WaitDialog
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class WaitDialog
   extends JDialog
{
   /**
   * 
   */
   private static final long serialVersionUID = -4853270595119199363L;
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JLabel jLabel2 = new JLabel();
   JClock jClock = new JClock();
   long start = System.currentTimeMillis();

   private static int locLast = 0;
   JButton jButton1 = new JButton();

   /**
    * Method getNextLocation
    *
    * @return
    * @author Andreas Brod
    */
   private static synchronized Point getNextLocation()
   {
      locLast += 20;

      if (locLast > 200) {
         locLast = 0;
      }

      return new Point(locLast * 2, locLast);
   }

   /**
    * Constructor WaitDialog
    *
    * @param frame
    * @param title
    * @param modal
    * @param pCom
    */
   public WaitDialog(Frame frame, String title, boolean modal)
   {

      super(frame, title, modal);
      setIconImage(Util.getImageIcon("dtdinfo/dtdinfo.png").getImage());

      try {
         jbInit();
         pack();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            jButton1_actionPerformed(null);
         }
      });

      setSize(140, 120);

      if (frame != null) {
         Dimension screenSize = frame.getSize();
         Dimension frameSize = getSize();

         setLocation(frame.getX() + (screenSize.width - frameSize.width) / 2, frame.getY()
               + (screenSize.height - frameSize.height) / 2);
      } else {
         setLocation(getNextLocation());
      }

      showTime();
   }

   /**
    * Method showTime
    * @author Andreas Brod
    */
   public void showTime()
   {
      if (start != 0) {
         double t = Math.round((System.currentTimeMillis() - start) / 100);

         jLabel2.setText((t / 10) + " s");
         jClock.repaint();
         validate();
      }
   }

   /**
    * Constructor WaitDialog
    *
    * @param frame
    * @param pCom
    */
   public WaitDialog(Frame frame)
   {
      this(frame, "Request started", false);
   }

   /**
    * Method jbInit
    *
    * @throws Exception
    * @author Andreas Brod
    */
   private void jbInit()
      throws Exception
   {
      panel1.setLayout(borderLayout1);
      jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
      jLabel2.setText("0.0 s");
      jButton1.setText("Stop");
      jButton1.addActionListener(new WaitDialog_jButton1_actionAdapter(this));
      getContentPane().add(panel1);
      panel1.add(jLabel2, BorderLayout.EAST);
      panel1.add(jClock, BorderLayout.CENTER);
      panel1.add(jButton1, BorderLayout.SOUTH);
   }

   /**
    * Method stop
    * @author Andreas Brod
    */
   public void stop()
   {
      start = 0;

      // setVisible(false);
      setVisible(false);
   }

   /**
    * Method jButton1_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButton1_actionPerformed(ActionEvent e)
   {
      stop();
   }
}


/**
 * Class WaitDialog_jButton1_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class WaitDialog_jButton1_actionAdapter
   implements java.awt.event.ActionListener
{
   WaitDialog adaptee;

   /**
    * Constructor WaitDialog_jButton1_actionAdapter
    *
    * @param adaptee
    */
   WaitDialog_jButton1_actionAdapter(WaitDialog adaptee)
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
