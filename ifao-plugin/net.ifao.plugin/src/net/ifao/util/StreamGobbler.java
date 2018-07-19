package net.ifao.util;


import java.io.*;


public class StreamGobbler
   extends Thread
{
   InputStream _inputStream;
   String _sType;
   private OutputStream _outputStream;

   public StreamGobbler(InputStream pInputStream, String psType, OutputStream psOutputStream)
   {
      this._inputStream = pInputStream;
      this._sType = psType;
      _outputStream = psOutputStream;
   }

   @Override
   public void run()
   {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream));
         String sLine = null;
         while ((sLine = reader.readLine()) != null) {
            synchronized (_outputStream) {
               _outputStream.write(sLine.getBytes());
               _outputStream.write('\n');
            }
         }
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
}
