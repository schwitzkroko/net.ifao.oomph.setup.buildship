package schemagenerator;


import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.gui.*;
import dtdinfo.*;


/**
 * The main class of the SchemaGenerator.
 * <p>
 * To implement a new 'Tab' for the schema generator, just implement a
 * new composite base on the SwtBase class ... and register this within the
 * getSchemaRunnable() method ! That should be all :-)
 *
 * <p>
 * Copyright &copy; 2008, i:FAO
 *
 * @author brod
 */
public class SchemaGenerator
{

   /**
    * Main method to start directly
    *
    * @param pArgs
    * @throws InterruptedException
    *
    */
   public static void main(String[] pArgs)
      throws InterruptedException
   {
      SchemaRunnable openSchemaGenerator = new SchemaRunnable(true);
      openSchemaGenerator.run();
   }

   /**
    * method openSchemaGenerator is the 'main' method within an eclipse
    * plugin to open the SchemaGenerator.
    * It creates the runnable which will be executed asynchron.
    *
    * @return Runnable
    *
    * @author brod
    */
   public static Runnable openSchemaGenerator()
   {
      Runnable openSchemaGenerator = new SchemaRunnable(false);
      Display.getDefault().asyncExec(openSchemaGenerator);
      return openSchemaGenerator;
   }

   /**
    * private method getSchemaRunnable which creates the runnable.
    *
    * @author brod
    */
   static class SchemaRunnable
      implements Runnable
   {

      private final boolean bWaitUntilFinished;

      public SchemaRunnable(boolean pbWaitUntilFinished)
      {
         bWaitUntilFinished = pbWaitUntilFinished;
      }

      @Override
      public void run()
      {
         String sPath = new DtdData(false).getPath();
         if (sPath.length() == 0) {
            Shell shell = new Shell();
            MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
            messageBox.setText("Info");
            messageBox.setMessage("No Path defined\nPlease start Dtd Info first to define a path");
            messageBox.open();
            shell.dispose();
            return;
         }

         Generator.setDEFAULTPATH(sPath);

         /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
          * for the correct SWT library path in order to run with the SWT dlls.
          * The dlls are located in the SWT plugin jar.
          * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
          *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
          */
         Display display = Display.getDefault();
         SchemaGeneratorSwt generator = new SchemaGeneratorSwt();

         generator.createSShell();

         // register tabs
         generator.addTab("Complete Directory", new SwtCompleteDirectory(generator.tabFolder(),
               SWT.NONE));
         generator.addTab("JaxB Directory", new SwtJaxBDirectory(generator.tabFolder(), SWT.NONE));
         generator.addTab("Cib", new SwtCib(generator.tabFolder(), SWT.NONE));
         generator.addTab("NewCib", new SwtNewCib(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("GDS");
         generator.addTab("Amadeus", new SwtAmadeus(generator.tabFolder(), SWT.NONE));
         generator.addTab("AmadeusWsdl", new SwtAmadeusWsdl(generator.tabFolder(), SWT.NONE));
         generator.addTab("AmadeusWsdlImport", new SwtAmadeusWsdlImport(generator.tabFolder(),
               SWT.NONE));
         generator.addTab("Galileo", new SwtGalileo(generator.tabFolder(), SWT.NONE));
         generator.addTab("GalileoWsdl", new SwtGalileoWsdl(generator.tabFolder(), SWT.NONE));
         SwtSabre swtSabre = new SwtSabre(generator.tabFolder(), SWT.NONE);
         generator.addTab("Sabre", swtSabre);
         generator.addTab("SabreInfo", new SwtSabreWsdl(generator.tabFolder(), SWT.NONE, swtSabre));
         generator.addTab("Travelport", new SwtTravelport(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("WSDL");
         generator.addTab("Wsdl", new SwtWsdl(generator.tabFolder(), SWT.NONE));
         generator.addTab("WsdlSingle", new SwtWsdlSingle(generator.tabFolder(), SWT.NONE));
         generator.addTab("WsdlJaxB", new SwtWsdlJaxB(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("Flight");

         generator.addTab("Travelfusion", new SwtTravelfusion(generator.tabFolder(), SWT.NONE));
         generator.addTab("Germanwings", new SwtGermanwings(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("Hotel");
         generator.addTab("Best Western", new SwtBestWestern(generator.tabFolder(), SWT.NONE));
         generator.addTab("CRC", new SwtCrc(generator.tabFolder(), SWT.NONE));
         generator.addTab("eHotel", new SwtEhotel(generator.tabFolder(), SWT.NONE));
         generator.addTab("Bookingcom", new SwtBookingcom(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("Car");

         generator.addTab("Sixt", new SwtSixt(generator.tabFolder(), SWT.NONE));
         generator.addTab("Caro", new SwtCaro(generator.tabFolder(), SWT.NONE));
         generator.addTab("Europcar", new SwtEuropcar(generator.tabFolder(), SWT.NONE));
         generator.addTab("Kemas", new SwtKemas(generator.tabFolder(), SWT.NONE));
         generator.addTab("Ota2008a", new SwtOta2008a(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("Rail");

         generator.addTab("SNCF", new SwtSncf(generator.tabFolder(), SWT.NONE));
         generator.addTab("NVS ServiceCatalogue", new SwtNvs(generator.tabFolder(), SWT.NONE));
         generator.addTab("NVS WSDL", new SwtNvsWsdl(generator.tabFolder(), SWT.NONE));
         generator.addTab("NVS Response Msgs", new SwtNvsResponseMsgs(generator.tabFolder(),
               SWT.NONE));
         generator.addTab("Evolvi XML", new SwtEvolviXml(generator.tabFolder(), SWT.NONE));
         generator.addTab("Bibe WSDL", new SwtBibeWsdl(generator.tabFolder(), SWT.NONE));
         generator.addTab("Silverrail", new SwtSilverrail(generator.tabFolder(), SWT.NONE));

         generator.createAdditionalTabFolder("CreditCard");
         generator.addTab("MasterCard", new SwtMasterCard(generator.tabFolder(), SWT.NONE));

         // load old values
         generator.loadValuesFrom(Generator.getSettings());

         // display
         generator.sShell.open();

         if (bWaitUntilFinished) {
            // run
            while (!generator.sShell.isDisposed()) {
               if (!display.readAndDispatch()) {
                  display.sleep();
               }
            }

            // finish
            display.dispose();

         }
      }

   }

}
