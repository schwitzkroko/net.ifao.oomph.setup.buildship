package schemagenerator;


import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.widgets.Display;

import schemagenerator.gui.SchemaGeneratorSwt;


/** 
 * The class Generator is the base class which handles the generator.
 * It capsulates the data <code>SchemaGenerator.xml</code> with save 
 * and get method. 
 * Additionally it implements a threaded main method. 
 * <p>
 * Look for additional information within the SchemaGenerator. 
 * 
 * <p> 
 * Copyright &copy; 2008, i:FAO 
 * 
 * @author brod 
 */
public class Generator
{
   public String sBaseArctic;

   /** 
    * Constructor Generator with the base Arctic Directory
    * 
    * @param psBaseArctic The base Arctic Directory
    * 
    * @author brod 
    */
   public Generator(String psBaseArctic)
   {
      sBaseArctic = psBaseArctic;
   }

   // ------------------------------------------------------------------
   // STATIC MEMBERS
   // ------------------------------------------------------------------

   private static String DEFAULTPATH = "";

   /** 
    * method setDEFAULTPATH sets the DefaultPath 
    * 
    * @param psPath The default arctic path
    * 
    * @author brod 
    */
   public static void setDEFAULTPATH(String psPath)
   {
      DEFAULTPATH = psPath;
   }

   /**
    * @return the default path
    */
   public static String getDefaultPath()
   {
      return DEFAULTPATH;
   }

   /** 
    * method saveSettings saves the settings  
    * 
    * @param pSettings The settings of the SchemaGenerator.
    * 
    * @author brod 
    */
   public static void saveSettings(XmlObject pSettings)
   {
      try {
         FileWriter fileWriter = new FileWriter("SchemaGenerator.xml");
         fileWriter.write(pSettings.toString());
         fileWriter.close();
      }
      catch (IOException e) {}
   }

   /** 
    * method getSettings loads the current settings of the SchemaGenerator
    * 
    * @return The SchemaGenerator
    * 
    * @author brod 
    */
   public static XmlObject getSettings()
   {
      XmlObject settings = null;
      try {
         settings = new XmlObject(new File("SchemaGenerator.xml")).getFirstObject();
      }
      catch (Exception ex) {}
      if (settings == null) {
         settings = new XmlObject("<Settings baseDir=\"" + DEFAULTPATH + "\"/>").getFirstObject();
      }

      return settings;

   }

   /** 
    * method start calls the start method of the SchemaGeneratorSwt.
    * It will be started in a threaded way.
    * 
    * @param pForm The form which has to be started.
    * 
    * @author brod 
    */
   public void start(final SchemaGeneratorSwt pForm)
   {
      // save current settings
      XmlObject settings = getSettings();
      pForm.saveValuesTo(settings);
      saveSettings(settings);
      pForm.setActive(false);
      final Generator thisGenerator = this;

      Runnable runnable = new Runnable()
      {
         @Override
         public void run()
         {
            // start to execute
            pForm.start(thisGenerator);
         }
      };
      Display.getDefault().asyncExec(runnable);
   }

}
