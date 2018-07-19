package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;
import schemagenerator.Generator;


/**
 * TODO (brod) add comment for class ImportSabreWsdlInfo
 *
 * <p>
 * Copyright &copy; 2009, i:FAO
 *
 * @author brod
 */
public class ImportSabreWsdlInfo
{

   public static abstract class DataUpdater
   {
      public abstract boolean addData(Data pData);
   }

   /**
    * TODO (brod) add comment for class ImportSabreWsdlInfo
    *
    * <p>
    * Copyright &copy; 2009, i:FAO
    *
    * @author brod
    */
   public class Data
   {

      private List<String> lst = new ArrayList<String>();


      public void setText(int i, String name)
      {
         while (i >= lst.size()) {
            lst.add("");
         }
         lst.set(i, name);
      }

      public String getText(int i)
      {
         if (i < lst.size()) {
            return lst.get(i);
         }
         return "";
      }

   }

   private static final String[] ROOT_WSDL = {
         "http://webservices.sabre.com/wsdl/sabreXML1.0.00/tpf/",
         "http://webservices.sabre.com/wsdl/tpfc/" };

   /**
    * TODO (brod) add comment for method getVersions
    *
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    * @param dataUpdater
    * @param pOut
    */
   public static void getVersions(DataUpdater dataUpdater, PrintStream pOut)
   {
      File f =
         Util.getProviderDataFile(Generator.getSettings().getAttribute("baseDir"),
               "net/ifao/providerdata/sabre");
      File fUtil =
         new File(Generator.getSettings().getAttribute("baseDir"),
               "src/net/ifao/arctic/agents/sabre/xml/SabreUtil.java");
      if (f.exists() && f.isDirectory() && fUtil.exists()) {
         try {
            new ImportSabreWsdlInfo().validate(f, new HashSet<String>(), getVersionsInJava(fUtil),
                  dataUpdater, pOut);
         }
         catch (IOException e) {
            // make nothing
         }

      }
   }

   /**
    * TODO (brod) add comment for method getVersionsInJava
    *
    * <p> TODO rename fJavaFile to pJavaFile
    * @param fJavaFile TODO (brod) add text for param fJavaFile
    * @return TODO (brod) add text for returnValue
    * @throws IOException
    *
    * @author brod
    */
   private static Hashtable<String, String> getVersionsInJava(File fJavaFile)
      throws IOException
   {
      Hashtable<String, String> ht = new Hashtable<String, String>();
      BufferedReader bufferedReader = new BufferedReader(new FileReader(fJavaFile));
      String sLine;
      while ((sLine = bufferedReader.readLine()) != null) {
         String sStart = "if (pNativeRequest instanceof ";
         int iStart = sLine.indexOf(sStart);
         int iEnd = sLine.indexOf("RQ)");
         if (iStart >= 0 && iStart < iEnd) {
            iStart += sStart.length();
            String sName = sLine.substring(iStart, iEnd);
            sLine = bufferedReader.readLine();
            if (sLine.indexOf("PREFIX_VERSION_WEB_SERVICE") > 0) {
               int i1 = sLine.indexOf("\"") + 1;
               int i2 = sLine.lastIndexOf("\"");
               if (i1 < i2) {
                  ht.put(sName.replaceAll("_", "").toUpperCase(), sLine.substring(i1, i2));
               }
            }
         }
      }
      bufferedReader.close();
      return ht;
   }

   /**
    * TODO (brod) add comment for method validate
    *
    * <p> TODO rename f to another name, versions to pVersions, hashtable to pHashtable
    * @param f TODO (brod) add text for param f
    * @param versions TODO (brod) add text for param versions
    * @param hashtable TODO (brod) add text for param hashtable
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    * @param dataUpdater
    * @param pOut
    * @return
    */
   private boolean validate(File f, HashSet<String> versions, Hashtable<String, String> hashtable,
                            DataUpdater dataUpdater, PrintStream pOut)
   {
      File[] listFiles = f.listFiles();
      for (File file : listFiles) {
         if (file.isDirectory()) {
            validate(file, versions, hashtable, dataUpdater, pOut);
         } else if (file.getName().equals("data.xsd")) {
            // read the data.xsd
            try {
               pOut.println(file.getAbsolutePath());

               XmlObject baseXml = new XmlObject(file).getFirstObject();
               XmlObject[] xmlObjects = baseXml.getObjects("include");
               for (XmlObject xmlObject2 : xmlObjects) {
                  // read the included schema
                  File importFile =
                     new File(file.getParentFile(), xmlObject2.getAttribute("schemaLocation"));
                  XmlObject xmlObject = new XmlObject(importFile).getFirstObject();
                  // get the last element
                  XmlObject[] elements = baseXml.getObjects("element");
                  if (elements.length > 0) {
                     String sName = importFile.getName();
                     sName = sName.substring(0, sName.lastIndexOf("."));
                     if (sName.endsWith("Schema")) {
                        sName = sName.substring(0, sName.length() - 6);
                     }
                     if (sName.endsWith("RQ")) {
                        sName = sName.substring(0, sName.length() - 2);
                     }
                     if (sName.endsWith("RS")) {
                        sName = sName.substring(0, sName.length() - 2);
                     }
                     if (sName.endsWith("RQ")) {
                        sName = sName.substring(0, sName.length() - 2);
                     }
                     if (elements.length >= 1) {
                        XmlObject lastElement = elements[elements.length - 1];
                        XmlObject complexType = lastElement.getObject("complexType");
                        String wsdl =
                           complexType.findSubObject("attribute", "name", "wsdl").getAttribute(
                                 "default");
                        String version =
                           complexType.findSubObject("attribute", "name", "version").getAttribute(
                                 "default");
                        if (version.indexOf("XML") > 0) {
                           version = version.substring(version.indexOf("XML") + 3);
                        }

                        complexType.findSubObject("attribute", "name", "soapAction").getAttribute(
                              "default");
                        complexType.findSubObject("attribute", "name", "service").getAttribute(
                              "default");
                        if (versions.add(wsdl)) {
                           Data item = new Data();
                           item.setText(0, sName);
                           setVersionAndWsdl(item, version, wsdl, pOut);
                           if (!dataUpdater.addData(item)) {
                              return false;
                           }
                        }
                     } else {
                        String version = getVersion(sName, hashtable);
                        String sPureVersion = version;
                        if (sPureVersion.endsWith(" *")) {
                           sPureVersion = sPureVersion.substring(0, sPureVersion.length() - 2);
                        }
                        if (sPureVersion.equals("1.0.1")) {
                           sPureVersion = "";
                        }

                        String wsdl = ROOT_WSDL[0] + sName + "LLS" + sPureVersion + "RQ.wsdl";
                        if (versions.add(wsdl)) {
                           Data item = new Data();
                           item.setText(0, sName);
                           setVersionAndWsdl(item, version, wsdl, pOut);
                           if (!dataUpdater.addData(item)) {
                              return false;
                           }
                        }
                     }
                  }
               }

            }
            catch (FileNotFoundException e) {
               e.printStackTrace();
            }
         }
      }
      return true;
   }

   /**
    * TODO (brod) add comment for method getNextVersion
    *
    * <p> TODO rename wsdl to psWsdl
    * @param psOldVersion TODO (brod) add text for param psOldVersion
    * @param wsdl TODO (brod) add text for param wsdl
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    * @param item
    * @param pOut
    */
   private void setVersionAndWsdl(Data item, String psOldVersion, String wsdl, PrintStream pOut)
   {
      item.setText(1, psOldVersion);
      // extract version
      int iEnd = wsdl.indexOf("RQ.wsdl");
      if (iEnd > 0) {
         try {
            int iStart = wsdl.lastIndexOf("S", iEnd) + 1;
            String sSub = wsdl.substring(iStart, iEnd);
            if (sSub.length() == 0) {
               sSub = "1.0.1";
            }
            StringTokenizer st = new StringTokenizer(sSub, ".");
            ArrayList<Integer> numbers = new ArrayList<Integer>();
            while (st.hasMoreTokens()) {
               numbers.add(Integer.parseInt(st.nextToken()));
            }
            if (numbers.size() == 3) {
               String sStartUrl = wsdl.substring(0, iStart);
               String sEndUrl = wsdl.substring(iEnd);
               // increase first number
               int i1 = numbers.get(0);
               int i2 = numbers.get(1);
               int i3 = numbers.get(2);
               String sStart2Url = sStartUrl;
               int iReplace = 0;
               for (int i = 0; i < ROOT_WSDL.length - 1; i++) {
                  if (sStart2Url.startsWith(ROOT_WSDL[i])) {
                     iReplace = i + 1;
                     sStart2Url = ROOT_WSDL[i + 1] + sStart2Url.substring(ROOT_WSDL[i].length());
                     break;
                  }
               }
               // validate if different version of the url exists
               for (int i = 0; i < 2; i++) {
                  while (existUrl(sStart2Url + (i1 + 1) + "." + 0 + "." + i + sEndUrl, pOut)) {
                     sStartUrl = sStart2Url;
                     for (int j = 0; j < ROOT_WSDL.length - 1; j++) {
                        if (wsdl.startsWith(ROOT_WSDL[j])) {
                           wsdl = ROOT_WSDL[iReplace] + wsdl.substring(ROOT_WSDL[j].length());
                           break;
                        }
                     }
                     i1++;
                     i2 = 0;
                     i3 = i;
                  }
               }
               for (int i = 0; i < 2; i++) {
                  while (existUrl(sStartUrl + i1 + "." + (i2 + 1) + "." + i + sEndUrl, pOut)) {
                     i2++;
                     i3 = i;
                  }
               }
               while (existUrl(sStartUrl + i1 + "." + i2 + "." + (i3 + 1) + sEndUrl, pOut)) {
                  i3++;
               }
               String sNewVersion = i1 + "." + i2 + "." + i3;
               if (psOldVersion.indexOf(sNewVersion) >= 0) {
                  psOldVersion = sNewVersion;
               }
               psOldVersion = sNewVersion + " (new)";
            }
         }
         catch (Exception ex) {
            // invalid version
         }
      }
      item.setText(2, psOldVersion);
      item.setText(3, wsdl);
   }

   /**
    * TODO (brod) add comment for method existUrl
    *
    * <p> TODO rename string to psString
    * @param psUrl TODO (brod) add text for param string
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    * @param pOut
    */
   private boolean existUrl(String psUrl, PrintStream pOut)
   {
      try {
         String outString = Utils.loadUrl(psUrl);

         boolean bContentLength;
         bContentLength =
            outString.indexOf("<definitions ") > 0 && outString.indexOf("<service ") > 0;
         if (!bContentLength) {
            bContentLength =
               outString.indexOf("<wsdl:definitions") > 0
                     && outString.indexOf("<wsdl:service ") > 0;
         }
         pOut.println(psUrl + "->" + bContentLength);
         return bContentLength;
      }
      catch (Exception e) {
         // url not existent
      }
      return false;
   }

   /**
    * TODO (brod) add comment for method getVersion
    *
    * <p> TODO rename name to psName, hashtable to pHashtable
    * @param name TODO (brod) add text for param name
    * @param hashtable TODO (brod) add text for param hashtable
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private String getVersion(String name, Hashtable<String, String> hashtable)
   {
      String sVersion = hashtable.get(name.replaceAll("_", "").toUpperCase());
      if (sVersion == null) {
         sVersion = "?";
      }
      return sVersion + " *";
   }

}
