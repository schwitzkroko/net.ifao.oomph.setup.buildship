package net.ifao.util;


import java.io.*;
import java.util.*;
import java.util.zip.*;


/**
* This class implements a ZipItem.
* <p>
* This class encapsulates reading of a zip file
* <p>
* Copyright &copy; 2013, i:FAO
* 
* @author brod
*/
public class ZipItem
{

   private byte[] _content;
   private ArrayList<ZipItem> _lstSubItems = new ArrayList<>();
   private String _sName;
   private ZipItem _parent;
   private String _sFullName = "";
   private File _zipFile;
   private long _time;

   /**
   * This is the constructor for the class ZipItem, with the following parameters:
   * 
   * @param pZipFile zip file object
   * @throws IOException
   * 
   * @author brod
   */
   public ZipItem(File pZipFile)
      throws IOException
   {
      _zipFile = pZipFile;
      _sName = pZipFile.getName();
      Hashtable<String, byte[]> ht = new Hashtable<>();
      Hashtable<String, Long> htTimes = new Hashtable<>();
      ZipInputStream stream = new ZipInputStream(new FileInputStream(pZipFile));
      // read the entries
      try {

         // now iterate through each item in the stream. The get next
         // entry call will return a ZipEntry for each file in the
         // stream
         ZipEntry entry;
         while ((entry = stream.getNextEntry()) != null) {

            // ignore directories
            if (entry.isDirectory()) {
               continue;
            }
            // Once we get the entry from the stream, the stream is
            // positioned read to read the raw data, and we keep
            // reading until read returns 0 or less.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
               int len = 0;
               byte[] buffer = new byte[4096];
               while ((len = stream.read(buffer)) > 0) {
                  output.write(buffer, 0, len);
               }
            }
            finally {
               // we must always close the output file
               if (output != null) {
                  output.close();
               }
            }
            if (output != null) {
               String sEntryName = entry.getName();
               ht.put(sEntryName, output.toByteArray());
               htTimes.put(sEntryName, Long.valueOf(entry.getTime()));
            }
         }
      }
      finally {
         // we must always close the zip file.
         stream.close();
      }
      for (String sName : ht.keySet()) {
         String[] split = sName.split("[/\\\\]");
         ZipItem item = null;
         for (String sFileName : split) {
            if (item == null) {
               item = getItem(sFileName, 0);
            } else {
               item = item.getItem(sFileName, 0);
            }
         }
         if (item != null) {
            item.setContent(ht.get(sName));
            item._time = htTimes.get(sName).longValue();
         }
      }
   }

   /**
   * This is the internal constructor for the class ZipItem, with the following parameters:
   * 
   * @param parent parent Zip Item
   * @param psFileName file name String
   * @param psFullName
   * @param plDate date long
   * 
   * @author brod
   */
   private ZipItem(ZipItem parent, String psFileName, String psFullName, long plDate)
   {
      _sName = psFileName;
      _sFullName = psFullName;
      _parent = parent;
   }

   /**
   * sets a content.
   * 
   * @param parrContent byte array content
   * 
   * @author brod
   */
   private void setContent(byte[] parrContent)
   {
      _content = parrContent;
   }

   /**
   * returns an item with the following name (if this does not exists, the
   * related item will be created).
   * 
   * @param psFileName file name String
   * @param plDate date long
   * @return the item
   * 
   * @author brod
   */
   private ZipItem getItem(String psFileName, long plDate)
   {
      for (ZipItem item : _lstSubItems) {
         if (item._sName.equals(psFileName)) {
            return item;
         }
      }

      String sFullName = (_sFullName.length() > 0 ? _sFullName + "/" : "") + psFileName;
      ZipItem item = new ZipItem(this, psFileName, sFullName, plDate);
      _lstSubItems.add(item);
      return item;
   }

   /**
   * lists the zip item array of files.
   * 
   * @return list zip item array of files
   * 
   * @author brod
   */
   public ZipItem[] listFiles()
   {
      return _lstSubItems.toArray(new ZipItem[0]);
   }

   /**
   * returns a name for this zip item
   * 
   * @return the name of this item
   * 
   * @author brod
   */
   public String getName()
   {
      return _sName;
   }

   /**
   * returns a name for this zip item
   * 
   * @return the name of this item
   * 
   * @author brod
   */
   public String getFullName()
   {
      return _sFullName;
   }

   /**
   * returns the byte array of bytes.
   * 
   * @return the byte array content of bytes (null in case of directory)
   * 
   * @author brod
   */
   public byte[] getBytes()
   {
      return _content;
   }

   /**
   * returns if the ZipItem is a directory.
   * 
   * @return true, if the ZipItem is a directory
   * 
   * @author brod
   */
   public boolean isDirectory()
   {
      return _content == null;
   }

   /**
   * converts this object to a string.
   * 
   * @return to string representation
   * 
   * @author brod
   */
   @Override
   public String toString()
   {
      String sName = _sName;
      if (_parent != null) {
         sName = _parent.toString() + "/" + sName;
      }
      return sName;
   }

   /**
    * returns a file with the specified name
    * 
    * @param name String
    * @return the file (or null if not found)
    * 
    * @author Brod
    */
   public ZipItem getFile(String name)
   {
      for (ZipItem subItem : _lstSubItems) {
         if (subItem.getName().matches(name)) {
            return subItem;
         }
      }
      return null;
   }

   /**
    * sets the bytes. Additionally the date is set to the current time.
    * 
    * @param pyarrBytes content
    * 
    * @author Brod
    */
   public void setBytes(byte[] pyarrBytes)
   {
      _content = pyarrBytes;
      _time = new Date().getTime();
   }

   /**
    * updates the zip file.
    * 
    * @throws IOException
    * 
    * @author Brod
    */
   public void updateFile()
      throws IOException
   {
      if (_zipFile != null) {
         ZipOutputStream zipOutputStream =
            new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(_zipFile)));

         for (ZipItem subItem : _lstSubItems) {
            addZipEntry(subItem, zipOutputStream, subItem._time);
         }
         zipOutputStream.close();
      }
   }

   /**
    * adds a zip entry.
    * 
    * @param pSubItem item object Zip Item
    * @param pZipOutputStream output stream object
    * @param plTime time long
    * @throws IOException
    * 
    * @author Brod
    */
   private void addZipEntry(ZipItem pSubItem, ZipOutputStream pZipOutputStream, long plTime)
      throws IOException
   {
      if (!pSubItem.isDirectory()) {
         if (pSubItem._content != null) {
            ZipEntry entry = new ZipEntry(pSubItem.getFullName());
            if (plTime > 0) {
               entry.setTime(plTime);
            } else {
               entry.setTime(new Date().getTime());
            }
            pZipOutputStream.putNextEntry(entry);
            pZipOutputStream.write(pSubItem._content);
            pZipOutputStream.closeEntry();
         }
      } else {
         for (ZipItem directoryItem : pSubItem.listFiles()) {
            addZipEntry(directoryItem, pZipOutputStream, directoryItem._time);
         }
      }

   }
}
