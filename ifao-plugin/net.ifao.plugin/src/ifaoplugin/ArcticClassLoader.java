package ifaoplugin;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 * private class ArcticClassLoader, to load an arctic class
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class ArcticClassLoader
   extends ClassLoader
{
   private final List<File> _startPath;
   private final String _fileSeparator = System.getProperty("file.separator");

   /**
    * Constructor for ArcticClassLoader
    *
    * @param pParentClassLoader ParentClassLoader
    * @param psStartPath startPath
    *
    * @author brod
    */
   public ArcticClassLoader(ClassLoader pParentClassLoader, List<File> psStartPath)
   {
      super(pParentClassLoader);
      this._startPath = psStartPath;
   }

   /**
    * method findClass has to be implemented
    *
    * @param psName name
    * @return found Class
    * @throws ClassNotFoundException
    *
    * @author brod
    */
   @Override
   protected Class<?> findClass(String psName)
      throws ClassNotFoundException
   {
      return findClass(psName, null);
   }

   /**
    * private method to find a Class
    *
    * @param psName name of the class
    * @param pstkFiles stack of Files
    * @return found class
    * @throws ClassNotFoundException
    *
    * @author brod
    */
   private Class<?> findClass(String psName, Stack<File> pstkFiles)
      throws ClassNotFoundException
   {

      // Absoluten Pfad zur Klasse finden
      StringBuffer path = new StringBuffer();
      String[] splittedName = psName.split("\\.");
      List<String> splittedNameList = Arrays.asList(splittedName);

      Iterator<String> it = splittedNameList.iterator();
      while (it.hasNext()) {
         String pathFragment = it.next();
         path.append(pathFragment);
         if (it.hasNext()) {
            path.append(_fileSeparator);

         }
      }
      path.append(".class");
      // Klasse aus Datei laden

      int fileLength = 0;
      InputStream fis = null;
      JarFile jarFile = null;
      try {

         if ((pstkFiles != null) && (pstkFiles.size() > 0)) {
            jarFile = new JarFile(pstkFiles.pop());
            ZipEntry jarEntry = jarFile.getEntry(path.toString().replaceAll("\\\\", "/"));
            fis = jarFile.getInputStream(jarEntry);
            fileLength = (int) jarEntry.getSize();
            println(" in jar " + jarFile.getName());
         } else {

            println("ArcticClassLoader - loading: " + path.toString());
            for (File startPath : _startPath) {
               File classFile = new File(startPath + _fileSeparator + path.toString());
               if (classFile.exists()) {
                  fileLength = (int) classFile.length();
                  fis = new FileInputStream(classFile);
                  break;
               }
            }
         }
         if (fis == null) {
            throw new LinkageError(path.toString() + " not found");
         }

         // read the contents from the stream
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buffer = new byte[4096];
         int length = 0;
         while ((length = fis.read(buffer)) != -1) {
            out.write(buffer, 0, length);
         }
         out.flush();
         byte[] fileContent = out.toByteArray();
         fileLength = fileContent.length;

         // define package
         String packageName = null;
         int lastDot = psName.lastIndexOf('.');
         if (lastDot != -1) {
            packageName = psName.substring(0, lastDot);
         }
         // Package
         if (packageName != null) {
            try {
               // define the package
               super.definePackage(packageName, null, null, null, null, null, null, null);
            }
            catch (Exception ex1) {}
         }


         // create Class Object from byte array
         return super.defineClass(psName, fileContent, 0, fileLength);
      }
      catch (LinkageError e) {
         throw new ClassNotFoundException(e.toString());
      }
      catch (Exception e) {
         // try to find within Stack
         if (pstkFiles == null) {
            return findClass(psName, getStack());
         }
         if (pstkFiles.size() > 0) {
            return findClass(psName, pstkFiles);
         }
         throw new ClassNotFoundException(e.toString());
      }
      finally {
         if (fis != null) {
            try {
               fis.close();
            }
            catch (Exception ignored) {}
         }
         if (jarFile != null) {
            try {
               jarFile.close();
            }
            catch (Exception ignored) {}
         }
      }
   }

   /**
    * @param psText Text to print
    *
    * @author brod
    */
   private void println(String psText)
   {
      //  _systemOut.println(string);
   }

   /**
    * Builds a Stack containing all relevant jar files for arctic.
    * These are currently found in <ul>
    * <li>extFiles/lib</li>
    * <li>lib/provider</li>
    * <li>lib/providerdataJar</li>
    * <li>lib</li>
    * </ul>
    *
    * @return the stack of files
    *
    * @author brod
    */
   private Stack<File> getStack()
   {
      Stack<File> stk = new Stack<>();
      try {
         File f;
         if (_startPath.size() > 1) {
            f = this._startPath.get(0).getParentFile();
         } else {
            f = this._startPath.get(0);
         }
         File sRoot = f.getParentFile().getAbsoluteFile();

         // add to path
         addToStack(new File(sRoot + File.separator + "extFiles" + File.separator + "lib"), stk);
         addToStack(new File(sRoot + File.separator + "lib" + File.separator + "provider"), stk);
         addToStack(new File(sRoot + File.separator + "lib" + File.separator + "providerdataJar"), stk);
         addToStack(new File(sRoot + File.separator + "lib"), stk);

      }
      catch (Exception e) {}

      return stk;
   }

   /**
    * Adds the jar files of a directory to the stack
    *
    * @param pDirectory directory where the jar files are searched
    * @param pStackOfFiles Stack the jar files are added to
    *
    * @author brod
    */
   private void addToStack(File pDirectory, Stack<File> pStackOfFiles)
   {
      File[] files = pDirectory.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.getName().endsWith(".jar")) {
               pStackOfFiles.add(file);
            }
         }
      }
   }

   /**
    * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
    *
    * @author kaufmann
    */
   @Override
   public InputStream getResourceAsStream(String pName)
   {
      InputStream resourceAsStream = super.getResourceAsStream(pName);
      if (resourceAsStream == null) {
         Stack<File> stack = getStack();
         while (!stack.empty() && resourceAsStream == null) {
            JarFile jarFile;
            try {
               jarFile = new JarFile(stack.pop());
               ZipEntry jarEntry = jarFile.getEntry(pName.replaceAll("\\\\", "/"));
               if (jarEntry != null) {
                  resourceAsStream = jarFile.getInputStream(jarEntry);
               }
            }
            catch (IOException pException) {
               pException.printStackTrace();
            }
         }

      }
      return resourceAsStream;
   }
}
