package net.ifao.util;


import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;


public class TextAreaOutputStream
   extends OutputStream
{

   /**
    * Represents the data written to the stream.
    */
   ArrayList<StringBuilder> lstLines = new ArrayList<>();

   /**
    * Represents the text area that will be showing the written data.
    */
   private JTextArea ta;

   private StringBuilder sbLine;

   private JFrame frame;

   private boolean _bWholeOutput = false;

   /**
    * Creates a console context.
    * @param sTitle
    * @param pbWholeOutput if true, the output is not reduced to the last (100) lines
    */
   private TextAreaOutputStream(String sTitle, boolean pbWholeOutput)
   {
      sbLine = new StringBuilder();
      lstLines.add(sbLine);
      _bWholeOutput = pbWholeOutput;

      frame = new JFrame();
      frame.setTitle(sTitle);
      frame.add(new JLabel(" Outout"), BorderLayout.NORTH);
      ta = new JTextArea();
      ta.setBackground(Color.BLACK);
      ta.setForeground(Color.WHITE);
      ta.setFont(new Font("monospaced", 0, 12));

      frame.add(new JScrollPane(ta));

      frame.setSize(600, 480);
      frame.setVisible(true);
   }

   @Override
   public void close()
      throws IOException
   {
      frame.dispose();
      super.close();
   }


   @Override
   public void write(int i)
      throws IOException
   {
      sbLine.append((char) i);
      if (i == '\n') {
         sbLine = new StringBuilder();
         lstLines.add(sbLine);
         if (!_bWholeOutput) {
            while (lstLines.size() > 100) {
               lstLines.remove(0);
            }
         }

         String s = toString();
         ta.setText(s);
         ta.setCaretPosition(s.length());
      }
   }

   @Override
   public String toString()
   {
      StringBuilder sbTotal = new StringBuilder();
      for (StringBuilder line : lstLines) {
         sbTotal.append(line);
      }
      return sbTotal.toString();
   }


   public static OutputStream createOutputStream(String psTitle, boolean pbWholeOutput)
   {
      TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(psTitle, pbWholeOutput);
      return textAreaOutputStream;
   }

}
