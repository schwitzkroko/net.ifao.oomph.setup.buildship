package schemagenerator.actions;


import java.io.*;
import java.net.*;
import java.security.*;
import java.util.StringTokenizer;


/**
 * TODO (brod) add comment for class Utils
 *
 * <p>
 * Copyright &copy; 2009, i:FAO
 *
 * @author brod
 */
public class Utils
{

   /**
    * This method loads the content from any location (url or file)
    *
    * @param psLocation
    * @return
    * @throws IOException
    */
   public static String load(String psLocation)
      throws IOException
   {
      String sLocation = psLocation;
      if (sLocation.startsWith("http")) {
         return loadUrl(sLocation);
      }
      if (sLocation.startsWith("file:/")) {
         sLocation = sLocation.substring(6);
         while (sLocation.startsWith("/")) {
            sLocation = sLocation.substring(1);
         }
      }
      File pFile = new File(sLocation);
      if (!pFile.exists()) {
         throw new FileNotFoundException("File " + pFile.getName() + " not found");
      }
      return readFile(pFile);
   }

   /**
    * TODO (brod) add comment for method loadUrl
    *
    * @param psUrl TODO (brod) add text for param psUrl
    * @return TODO (brod) add text for returnValue
    * @throws IOException
    *
    * @author brod
    */
   public static String loadUrl(String psUrl)
      throws IOException
   {
      URL url = new URL(psUrl);
      URLConnection openConnection = url.openConnection();
      BufferedInputStream in = new BufferedInputStream(openConnection.getInputStream());
      byte[] b = new byte[4096];
      int count;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      while ((count = in.read(b)) > 0) {
         out.write(b, 0, count);
      }
      in.close();
      return out.toString();
   }

   public static void writeFile(File pFile, String psText)
      throws IOException
   {
      // TODO Auto-generated method stub
      if (!pFile.getParentFile().exists()) {
         pFile.getParentFile().mkdirs();
      }
      FileWriter fileWriter = new FileWriter(pFile);
      fileWriter.write(psText);
      fileWriter.close();
   }

   public static void writeFile(File pFile, byte[] psText)
      throws IOException
   {
      // TODO Auto-generated method stub
      if (!pFile.getParentFile().exists()) {
         pFile.getParentFile().mkdirs();
      }
      FileOutputStream fileWriter = new FileOutputStream(pFile);
      fileWriter.write(psText);
      fileWriter.close();
   }

   public static String readFile(File pFile)
      throws IOException
   {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(pFile));
      byte[] bytes = new byte[4096];
      int count;
      while ((count = bufferedInputStream.read(bytes)) > 0) {
         outputStream.write(bytes, 0, count);
      }
      bufferedInputStream.close();
      return outputStream.toString();
   }

   public static void clearDirectory(String temp)
   {
      File f = new File(temp);
      // ignore windows directory
      String sDir = f.getAbsolutePath().toLowerCase().replaceAll("\\\\", "/");
      System.out.println("clear:" + sDir);
      if (sDir.startsWith("c:/win") || (sDir.length() < 4)) {
         return;
      }
      // 1. the file has to exist and must have a parent !!!
      if (f.exists() && f.isDirectory() && (f.getParentFile() != null)) {
         // 2. get all containing files
         File[] listFiles = f.listFiles();
         for (File listFile : listFiles) {
            // in case of subdirectory
            if (listFile.isDirectory()) {
               // ... delete its contents
               clearDirectory(listFile.getAbsolutePath());
            }
            // finally delete the file
            listFile.delete();
         }
      }
   }


   public static void executeBat(String psTitle, String psBatchContent)
   {
      File fileTemp = null;
      try {
         fileTemp = new File("C:/temp/exec" + Math.random() + ".bat");

         writeFile(fileTemp, psBatchContent + "\nEXIT");

         String sCmd =
            "cmd.exe /c start \"" + psTitle + "\" /WAIT \"" + fileTemp.getAbsolutePath() + "\"";

         Runtime rt = Runtime.getRuntime();
         Process proc = rt.exec(sCmd);
         // any error message?
         StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", System.err);

         // any output?
         StreamGobbler outputGobbler =
            new StreamGobbler(proc.getInputStream(), "OUTPUT", System.out);

         // kick them off
         errorGobbler.start();
         outputGobbler.start();

         // any error???
         int exitVal = proc.waitFor();
         if (exitVal > 0) {
            System.out.println("ExitValue: " + exitVal);
         }

      }
      catch (Exception e) {
         // invalid exception
      }
      finally {
         if ((fileTemp != null) && fileTemp.exists()) {
            fileTemp.delete();
         }
      }
   }

   private static class StreamGobbler
      extends Thread
   {
      InputStream is;
      String type;
      OutputStream os;

      StreamGobbler(InputStream is, String type, OutputStream redirect)
      {
         this.is = is;
         this.type = type;
         this.os = redirect;
      }

      @Override
      public void run()
      {
         try {
            PrintWriter pw = null;
            if (os != null) {
               pw = new PrintWriter(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
               if (pw != null) {
                  pw.println(line);
               }
               System.out.println(type + ">" + line);
            }
            if (pw != null) {
               pw.flush();
            }
         }
         catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }

   public static String getCheckSum(byte[] bytes)
      throws NoSuchAlgorithmException, IOException
   {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      InputStream fis = new ByteArrayInputStream(bytes);
      byte[] dataBytes = new byte[1024];

      int nread = 0;

      while ((nread = fis.read(dataBytes)) != -1) {
         md.update(dataBytes, 0, nread);
      }

      byte[] mdbytes = md.digest();

      //convert the byte to hex format
      StringBuffer sb = new StringBuffer("");
      for (byte mdbyte : mdbytes) {
         sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
      }

      return sb.toString();
   }

   public static String getRelativePath(File file, File pRelative)
   {
      StringTokenizer st1 = new StringTokenizer(getPath(pRelative), "\\/:");
      StringTokenizer st2 = new StringTokenizer(getPath(file), "\\/:");
      StringBuilder sb = new StringBuilder();
      while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
         String s1 = st1.nextToken();
         String s2 = st2.nextToken();
         if (!s1.equalsIgnoreCase(s2)) {
            for (int i = 0; i < st2.countTokens(); i++) {
               sb.append("../");
            }
            if (sb.length() == 0)
               sb.append("./");
            sb.append(s1);
            break;
         }
      }
      if (sb.length() == 0)
         sb.append(".");
      while (st1.hasMoreTokens()) {
         sb.append("/");
         sb.append(st1.nextToken());
      }
      return sb.toString();
   }

   private static String getPath(File file)
   {
      try {
         return file.getCanonicalPath();
      }
      catch (IOException e) {
         return file.getAbsolutePath();
      }
   }

   public static void main(String[] args)
   {
      System.out.println(getRelativePath(new File(
            "c:/workspace/arctic/lib/providerdata/com/amadeus/xml/schema.xsd"), new File(
            "c:/workspace/arctic/lib/providerdata/com/amadeus/xml/x/schema2.xsd ")));
   }

   public static String getUser()
   {
      String sUser = System.getProperty("user.name");

      if ((sUser == null) || (sUser.length() == 0)) {
         try {
            sUser = InetAddress.getLocalHost().getHostName();
         }
         catch (UnknownHostException e) {
            sUser = "unkown";
         }
      }
      return sUser;
   }
}
