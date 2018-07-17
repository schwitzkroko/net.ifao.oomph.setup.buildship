package schemagenerator.actions.amadeus;


import java.io.*;
import java.net.*;
import java.util.*;


/** 
 * This is a helper class for Amadeus help File. In encapluates 
 * html and local files, which will be buffered. 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class AmadeusFile
{

   private static HashSet<File> _hsFileNotFound = new HashSet<File>();

   static final String AMADEUSBASEURL = "http://api.dev.amadeus.net/";

   /** 
    * This method inits the FileSystem. If this is called again, 
    * empty files will be written. 
    * 
    * @author brod 
    */
   static void initFileSystem()
   {
      // flush the empty files
      for (File emptyFile : _hsFileNotFound) {
         // ... if not written jet
         if (!new File(emptyFile.getAbsolutePath()).exists()) {
            writeFile(emptyFile, new byte[0]);
         }
      }
      _hsFileNotFound.clear();
   }

   /** 
    * private method to read from a stream 
    * 
    * @param pInputStream inputStream 
    * @param pOutputStream outputStream 
    * @throws IOException 
    * 
    * @author brod 
    */
   private static void read(InputStream pInputStream, OutputStream pOutputStream)
      throws IOException
   {
      int count;
      byte[] bytes = new byte[4096];
      while ((count = pInputStream.read(bytes)) > 0) {
         pOutputStream.write(bytes, 0, count);
      }
      pInputStream.close();
   }

   /** 
    * This method reads a File 
    * 
    * @param pFile File to read 
    * @return content of the file 
    * 
    * @author brod 
    */
   public static String readFile(File pFile)
   {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         read(new FileInputStream(pFile), outputStream);
      }
      catch (IOException e) {
         // file read exception
      }
      return outputStream.toString();
   }

   /** 
    * This method writes a File. If the paren directory does 
    * not exist, this will be also created 
    * 
    * @param pFile File object 
    * @param pyByteArray byteArray (to write) 
    * 
    * @author brod 
    */
   private static void writeFile(File pFile, byte[] pyByteArray)
   {
      if (!pFile.getParentFile().exists()) {
         pFile.getParentFile().mkdirs();
      }
      try {
         FileOutputStream fileWriter = new FileOutputStream(pFile);
         fileWriter.write(pyByteArray);
         fileWriter.close();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   private File _amadeusCacheDirectory;
   private boolean _bCorrectName;
   private String _sFileName;

   /** 
    * Constructor AmadeusFile 
    * 
    * @param pAmadeusCacheDirectory AmadeusCacheDirectory 
    * @param psFileName FileName (for the amadeus help file) 
    * @param pbCorrectName if true, the Name will be corrected 
    * 
    * @author brod 
    */
   public AmadeusFile(File pAmadeusCacheDirectory, String psFileName, boolean pbCorrectName)
   {
      _sFileName = psFileName;
      _amadeusCacheDirectory = pAmadeusCacheDirectory;
      _bCorrectName = pbCorrectName;
   }

   /** 
    * Tests whether the file or directory denoted by this abstract 
    * pathname exists. 
    * 
    * @return true if and only if the file or directory denoted by 
    * this abstract pathname exists; false otherwise 
    * 
    * @author brod 
    */
   public boolean exists()
   {
      return getFile().exists();
   }

   /** 
    * This method returns the local java File handle 
    * 
    * @return java file object 
    * 
    * @author brod 
    */
   public File getFile()
   {
      String sFileName = _sFileName;
      // correct quo
      if (sFileName.contains("?")) {
         sFileName = sFileName.substring(0, sFileName.indexOf("?"));
      }
      if (_bCorrectName) {
         sFileName = _sFileName.replaceAll("\\_", "/").replaceAll("[?|]+", "");
      }
      return new File(_amadeusCacheDirectory, sFileName);
   }

   /** 
    * This method returns the File object 
    * 
    * @param psFileName FileName 
    * @return File object 
    * 
    * @author brod 
    */
   public File getFile(String psFileName)
   {
      return new AmadeusFile(getFile(), psFileName, _bCorrectName).getFile();
   }

   /** 
    * this method reads a set of files (from a file buffer or 
    * from the file) 
    * 
    * @param phtFileBuffer FileBuffer (with already readen files) 
    * @param pLogOutput LogOutput stream 
    * @return content of the file 
    * 
    * @author brod 
    */
   public String read(Hashtable<String, String> phtFileBuffer, PrintStream pLogOutput)
   {
      String sHtmlPage = phtFileBuffer.get(_sFileName);
      if (sHtmlPage == null) {
         sHtmlPage = read(pLogOutput);
         phtFileBuffer.put(_sFileName, sHtmlPage);
      }
      return sHtmlPage;
   }

   /** 
    * This method reads the file content 
    * 
    * @param pLogOutput LogOutput stream, to log, that a 
    * file was readen 
    * @return content of the file 
    * 
    * @author brod 
    */
   public String read(PrintStream pLogOutput)
   {
      File file = getFile();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      boolean bFileNotFound = false;
      try {
         InputStream inputStream;
         if (file.exists()) {
            pLogOutput.print(".");
            inputStream = new FileInputStream(file);
         } else if (_hsFileNotFound.contains(_sFileName)) {
            // not neccessary to try again
            // ... co create empty InputStream
            inputStream = new ByteArrayInputStream(new byte[0]);
         } else {
            URL url = new URL(AMADEUSBASEURL + _sFileName);
            pLogOutput.print("#");
            URLConnection openConnection = url.openConnection();
            inputStream = openConnection.getInputStream();
         }
         read(inputStream, outputStream);
      }
      catch (java.io.FileNotFoundException ex) {
         // file not found on server
         bFileNotFound = true;
         _hsFileNotFound.add(file);
      }
      catch (Exception ex) {
         bFileNotFound = true;
      }
      byte[] outputBytes = outputStream.toByteArray();
      // buffer to drive (if not exists)
      if (!file.exists() && outputBytes.length > 0) {
         write(outputBytes);
      }
      // return the String
      return new String(outputBytes);
   }

   /** 
    * This method writes bytes 
    * 
    * @param pyByteArray byteArray 
    * 
    * @author brod 
    */
   public void write(byte[] pyByteArray)
   {
      File file = getFile();
      writeFile(file, pyByteArray);
   }
}
