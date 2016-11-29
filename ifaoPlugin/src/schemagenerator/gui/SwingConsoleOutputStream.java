package schemagenerator.gui;


import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.io.*;


public class SwingConsoleOutputStream
   extends JDialog
{

   private static final long serialVersionUID = 1L;
   private JPanel jContentPane = null;
   private JScrollPane jScrollPane = null;
   JTextArea jTextArea = null;
   private OutputStream out; //  @jve:decl-index=0:

   /**
    * This method initializes jScrollPane	
    * 	
    * @return javax.swing.JScrollPane	
    */
   private JScrollPane getJScrollPane()
   {
      if (jScrollPane == null) {
         jScrollPane = new JScrollPane();
         jScrollPane.setViewportView(getJTextArea());
      }
      return jScrollPane;
   }

   /**
    * This method initializes jTextArea	
    * 	
    * @return javax.swing.JTextArea	
    */
   private JTextArea getJTextArea()
   {
      if (jTextArea == null) {
         jTextArea = new JTextArea();
         jTextArea.setBackground(SystemColor.activeCaption);
         jTextArea.setFont(new Font("DialogInput", Font.PLAIN, 12));
         jTextArea.setForeground(Color.white);
      }
      return jTextArea;
   }

   void updateTextPane(final String text)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            Document doc = jTextArea.getDocument();
            try {
               doc.insertString(doc.getLength(), text, null);
            }
            catch (BadLocationException e) {
               throw new RuntimeException(e);
            }
            jTextArea.setCaretPosition(doc.getLength() - 1);
         }
      });
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      SwingConsoleOutputStream swingConsoleOutputStream = new SwingConsoleOutputStream(null);
      swingConsoleOutputStream.setVisible(true);

      try {
         swingConsoleOutputStream.getOutputStream().write("Dies ist ein Test\n".getBytes());
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @param owner
    */
   public SwingConsoleOutputStream(Frame owner)
   {
      super(owner);
      initialize();
      initStream();
   }

   private void initStream()
   {
      out = new OutputStream()
      {
         @Override
         public void write(final int b)
            throws IOException
         {
            updateTextPane(String.valueOf((char) b));
         }

         @Override
         public void write(byte[] b, int off, int len)
            throws IOException
         {
            updateTextPane(new String(b, off, len));
         }

         @Override
         public void write(byte[] b)
            throws IOException
         {
            write(b, 0, b.length);
         }

         @Override
         public void close()
         {
            setVisible(false);
         }
      };
   }

   public OutputStream getOutputStream()
   {
      // java - get screen size using the Toolkit class
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation(0, 0);
      setSize(screenSize);
      setVisible(true);
      return out;
   }

   /**
    * This method initializes this
    * 
    * @return void
    */
   private void initialize()
   {
      this.setSize(543, 396);
      this.setFont(new Font("DialogInput", Font.PLAIN, 12));
      this.setTitle("ConsoleOutput");
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
         jContentPane = new JPanel();
         jContentPane.setLayout(new BorderLayout());
         jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
      }
      return jContentPane;
   }

} //  @jve:decl-index=0:visual-constraint="10,10"
