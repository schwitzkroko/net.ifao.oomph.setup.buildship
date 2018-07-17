package net.ifao.tools.dtdinfo.swt;


import ifaoplugin.UtilSwt;

import java.util.Hashtable;

import net.ifao.tools.dtdinfo.Data;
import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;


public class DtdImages
{
   private static Hashtable<String, Image> htImages = new Hashtable<String, Image>();

   /** 
    * TODO (brod) add comment for method getImageFolder 
    * 
    * <p> TODO rename bOpened to pbOpened, xml to pXml, device to pDevice
    * @param bOpened TODO (brod) add text for param bOpened 
    * @param xml TODO (brod) add text for param xml 
    * @param device TODO (brod) add text for param device 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   public static Image getImageFolder(boolean bOpened, XmlObject xml, Device device)
   {
      String sIcon = "Folder" + (bOpened ? "O" : "C");
      boolean bSelfDefined = xml.getAttribute("selfDefined").equalsIgnoreCase("true");

      if (xml.createObject("Pnr").getCData().length() > 0) {
         sIcon += "_Text";
         if (bSelfDefined)
            sIcon += "_Func";
      } else if (Data.isNotSupported(xml)) {
         sIcon += "_NotSupported2";
      } else if (!Data.hasFilledSubObjects(xml)) {
         sIcon = "Folder" + (bOpened ? "X" : "M");
      }
      return size16(sIcon + ".jpg", device);
   }

   /** 
    * TODO (brod) add comment for method getTextFolder 
    * 
    * <p> TODO rename xml to pXml, device to pDevice
    * @param xml TODO (brod) add text for param xml 
    * @param device TODO (brod) add text for param device 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   public static Image getTextFolder(XmlObject xml, Display device)
   {
      String sIcon = "Empty";
      boolean bSelfDefined =
         xml != null && xml.getAttribute("selfDefined").equalsIgnoreCase("true");
      if (xml == null) {
         sIcon = "Empty";
      } else if (xml.createObject("Pnr").getCData().length() > 0) {
         sIcon = "Text";
         if (bSelfDefined)
            sIcon += "_Func";
      } else {
         String dataTransform = xml.createObject("TransformRules").getCData();
         if (Data.isNotSupported(xml)) {
            sIcon += "_NotSupported";
         } else if (dataTransform.length() == 0) {
            sIcon = "middle";
         }
      }

      return size16(sIcon + ".jpg", device);
   }


   /** 
    * TODO (brod) add comment for method size16 
    * 
    * <p> TODO rename sName to psName, device to pDevice
    * @param sName TODO (brod) add text for param sName 
    * @param device TODO (brod) add text for param device 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private static Image size16(String sName, Device device)
   {

      Image image = htImages.get(sName);
      if (image != null)
         return image;

      image = UtilSwt.getImage("dtdinfo/" + sName, device);
      ImageData data = image.getImageData();

      PaletteData paletteData = new PaletteData(new RGB[]{ new RGB(255, 255, 255) });

      int iMaxX = Math.max(16, data.width);
      int iMaxY = Math.max(16, data.height);
      ImageData imageData = new ImageData(iMaxX, iMaxY, data.depth, paletteData);

      int white = new RGB(255, 255, 255).hashCode();
      white = device.getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB().hashCode();

      for (int x = 0; x < iMaxX; x++) {
         for (int y = 0; y < iMaxY; y++) {
            int pixel;
            if (x < data.width && y < data.height)
               pixel = data.getPixel(x, y);
            else {
               pixel = white;
            }
            imageData.setPixel(x, y, pixel);

         }
      }
      Image retImage = new Image(device, imageData);
      htImages.put(sName, retImage);
      return retImage;
   }
}
