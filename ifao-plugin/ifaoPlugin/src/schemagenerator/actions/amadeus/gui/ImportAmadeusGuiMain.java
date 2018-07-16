package schemagenerator.actions.amadeus.gui;


import java.awt.Color;
import java.io.File;

import javax.swing.*;


/** 
 * This is the main class for a ImportAmadeusGui 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class ImportAmadeusGuiMain
{

   /** 
    * Main method (for test purpose)
    * @param psArgs  will be ignored
    * 
    */
   public static void main(String[] psArgs)
   {
      new ImportAmadeusGuiMain(null);
   }

   private File _amadeusCacheDirectory;

   private ImportAmadeusGui application;

   /** 
    *  Constructor ImportAmadeusGuiMain 
    * 
    * @param pAmadeusCacheDirectory AmadeusCacheDirectory
    * 
    * @author brod 
    */
   public ImportAmadeusGuiMain(File pAmadeusCacheDirectory)
   {
      _amadeusCacheDirectory = pAmadeusCacheDirectory;
      application = new ImportAmadeusGui();

      if (_amadeusCacheDirectory != null) {
         // init the button
         application.getJButtonOpenHelp().addActionListener(new java.awt.event.ActionListener()
         {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               openHtmlHelpFiles();
            }
         });
      }

      Thread doRun = new Thread()
      {
         @Override
         public void run()
         {
            application.getJFrame().setVisible(true);
         }
      };
      SwingUtilities.invokeLater(doRun);
      try {
         doRun.join();
      }
      catch (InterruptedException e) {
         // Interrupted
      }
   }

   /** 
    * This method activates the Help button 
    * 
    * @author brod 
    */
   public void activateHelp()
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            application.getJButtonOpenHelp().setEnabled(true);
         }
      });
   }

   /** 
    * The method close has to called finally 
    * 
    * @author brod 
    */
   public void close()
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            application.getJButtonStop().setEnabled(false);
            application.getJButtonFinished().setEnabled(true);
         }
      });
      application.waitUntilClosed();
   }

   /** 
    * @return true if stop button is pressed
    * 
    * @author brod 
    */
   public boolean isStopped()
   {
      return application.isStopped();
   }

   /** 
    * This method opens html help files 
    * 
    * @author brod 
    */
   public void openHtmlHelpFiles()
   {
      File indexFile = new File(_amadeusCacheDirectory, "api/index.htm");
      if (indexFile.exists()) {
         try {
            Process exec =
               Runtime.getRuntime().exec(
                     new String[]{ "cmd", "/c", "start", indexFile.getAbsolutePath() });
            exec.waitFor();
         }
         catch (Exception e) {
            // error during execution
         }
      }
   }

   /** 
    * This method is used to set "read Html" slider 
    * 
    * @param pdValue Slider Value
    * @param sCallPageLink CallPageLink
    * 
    * @author brod 
    */
   public void readHtml(final double pdValue, final String sCallPageLink)
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            JProgressBar jProgressBar = application.getJProgressBarHtml();
            int iValue = (int) Math.round(pdValue * 1000.0);
            if (iValue == 1000) {
               application.jLabelLoadHtml.setBackground(Color.GREEN);
            }
            if (sCallPageLink.length() == 0) {
               jProgressBar.setString(null);
            } else {
               jProgressBar.setString(sCallPageLink.substring(sCallPageLink.lastIndexOf("/") + 1));
            }
            jProgressBar.setValue(iValue);
         }
      });
   }

   /** 
    * This method sets the "readInterface" Slider 
    * 
    * @param piValue slider Value
    * @param piMax Max slider value
    * @param psText Text (for slider information)
    * 
    * @author brod 
    */
   public void readInterface(final int piValue, final int piMax, final String psText)
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            JProgressBar jProgressBar = application.getJProgressBarInterface();
            if (piValue == 0) {
               application.jLabelInterfaces.setBackground(Color.YELLOW);
               jProgressBar.setMaximum(piMax);
            } else if (piValue == piMax) {
               application.jLabelInterfaces.setBackground(Color.GREEN);
            }
            jProgressBar.setValue(piValue);
            if (psText.length() == 0) {
               jProgressBar.setString(null);
            } else {
               jProgressBar.setString(psText);
            }
         }
      });
   }

   /** 
    * This method sets the slider for "readTransaction"
    * 
    * @param piValue slider Value
    * @param piMax Max slider values
    * @param psText Text for slider
    * 
    * @author brod 
    */
   public void readTransaction(final int piValue, final int piMax, final String psText)
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            JProgressBar jProgressBar = application.getJProgressBarTransaction();
            if (piValue == 0) {
               application.jLabelTransaction.setBackground(Color.YELLOW);
               jProgressBar.setMaximum(piMax);
            }
            if (piValue == piMax) {
               application.jLabelTransaction.setBackground(Color.GREEN);
            }
            jProgressBar.setValue(piValue);
            if (psText.length() == 0) {
               jProgressBar.setString(null);
            } else {
               jProgressBar.setString(psText.substring(psText.lastIndexOf("/") + 1));
            }
         }
      });
   }

   public void scanLibDirectories(final int piValue, final int piMax, final String psText)
   {
      start(new Thread()
      {
         @Override
         public void run()
         {
            JProgressBar jProgressBar = application.getJProgressBarTransaction();
            if (piValue == 0) {
               application.jLabelTransaction.setText("Scan lib directories");
               application.jLabelTransaction.setBackground(Color.YELLOW);
               jProgressBar.setMaximum(piMax);
            }
            if (piValue == piMax) {
               application.jLabelTransaction.setBackground(Color.GREEN);
            }
            jProgressBar.setValue(piValue);
            if (psText.length() == 0) {
               jProgressBar.setString(null);
            } else {
               jProgressBar.setString(psText.substring(psText.lastIndexOf("/") + 1));
            }
         }
      });
   }

   /** 
    * Private method to start a thread 
    * 
    * @param pThread Thread (to start)
    * 
    * @author brod 
    */
   private void start(Thread pThread)
   {
      try {
         SwingUtilities.invokeAndWait(pThread);
      }
      catch (Exception e) {
         // just in case of exception 
         e.printStackTrace();
      }
   }
}
