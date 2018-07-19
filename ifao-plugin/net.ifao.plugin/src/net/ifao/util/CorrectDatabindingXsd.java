package net.ifao.util;


import ifaoplugin.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import net.ifao.xml.XmlObject;


/**
 * TODO (brod) add comment for class CorrectDatabindingXsd
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class CorrectDatabindingXsd
{

   private static boolean isCastor_1_3 = false;


   /**
    * TODO (brod) add comment for method correctDataBinding
    *
    * @param pDataDirectory TODO (brod) add text for param pDataDirectory
    * @param psPath TODO (brod) add text for param psPath
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   public static boolean correctDataBinding(File pDataDirectory, String psPath)
   {
      return correctDataBinding(pDataDirectory, psPath, System.out);
   }

   public static boolean correctDataBinding(File pfDataBinding, String psPath, PrintStream pOut)
   {
      try {
         File fBaseDirectory = pfDataBinding.getParentFile();
         File fileDataXsd = new File(fBaseDirectory, "data.xsd");
         if (fileDataXsd.exists()) {
            String sPath = psPath;
            if (sPath.length() == 0) {
               // try to find the lib provider data
               sPath =
                  Util.getProviderDataPackagePath(fileDataXsd.getParent().replaceAll("\\\\", "/"),
                        sPath);
            }

            // if there is a dataBinding.xml
            if (pfDataBinding.getName().equalsIgnoreCase("dataBinding.xml")) {
               // delete eventually a dataBinding.xsd file (wrong name)
               File dataBindingXSD = new File(fBaseDirectory, "dataBinding.xsd");
               if (dataBindingXSD.exists()) {
                  dataBindingXSD.delete();
               }
            }

            XmlObject dataXsd = new XmlObject(fileDataXsd).getFirstObject();
            boolean bCorrectDataXsd = correctDataXsd(dataXsd);

            XmlObject xmlDataBinding = new XmlObject(pfDataBinding).getFirstObject();
            boolean bDataBindingChanged = false;
            if (xmlDataBinding == null) {
               bDataBindingChanged = true;
               xmlDataBinding =
                  new XmlObject(
                        "<binding xmlns='http://www.castor.org/SourceGenerator/Binding' defaultBindingType='element' />")
                        .getFirstObject();
            }
            if (correctBindingForInvalidElements(xmlDataBinding, dataXsd, "/")) {
               bDataBindingChanged = true;
            }
            if (correctElementBinding(sPath, xmlDataBinding, dataXsd)) {
               bDataBindingChanged = true;
            }
            if (correctAttributeGroups(fileDataXsd, dataXsd, dataXsd)) {
               bCorrectDataXsd = true;
            }

            if (mapPackagesForImports(xmlDataBinding, fileDataXsd, new HashSet<String>(), "")) {
               bDataBindingChanged = true;
            }

            if (bCorrectDataXsd) {
               FileWriter fileWriter = new FileWriter(fileDataXsd);
               fileWriter.write(dataXsd.toString());
               fileWriter.close();
               pOut.println("Corrected " + fileDataXsd.getAbsolutePath());
            }
            if (bDataBindingChanged) {
               // if the content is 'empty'
               if (xmlDataBinding.getObjects("").length == 0) {
                  // delete the file
                  if (pfDataBinding.exists()) {
                     pfDataBinding.delete();
                     pOut.println("deleted " + pfDataBinding.getAbsolutePath());
                  }
               } else {
                  FileWriter fileWriter = new FileWriter(pfDataBinding);
                  fileWriter.write(xmlDataBinding.toString());
                  fileWriter.close();
                  pOut.println("Corrected " + pfDataBinding.getAbsolutePath());
               }
               return true;
            }
         }
      }
      catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return false;
   }

   private static boolean mapPackagesForImports(XmlObject xmlDataBinding, File pfDataXsd,
                                                HashSet<String> hashSet, String psPackage)
   {
      try {
         File dataXsd = pfDataXsd.getCanonicalFile();
         // if file already added
         if (!hashSet.add(dataXsd.getCanonicalPath()) || !dataXsd.exists()) {
            return false;
         }
         XmlObject xmlDataXsd = new XmlObject(dataXsd).getFirstObject();

         if (dataXsd.getName().equals("data.xsd")) {
            // get the targetNameSpace
            String targetNamespace = xmlDataXsd.getAttribute("targetNamespace");
            if (targetNamespace.length() == 0) {
               return false;
            }
            // validate if this already exists
            XmlObject[] packages = xmlDataBinding.getObjects("package");
            XmlObject found = null;
            for (XmlObject pack : packages) {
               XmlObject namespace = pack.getObject("namespace");
               if (namespace != null && namespace.getCData().equals(targetNamespace)) {
                  found = pack;
                  break;
               }
            }
            // if not found
            if (found == null) {
               // calculate the package
               String sPackage = psPackage;
               if (sPackage.length() == 0) {
                  sPackage = getPackageName(dataXsd);
               }

               found = new XmlObject("<package />").getFirstObject();
               found.createObject("name").setCData(sPackage);
               found.createObject("namespace").setCData(targetNamespace);
               xmlDataBinding.addElementObject(found, packages.length);
            }
         }
         // validate subobjects
         for (String sType : new String[]{ "include", "import" }) {
            for (XmlObject i : xmlDataXsd.getObjects(sType)) {
               String schemaLocation = i.getAttribute("schemaLocation");
               if (schemaLocation.length() > 0) {
                  mapPackagesForImports(xmlDataBinding, new File(dataXsd.getParentFile(),
                        schemaLocation), hashSet, sType.equals("include") ? getPackageName(dataXsd)
                        : "");
               }
            }
         }
      }
      catch (Exception e) {
         // no valid file
      }

      return hashSet.size() > 1;
   }

   private static String getPackageName(File dataXsd)
   {
      String sPackage =
         Util.getProviderDataPackagePath(dataXsd.getParent().replaceAll("[\\\\/]", "."), "");
      return sPackage;
   }

   /**
    * TODO (brod) add comment for method correctAttributeGroups
    *
    * <p> TODO rename fDataXsd to pDataXsd, root to pRoot, element to pElement
    * @param fDataXsd TODO (brod) add text for param fDataXsd
    * @param root TODO (brod) add text for param root
    * @param element TODO (brod) add text for param element
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private static boolean correctAttributeGroups(File fDataXsd, XmlObject root, XmlObject element)
   {
      boolean bChanged = false;
      String sElementName = element.getName();
      if (sElementName.equals("attribute")) {
         String sType = element.getAttribute("type");
         if (sType.equals("xs:language") || sType.equals("xsd:language")) {
            // castor does not support language
            element.setAttribute("type", sType.substring(0, sType.indexOf(":") + 1) + "string");
         }
      } else if (sElementName.equals("complexType") || sElementName.equals("extension")) {
         XmlObject[] attributeGroups = element.getObjects("attributeGroup");
         for (XmlObject attributeGroup : attributeGroups) {
            String ref = attributeGroup.getAttribute("ref");
            if (ref.contains(":")) {
               String sNS = ref.substring(0, ref.indexOf(":"));
               ref = ref.substring(ref.indexOf(":") + 1);
               String sNameSpace = root.getAttribute("xmlns:" + sNS);
               XmlObject subObject = root.findSubObject("attributeGroup", "name", ref);
               if (sNameSpace.length() > 0 && subObject == null) {
                  XmlObject findSubObject = root.findSubObject("import", "namespace", sNameSpace);
                  if (findSubObject != null) {
                     String schemaLocation = findSubObject.getAttribute("schemaLocation");
                     if (schemaLocation.length() > 0) {
                        try {
                           XmlObject xmlObject =
                              new XmlObject(new File(fDataXsd.getParentFile(), schemaLocation))
                                    .getFirstObject();
                           if (xmlObject != null) {
                              subObject = xmlObject.findSubObject("attributeGroup", "name", ref);
                              if (subObject != null) {
                                 subObject = subObject.copy();
                                 setNSAttributes(subObject, "ref", sNS);
                                 setNSAttributes(subObject, "type", sNS);
                                 root.addObject(subObject);
                                 bChanged = true;
                                 correctAttributeGroups(fDataXsd, root, subObject);
                              }
                           }
                        }
                        catch (Exception e) {
                           // ignored
                        }
                     }
                  }
               }
               if (subObject != null) {
                  attributeGroup.setAttribute("ref", ref);
               }

            }
         }
      }
      XmlObject[] objects = element.getObjects("");
      for (XmlObject object : objects) {
         if (correctAttributeGroups(fDataXsd, root, object)) {
            bChanged = true;
         }
      }
      return bChanged;
   }

   /**
    * TODO (brod) add comment for method setNSAttributes
    *
    * <p> TODO rename subObject to pObject, string to psString, sNS to psNS
    * @param subObject TODO (brod) add text for param subObject
    * @param string TODO (brod) add text for param string
    * @param sNS TODO (brod) add text for param sNS
    *
    * @author brod
    */
   private static void setNSAttributes(XmlObject subObject, String string, String sNS)
   {
      XmlObject[] objects = subObject.getObjects("");
      for (XmlObject object : objects) {
         String sName = object.getAttribute(string);
         if (sName.length() > 0 && !sName.contains(":")) {
            object.setAttribute(string, sNS + ":" + sName);
         }
         setNSAttributes(object, string, sNS);
      }
   }

   /**
    * TODO (brod) add comment for method correctDataXsd
    *
    * <p> TODO rename dataXsd to pXsd
    * @param dataXsd TODO (brod) add text for param dataXsd
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private static boolean correctDataXsd(XmlObject dataXsd)
   {
      // simple types and list does not match (for castor)
      XmlObject[] objects = dataXsd.getObjects("simpleType");
      boolean bChanged = false;
      for (XmlObject object : objects) {
         if (object.deleteObjects("list").length > 0) {
            String nameSpace = object.getNameSpace();
            if (nameSpace.length() > 0) {
               nameSpace += ":";
            }
            object.createObject(nameSpace + "restriction", "base", nameSpace + "string", true);
            bChanged = true;
         }
      }
      return bChanged;
   }

   /**
    * TODO (brod) add comment for method correctBindingForInvalidElements
    *
    * <p> TODO rename xmlObject to pObject, dataXsd to pXsd
    * @param xmlObject TODO (brod) add text for param xmlObject
    * @param dataXsd TODO (brod) add text for param dataXsd
    * @param psPath TODO (brod) add text for param psPath
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private static boolean correctBindingForInvalidElements(XmlObject xmlObject, XmlObject dataXsd,
                                                           String psPath)
   {
      boolean pbChanged = false;
      XmlObject[] elements = dataXsd.getObjects("element");
      for (XmlObject element : elements) {
         String sName = element.getAttribute("name");
         if (sName.equalsIgnoreCase("override")) {

            String sName2 = psPath + sName;
            XmlObject createObject =
               xmlObject.createObject("elementBinding", "name", sName2, false);
            if (createObject == null) {
               createObject = xmlObject.createObject("elementBinding", "name", sName2, true);
               StringTokenizer st = new StringTokenizer(psPath, "/");
               String sName3 = "";
               while (st.hasMoreElements()) {
                  sName3 = st.nextToken();
               }
               if (sName3.length() == 0) {
                  sName3 = "Element";
               }

               createObject.createObject("java-class", "name",
                     camelcase(sName3) + camelcase(sName), true);
               pbChanged = true;
            }
         }
         // correct complexType Names
         if (sName.length() > 0) {
            String sType = element.getAttribute("type");
            if (sType.length() > 0) {
               if (sType.contains(":")) {
                  sType = sType.substring(sType.indexOf(":") + 1);
               }
               if (sType.contains("_")) {
                  String sPre = isCastor_1_3 ? "/complexType:" : "";
                  XmlObject createObject =
                     xmlObject.createObject("complexTypeBinding", "name", sPre + sType, false);
                  if (createObject == null) {
                     xmlObject.createObject("complexTypeBinding", "name", sPre + sType, true)
                           .createObject("java-class", "name", sType.replaceAll("_", ""), true);

                  }
               }
            }
         } else {
            // validate the ref
            String sRef = element.getAttribute("ref");
            if (sRef.length() > 0) {
               if (sRef.contains(":")) {
                  sRef = sRef.substring(sRef.indexOf(":") + 1);
               }
               if (sRef.contains("_")) {
                  XmlObject createObject =
                     xmlObject.createObject("elementBinding", "name", "/" + sRef, false);
                  if (createObject == null) {
                     xmlObject.createObject("elementBinding", "name", "/" + sRef, true)
                           .createObject("java-class", "name", sRef.replaceAll("_", ""), true);

                  }
               }
            }

         }
      }
      XmlObject[] objects = dataXsd.getObjects("");
      for (XmlObject object : objects) {
         String sName = object.getAttribute("name");
         if (sName.length() > 0) {
            sName += "/";
         }
         if (correctBindingForInvalidElements(xmlObject, object, psPath + sName)) {
            pbChanged = true;
         }
      }
      return pbChanged;
   }

   /**
    * TODO (brod) add comment for method camelcase
    *
    * @param psText TODO (brod) add text for param psText
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private static String camelcase(String psText)
   {
      if (psText.length() > 1) {
         return psText.substring(0, 1).toUpperCase() + psText.substring(1).toLowerCase();
      }
      return psText;
   }

   /**
    * TODO (brod) add comment for method correctElementBinding
    *
    * <p> TODO rename xmlObject to pObject, dataXsd to pXsd
    * @param psPath TODO (brod) add text for param psPath
    * @param xmlObject TODO (brod) add text for param xmlObject
    * @param dataXsd TODO (brod) add text for param dataXsd
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private static boolean correctElementBinding(String psPath, XmlObject xmlObject,
                                                XmlObject dataXsd)
   {
      boolean bOk = false;
      if (xmlObject != null) {
         if (xmlObject.getName().equals("elementBinding")
               || xmlObject.getName().equals("attributeBinding")) {
            String attribute = xmlObject.getAttribute("name");
            if (isCastor_1_3) {
               if (attribute.startsWith("complexType:")) {
                  xmlObject.setAttribute("name", "/" + attribute);
                  bOk = true;
               }
            } else {
               if (attribute.startsWith("/complexType:")) {
                  xmlObject.setAttribute("name", attribute.substring(1));
                  bOk = true;
               }
            }
         }
         List<XmlObject> lstAllPackages = new ArrayList<XmlObject>();
         boolean bCorrectPackages = false;

         // corrrect the imports
         if (dataXsd != null) {
            XmlObject[] packages = xmlObject.getObjects("package");
            XmlObject[] imports = dataXsd.getObjects("import");
            for (XmlObject import1 : imports) {
               String schemaLocation = import1.getAttribute("schemaLocation");
               String namespace = import1.getAttribute("namespace");
               StringTokenizer st = new StringTokenizer(psPath + "/" + schemaLocation, "\\/");
               List<String> lst = new ArrayList<String>();
               while (st.hasMoreTokens()) {
                  String token = st.nextToken();
                  if (token.equals("..")) {
                     // go back
                     if (lst.size() > 0) {
                        lst.remove(lst.size() - 1);
                     }
                  } else if (token.equals(".")) {
                     // make nothing
                  } else {
                     lst.add(token);
                  }
               }
               schemaLocation = "";
               for (int j = 0; j < lst.size() - 1; j++) {
                  if (j > 0) {
                     schemaLocation += ".";
                  }
                  schemaLocation += lst.get(j);
               }
               XmlObject xmlPackage =
                  new XmlObject("<package><name>" + schemaLocation + "</name><namespace>"
                        + namespace + "</namespace></package>").getFirstObject();
               boolean bFound = false;
               for (XmlObject package1 : packages) {
                  if (package1.toString().equals(xmlPackage.toString())) {
                     bFound = true;
                  }
               }
               lstAllPackages.add(xmlPackage);
               if (!bFound) {
                  bCorrectPackages = true;
                  bOk = true;
               }
            }
         }
         List<XmlObject> lstElementBinding = new ArrayList<XmlObject>();
         List<XmlObject> lstattributeBinding = new ArrayList<XmlObject>();
         List<XmlObject> lstcomplexTypeBinding = new ArrayList<XmlObject>();
         XmlObject[] objects = xmlObject.getObjects("");
         String sBinding = "";
         for (XmlObject object : objects) {
            if (object.getName().equalsIgnoreCase("attributeBinding")) {
               // move attributeBinding to last
               lstattributeBinding.add(object);
               if (correctElementBinding(psPath, object, null)) {
                  bOk = true;
               }
               if (!sBinding.endsWith("attributeBinding")) {
                  sBinding += "attributeBinding";
               }
            } else if (object.getName().equalsIgnoreCase("complexTypeBinding")) {
               String sName = object.getAttribute("name");
               // move attributeBinding to last
               if (isCastor_1_3) {
                  if (!sName.startsWith("/") && sName.indexOf(":") < 0) {
                     object.setAttribute("name", "/complexType:" + sName);
                     bOk = true;
                  }
               } else {
                  if (sName.startsWith("/complexType:")) {
                     object.setAttribute("name", sName.substring(sName.indexOf(":") + 1));
                     bOk = true;
                  }
               }
               lstcomplexTypeBinding.add(object);
               if (!sBinding.endsWith("complexTypeBinding")) {
                  sBinding += "complexTypeBinding";
               }
            } else if (object.getName().equalsIgnoreCase("elementBinding")) {
               lstElementBinding.add(object);
               if (correctElementBinding(psPath, object, null)) {
                  bOk = true;
               }
               if (!sBinding.endsWith("elementBinding")) {
                  sBinding += "elementBinding";
               }
            }
         }
         // reorder
         if ((sBinding.length() == 0 || sBinding.equals("elementBinding")
               || sBinding.equals("elementBindingattributeBinding")
               || sBinding.equals("elementBindingattributeBindingcomplexTypeBinding") || sBinding
                  .equals("elementBindingcomplexTypeBinding")) && !bCorrectPackages) {
            // make nothing ... correct order
         } else {
            // add missing lstPackages
            if (bCorrectPackages) {
               xmlObject.deleteObjects("package");
               for (int i = 0; i < lstAllPackages.size(); i++) {
                  xmlObject.addObject(lstAllPackages.get(i));
               }
            }
            // remove naming xml
            XmlObject[] namingXML = xmlObject.getObjects("namingXML");
            xmlObject.deleteObjects("namingXML");
            xmlObject.addObjects(namingXML);

            xmlObject.deleteObjects("elementBinding");
            for (int i = 0; i < lstElementBinding.size(); i++) {
               xmlObject.addObject(lstElementBinding.get(i));
            }
            xmlObject.deleteObjects("attributeBinding");
            for (int i = 0; i < lstattributeBinding.size(); i++) {
               xmlObject.addObject(lstattributeBinding.get(i));
            }
            xmlObject.deleteObjects("complexTypeBinding");
            for (int i = 0; i < lstcomplexTypeBinding.size(); i++) {
               xmlObject.addObject(lstcomplexTypeBinding.get(i));
            }
            bOk = true;
         }
      }
      return bOk;
   }
}
