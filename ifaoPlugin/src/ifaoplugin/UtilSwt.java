package ifaoplugin;


import java.io.*;
import java.net.*;
import java.util.Hashtable;

import net.ifao.util.Base64Coder;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;


/** 
 * TODO (brod) add comment for class Tools 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class UtilSwt
{
   static String _sAuth = "";

   private static Hashtable<String, Image> htImages = new Hashtable<String, Image>();

   /** 
    * TODO (brod) add comment for method getImage 
    * 
    * <p> TODO rename sName to psName, device to pDevice
    * @param sName TODO (brod) add text for param sName
    * @param device TODO (brod) add text for param device
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   public static Image getImage(String sName, Device device)
   {
      Class<Activator> _abstractUIPlugin = ifaoplugin.Activator.class;
      Image image = htImages.get(sName);
      if (image == null) {
         Class<?>[] classes = { String.class };
         Object[] args = { "/icons/" + sName };
         ImageDescriptor descriptor;
         try {
            descriptor =
               (ImageDescriptor) _abstractUIPlugin.getMethod("getImageDescriptor", classes).invoke(
                     _abstractUIPlugin, args);
         }
         catch (Exception e) {
            descriptor = null;
         }
         if (descriptor == null) {
            image = new Image(device, "icons/" + sName);
         } else {
            image = descriptor.createImage();
         }
         htImages.put(sName, image);
      }
      return image;
   }


   /** 
    * Method loadFromURL 
    * 
    * @param psURL 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static String loadFromURL(String psURL)
   {
      if (psURL.indexOf("://") < 0) {
         psURL = "file:///" + psURL;
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      boolean bLoad = true;
      while (bLoad) {
         bLoad = false;
         try {
            System.out.println("Load From URL " + psURL);

            URL url = new URL(psURL);
            URLConnection connection = url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(false);
            if (_sAuth.length() > 0) {
               String encoded = "Basic " + Base64Coder.encodeString(_sAuth);
               connection.setRequestProperty("Authorization", encoded);
            }
            InputStream openStream = connection.getInputStream();
            openStream = new BufferedInputStream(openStream);
            byte[] b = new byte[4096];
            int count;
            while ((count = openStream.read(b)) > 0) {
               out.write(b, 0, count);
            }
            openStream.close();

         }
         catch (IOException ex) {
            ex.printStackTrace();
            if (ex.getLocalizedMessage().indexOf(" 401 ") > 0) {
               // enter auth
               InputDialog dlg =
                  new InputDialog(Display.getCurrent().getActiveShell(), "",
                        "Enter the Authentification \"user[:password]\"", _sAuth, null);
               if (dlg.open() == Window.OK) {
                  // User clicked OK; update the label with the input
                  _sAuth = dlg.getValue();
                  bLoad = true;
               }
            }
         }
      }

      return new String(out.toByteArray());
   }

   public static void writeFile(IFile file, String sText, IProgressMonitor monitor)
   {
      InputStream in = null;
      try {
         in = new ByteArrayInputStream(sText.getBytes());
         if (file.exists()) {
            file.setContents(in, true, false, monitor);
         } else {
            File baseFile = file.getRawLocation().toFile();
            if (!baseFile.getParentFile().exists()) {
               baseFile.getParentFile().mkdirs();
               file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            }
            file.create(in, true, monitor);
         }
      }
      catch (CoreException e) {
         e.printStackTrace();
      }
      finally {
         if (in != null)
            try {
               in.close();
            }
            catch (IOException e) {}
      }

   }


}
