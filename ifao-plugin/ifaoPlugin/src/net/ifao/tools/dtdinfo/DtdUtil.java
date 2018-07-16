package net.ifao.tools.dtdinfo;


import java.io.*;


/** 
 * TODO (brod) add comment for class DtdUtil 
 * 
 * <p> 
 * Copyright &copy; 2008, i:FAO 
 * 
 * @author brod 
 */
public class DtdUtil
{

   /** 
    * TODO (brod) add comment for method saveFile 
    * 
    * <p> TODO rename string to psString, string2 to ps2
    * @param string TODO (brod) add text for param string
    * @param string2 TODO (brod) add text for param string2
    * 
    * @author brod 
    */
   public static void saveFile(String string, String string2)
   {
      try {
         File file = new File(string);
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         fileOutputStream.write(string2.getBytes());
         fileOutputStream.close();
         System.out.println("WriteFile:" + file.getAbsolutePath());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** 
    * TODO (brod) add comment for method camelCase 
    * 
    * <p> TODO rename property to psProperty
    * @param property TODO (brod) add text for param property
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   public static String camelCase(String property)
   {
      if (property.length() > 1)
         return property.toUpperCase().substring(0, 1) + property.toLowerCase().substring(1);
      return property.toUpperCase();
   }

}
