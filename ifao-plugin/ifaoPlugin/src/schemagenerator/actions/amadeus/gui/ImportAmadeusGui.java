package schemagenerator.actions.amadeus.gui;


import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;


public class ImportAmadeusGui
{

   private Thread waitThread = null; //  @jve:decl-index=0:
   private JFrame jFrame = null; //  @jve:decl-index=0:visual-constraint="10,10"
   private JPanel jContentPane = null;
   private JPanel jPanelNavigation = null;
   private JPanel jPanelMain = null;
   private JButton jButtonStop = null;
   JLabel jLabelLoadHtml = null;
   private JProgressBar jProgressBarHtml = null;
   JLabel jLabelInterfaces = null;
   private JProgressBar jProgressBarInterface = null;
   JLabel jLabelTransaction = null;
   private JProgressBar jProgressBarTransaction = null;
   private JPanel jPanelRight = null;
   private JButton jButtonFinished = null;

   /**
    * This method initializes jPanelNavigation	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanelNavigation()
   {
      if (jPanelNavigation == null) {
         GridBagConstraints gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = -1;
         gridBagConstraints.gridy = -1;
         jPanelNavigation = new JPanel();
         jPanelNavigation.setLayout(new BorderLayout());
         jPanelNavigation.add(getJPanelRight(), BorderLayout.EAST);
         jPanelNavigation.add(getJPanelLeft(), BorderLayout.WEST);
      }
      return jPanelNavigation;
   }

   /**
    * This method initializes jPanelMain	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanelMain()
   {
      if (jPanelMain == null) {
         jLabelTransaction = new JLabel();
         jLabelTransaction.setText("load Transaction");
         jLabelTransaction.setOpaque(true);
         jLabelTransaction.setHorizontalAlignment(SwingConstants.CENTER);
         jLabelTransaction.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
         jLabelInterfaces = new JLabel();
         jLabelInterfaces.setText("Load Interface");
         jLabelInterfaces.setOpaque(true);
         jLabelInterfaces.setHorizontalAlignment(SwingConstants.CENTER);
         jLabelInterfaces.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
         jLabelLoadHtml = new JLabel();
         jLabelLoadHtml.setText("LoadHtmlPages");
         jLabelLoadHtml.setOpaque(true);
         jLabelLoadHtml.setHorizontalAlignment(SwingConstants.CENTER);
         jLabelLoadHtml.setBackground(Color.yellow);
         jLabelLoadHtml.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
         GridLayout gridLayout = new GridLayout();
         gridLayout.setRows(6);
         jPanelMain = new JPanel();
         jPanelMain.setLayout(gridLayout);
         jPanelMain.add(jLabelLoadHtml, null);
         jPanelMain.add(getJProgressBarHtml(), null);
         jPanelMain.add(jLabelInterfaces, null);
         jPanelMain.add(getJProgressBarInterface(), null);
         jPanelMain.add(jLabelTransaction, null);
         jPanelMain.add(getJProgressBarTransaction(), null);
      }
      return jPanelMain;
   }

   /**
    * This method initializes jButtonStop	
    * 	
    * @return javax.swing.JButton	
    */
   JButton getJButtonStop()
   {
      if (jButtonStop == null) {
         jButtonStop = new JButton();
         jButtonStop.setText("Stop");
         jButtonStop.addActionListener(new java.awt.event.ActionListener()
         {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               _bStopped = true;
            }
         });
      }
      return jButtonStop;
   }

   private boolean _bStopped = false;
   private JPanel jPanelLeft = null;
   private JButton jButtonOpenHelp = null;

   public boolean isStopped()
   {
      return _bStopped;
   }

   /**
    * This method initializes jProgressBarHtml	
    * 	
    * @return javax.swing.JProgressBar	
    */
   public JProgressBar getJProgressBarHtml()
   {
      if (jProgressBarHtml == null) {
         jProgressBarHtml = new JProgressBar();
         jProgressBarHtml.setStringPainted(true);
         jProgressBarHtml.setMaximum(1000);
      }
      return jProgressBarHtml;
   }

   /**
    * This method initializes jProgressBarInterface	
    * 	
    * @return javax.swing.JProgressBar	
    */
   public JProgressBar getJProgressBarInterface()
   {
      if (jProgressBarInterface == null) {
         jProgressBarInterface = new JProgressBar();
         jProgressBarInterface.setStringPainted(true);
      }
      return jProgressBarInterface;
   }

   /**
    * This method initializes jProgressBarTransaction	
    * 	
    * @return javax.swing.JProgressBar	
    */
   public JProgressBar getJProgressBarTransaction()
   {
      if (jProgressBarTransaction == null) {
         jProgressBarTransaction = new JProgressBar();
         jProgressBarTransaction.setStringPainted(true);
      }
      return jProgressBarTransaction;
   }

   /**
    * This method initializes jFrame
    * 
    * @return javax.swing.JFrame
    */
   public JFrame getJFrame()
   {
      if (jFrame == null) {
         jFrame = new JFrame();
         jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         jFrame.setSize(402, 202);
         jFrame.setContentPane(getJContentPane());
         jFrame.setTitle("Amadeus Help to Schemas");
         jFrame.addWindowListener(new java.awt.event.WindowAdapter()
         {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
            // make nothing
            }
         });
      }
      return jFrame;
   }

   /**
    * This method initializes jContentPane
    * 
    * @return javax.swing.JPanel
    */
   private JPanel getJContentPane()
   {
      if (jContentPane == null) {
         jContentPane = new JPanel();
         jContentPane.setLayout(new BorderLayout());
         jContentPane.add(getJPanelNavigation(), BorderLayout.SOUTH);
         jContentPane.add(getJPanelMain(), BorderLayout.CENTER);
      }
      return jContentPane;
   }

   /**
    * This method initializes jPanelRight	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanelRight()
   {
      if (jPanelRight == null) {
         jPanelRight = new JPanel();
         jPanelRight.setLayout(new GridBagLayout());
         jPanelRight.add(getJButtonStop(), new GridBagConstraints());
         jPanelRight.add(getJButtonFinished(), new GridBagConstraints());
      }
      return jPanelRight;
   }

   /**
    * This method initializes jButtonFinished	
    * 	
    * @return javax.swing.JButton	
    */
   JButton getJButtonFinished()
   {
      if (jButtonFinished == null) {
         jButtonFinished = new JButton();
         jButtonFinished.setText("Finished");
         jButtonFinished.setEnabled(false);
         jButtonFinished.addActionListener(new java.awt.event.ActionListener()
         {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               jFrame = getJFrame();
               jFrame.setVisible(false);
               jFrame.dispose();
               if (waitThread != null) {
                  waitThread.interrupt();
               }
            }
         });
      }
      return jButtonFinished;
   }

   public void waitUntilClosed()
   {
      if (!jFrame.isValid()) {
         return;
      }
      waitThread = new Thread()
      {
         @Override
         public void run()
         {
            try {
               join(); //  @jve:decl-index=0:
            }
            catch (InterruptedException e) {
               // wait until iterrupted
               interrupt();
            }

         }
      };
      waitThread.start();
      try {
         waitThread.join();
      }
      catch (InterruptedException e) {
         // finished
      }
   }

   /**
    * This method initializes jPanelLeft	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanelLeft()
   {
      if (jPanelLeft == null) {
         jPanelLeft = new JPanel();
         jPanelLeft.setLayout(new GridBagLayout());
         jPanelLeft.add(getJButtonOpenHelp(), new GridBagConstraints());
      }
      return jPanelLeft;
   }

   /**
    * This method initializes jButtonOpenHelp	
    * 	
    * @return javax.swing.JButton	
    */
   protected JButton getJButtonOpenHelp()
   {
      if (jButtonOpenHelp == null) {
         jButtonOpenHelp = new JButton();
         jButtonOpenHelp.setText("Open AmadeusHelp");
         jButtonOpenHelp.setEnabled(false);         
      }
      return jButtonOpenHelp;
   }


}
