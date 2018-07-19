package net.ifao.util;


import java.io.*;
import java.net.*;


public class Url
{

   private URL url;

   public Url(String psUrl)
      throws MalformedURLException
   {
      url = new URL(psUrl);
   }

   public String getName()
   {
      String sName = url.getPath();
      if (sName.contains("?")) {
         sName = sName.substring(0, sName.indexOf("?"));
      }
      sName = sName.substring(sName.lastIndexOf("/") + 1);
      return sName;
   }

   public byte[] getBytes()
      throws IOException
   {
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      HttpURLConnection httpConn = urlConn;
      httpConn.setAllowUserInteraction(false);
      httpConn.connect();
      InputStream in = httpConn.getInputStream();
      BufferedInputStream bis = new BufferedInputStream(in);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int read = 0;
      byte[] buffer = new byte[4096];
      while ((read = bis.read(buffer)) > 0) {
         out.write(buffer, 0, read);
      }
      return out.toByteArray();
   }


}
