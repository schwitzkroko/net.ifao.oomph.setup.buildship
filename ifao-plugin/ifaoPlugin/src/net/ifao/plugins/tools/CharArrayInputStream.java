package net.ifao.plugins.tools;


import java.io.*;


/** 
 * The class CharArrayInputStream is used to simluate 
 * a InputStream to handle Char arrays 
 * 
 * <p> 
 * Copyright &copy; 2006, i:FAO 
 * 
 * @author brod 
 */
public class CharArrayInputStream
   extends InputStream
{

   char[] chars;

   int offs = 0;

   /** 
    * Constructor CharArrayInputStream inits the Stream with an 
    * CharArray 
    * 
    * @param pcChars The default parameters
    * 
    * @author brod 
    */
   public CharArrayInputStream(char[] pcChars)
   {
      chars = pcChars;
   }

   /** 
    * The methods read has to be implemented to get the
    * next char 
    * 
    * @return The next (character) of the stream, -1 if finished. 
    * @throws IOException 
    * 
    * @author brod 
    */
   @Override
   public int read()
      throws IOException
   {
      if (offs < chars.length) {
         offs++;
         return chars[offs - 1];
      }
      return -1;
   }

}
