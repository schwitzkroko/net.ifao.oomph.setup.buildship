package schemagenerator.actions.amadeus;


import java.io.*;

import net.ifao.tools.schemaconversion.localtoglobalelements.Start;
import net.ifao.util.CorrectDatabindingXsd;
import net.ifao.xml.XmlObject;


public class PoweredFares
{

   public static void updatePoweredFares(String psAmadeusBaseDir, PrintStream pOut)
   {
      // rem Amadeus command PoweredFare_PricePNRWithBookingClass
      try {

         String[] sKeys =
            new String[]{ "pricepnrwithbookingclass", "pricepnrwithlowerfares",
                  "informativepricingwithoutpnr", "informativebestpricingwithoutpnr" };
         for (String sKey : sKeys) {
            Start.poweredfare(psAmadeusBaseDir + "/poweredfare/" + sKey, pOut);
            Start.poweredfare(psAmadeusBaseDir + "/poweredfare/" + sKey + "reply", pOut);
         }
         mergePricePnr("pricepnr", sKeys[0], sKeys[1], psAmadeusBaseDir, pOut);
         mergePricePnr("informativepricing", sKeys[2], sKeys[3], psAmadeusBaseDir, pOut);

      }
      catch (IOException e) {
         e.printStackTrace(pOut);
      }

   }

   /**
    * TODO (brod) add comment for method mergePricePnr
    *
    * @param psPricePnr TODO (brod) add text for param psPricePnr
    * @param psType1 TODO (brod) add text for param psType1
    * @param psType2 TODO (brod) add text for param psType2
    *
    * @author brod
    * @param psAmadeusBaseDir
    * @param pOut
    */
   private static void mergePricePnr(String psPricePnr, String psType1, String psType2,
                                     String psAmadeusBaseDir, PrintStream pOut)
   {
      mergePricePnr(psPricePnr, "", psType1, psType2, psAmadeusBaseDir, pOut);
      mergePricePnr(psPricePnr, "reply", psType1, psType2, psAmadeusBaseDir, pOut);

   }

   /**
    * TODO (brod) add comment for method mergePricePnr
    *
    * @param psPricePnr TODO (brod) add text for param psPricePnr
    * @param psReply TODO (brod) add text for param psReply
    * @param psType1 TODO (brod) add text for param psType1
    * @param psType2 TODO (brod) add text for param psType2
    *
    * @author brod
    * @param psAmadeusBaseDir
    * @param _out
    */
   private static void mergePricePnr(String psPricePnr, String psReply, String psType1,
                                     String psType2, String psAmadeusBaseDir, PrintStream _out)
   {

      // Manually merge the file
      String sWithbookingclass =
         psAmadeusBaseDir + "/poweredfare/" + psType1 + psReply + "/global_element_data.xsd";
      String sWithlowerfares =
         psAmadeusBaseDir + "/poweredfare/" + psType2 + psReply + "/global_element_data.xsd";
      String sPath = psAmadeusBaseDir + "/poweredfare/" + psPricePnr + psReply;

      String sTo = sPath + "/data.xsd";

      try {
         _out.println("Merge:");
         _out.println("File1:" + sWithbookingclass);
         XmlObject withbookingclass = new XmlObject(new File(sWithbookingclass)).getFirstObject();
         _out.println("File2:" + sWithlowerfares);
         XmlObject withlowerfares = new XmlObject(new File(sWithlowerfares)).getFirstObject();
         // get all Objects
         XmlObject[] objects = withlowerfares.getObjects("");
         // validate if all objects exist
         for (XmlObject object : objects) {
            XmlObject subObject =
               withbookingclass
                     .findSubObject(object.getName(), "name", object.getAttribute("name"));
            if (subObject == null) {
               withbookingclass.addObject(object.copy());
            } else {
               merge(object, subObject);
            }
         }
         _out.println("Into:" + sTo);
         File dataXsd = new File(sTo);
         AmadeusUtils.writeToFile(dataXsd.getAbsolutePath(), withbookingclass.toString());
         CorrectDatabindingXsd.correctDataBinding(dataXsd, sPath, _out);
         String[] files = { "dataBinding.xsd", "local_element_data.xsd" };
         for (String file : files) {
            // delete unneccesary files
            AmadeusUtils.delete(psAmadeusBaseDir + "/poweredfare/" + psType1 + psReply + "/"
                  + file);
            AmadeusUtils.delete(psAmadeusBaseDir + "/poweredfare/" + psType2 + psReply + "/"
                  + file);

         }

      }
      catch (Exception e) {
         e.printStackTrace(_out);
      }
   }

   /**
    * TODO (brod) add comment for method merge
    *
    * @param pXmlObjectFrom TODO (brod) add text for param pXmlObjectFrom
    * @param pXmlObjectTo TODO (brod) add text for param pXmlObjectTo
    *
    * @author brod
    */
   private static void merge(XmlObject pXmlObjectFrom, XmlObject pXmlObjectTo)
   {
      // get all objects
      XmlObject[] objects = pXmlObjectFrom.getObjects("");
      for (XmlObject object2 : objects) {
         String sType = "ref";
         String sRef = object2.getAttribute("ref");
         if (sRef.length() == 0) {
            sType = "name";
            sRef = object2.getAttribute("name");
         }
         if (sRef.length() == 0) {
            // find related object within toObject
            XmlObject object = pXmlObjectTo.getObject(object2.getName());
            if (object == null) {
               pXmlObjectTo.addObject(object2.copy());
            } else {
               merge(object2, object);
            }
         } else {
            XmlObject object = pXmlObjectTo.findSubObject(object2.getName(), sType, sRef);
            if (object == null) {
               pXmlObjectTo.addObject(object2.copy());
            }
         }
      }
   }

}
