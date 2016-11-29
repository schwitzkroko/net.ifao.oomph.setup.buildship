package schemagenerator.correctors;


import java.io.*;
import java.util.*;

import net.ifao.util.ZipItem;
import net.ifao.xml.XmlObject;


/**
 * This class implements an AmadeusWsdlZipCorrector.
 * <p>
 * Copyright &copy; 2014, i:FAO
 * 
 * @author Brod
 */
public class AmadeusWsdlZipCorrector
   implements ICorrector
{

   private File _importZipFile;
   private File _amadeusWSZip;
   private HashSet<String> _lstOperations = new HashSet<String>();
   private ZipItem[] _listFiles;
   private ZipItem _currentImportItem;

   /**
    * This is the constructor for the class AmadeusWsdlZipCorrector, with the following parameters:
    * 
    * @param pZipFile zip file object
    * @param pWSZip wszip object File
    * 
    * @author Brod
    */
   public AmadeusWsdlZipCorrector(File pZipFile, File pWSZip)
   {
      this._importZipFile = pZipFile;
      this._amadeusWSZip = pWSZip;
   }

   /**
    * corrects this AmadeusWsdlZipCorrector.
    * 
    * @param pWsdlFile file object Xml Object
    * 
    * @author Brod
    */
   @Override
   public void correct(XmlObject pWsdlFile)
   {
      XmlObject importWsdlFile =
         new XmlObject(new String(_currentImportItem.getBytes())).getFirstObject();
      // check if all operations are valid
      for (XmlObject portType : pWsdlFile.getObjects("portType")) {
         XmlObject[] operations = portType.getObjects("operation");
         for (XmlObject operation : operations) {
            String sIn = operation.createObject("input").getAttribute("message");
            String sOut = operation.createObject("output").getAttribute("message");
            boolean bInput = checkWsdlMessage(pWsdlFile, sIn);
            boolean bOutput = checkWsdlMessage(pWsdlFile, sOut);
            if (!bInput || !bOutput) {
               portType.deleteObjects(operation);
            }
         }
         // add new operations
         XmlObject importPort =
            importWsdlFile.findSubObject("portType", "name", portType.getAttribute("name"));
         if (importPort != null) {
            for (XmlObject operation : importPort.getObjects("operation")) {
               String sOperationName = operation.getAttribute("name");
               if (portType.findSubObject("operation", "name", sOperationName) == null) {
                  String sIn = operation.createObject("input").getAttribute("message");
                  String sOut = operation.createObject("output").getAttribute("message");
                  boolean bInput = checkWsdlMessage(importWsdlFile, sIn);
                  boolean bOutput = checkWsdlMessage(importWsdlFile, sOut);
                  if (bInput && bOutput) {
                     _lstOperations.add(sOperationName);

                     portType.addObject(operation.copy(), true);

                     addOperation(sIn, importWsdlFile, pWsdlFile);
                     addOperation(sOut, importWsdlFile, pWsdlFile);
                  }
                  // update the binding
                  for (XmlObject binding : pWsdlFile.getObjects("binding")) {
                     XmlObject importBinding =
                        importWsdlFile.findSubObject("binding", "name",
                              binding.getAttribute("name"));
                     if (importBinding != null) {
                        XmlObject[] importBindOperation =
                           importBinding.findSubObjects("operation", "name", sOperationName);
                        binding.deleteObjects(binding.findSubObject("operation", "name",
                              sOperationName));
                        if (importBindOperation.length > 0) {
                           int iCounter = 0;
                           for (int i = 1; i < importBindOperation.length; i++) {
                              XmlObject soapActionOperation =
                                 importBindOperation[i].getObject("operation");
                              if (soapActionOperation != null) {
                                 String soapAction = soapActionOperation.getAttribute("soapAction");
                                 // check if end matches
                                 if (soapAction.contains("/")) {

                                    XmlObject message =
                                       importWsdlFile.findSubObject("message", "name",
                                             sIn.substring(sIn.lastIndexOf(":") + 1));
                                    if (message != null) {
                                       XmlObject part = message.getObject("part");
                                       if (part != null) {
                                          // get the NameSpace of the element
                                          String sElementNamespace = part.getAttribute("element");
                                          if (sElementNamespace.contains(":")) {
                                             String sXmlNs =
                                                "xmlns:"
                                                      + sElementNamespace.substring(0,
                                                            sElementNamespace.indexOf(":"));
                                             String sSoapActionPath =
                                                soapAction.substring(soapAction.lastIndexOf("/"));
                                             if (importWsdlFile.getAttribute(sXmlNs).endsWith(
                                                   sSoapActionPath)) {
                                                iCounter = i;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                           binding.addObject(importBindOperation[iCounter].copy());
                        }

                     }
                  }
               }
            }
         }
      }
      deleteUnusedEntries(pWsdlFile);
   }

   /**
    * returns a correction summary.
    * 
    * @return the correction summary
    * 
    * @author Brod
    */
   @Override
   public String getCorrectionSummary()
   {

      StringBuilder sbSummary = new StringBuilder();
      for (String sItem : _lstOperations) {
         if (sbSummary.length() > 0) {
            sbSummary.append(", ");
         }
         sbSummary.append(sItem);
      }
      return sbSummary.toString();
   }

   /**
    * checks the wsdl files and updates the zip file.
    * 
    * @author Brod
    */
   public void checkWsdlFiles()
   {
      try {
         ZipItem zipItems = new ZipItem(_amadeusWSZip);
         ZipItem importZipItems = new ZipItem(this._importZipFile);
         _listFiles = zipItems.listFiles();
         for (ZipItem item : _listFiles) {
            String name = item.getName();
            if (name.endsWith(".wsdl")) {

               _currentImportItem = importZipItems.getFile(name.replaceAll("[0-9]", "."));
               if (_currentImportItem != null) {

                  XmlObject wsdlFile = new XmlObject(new String(item.getBytes())).getFirstObject();
                  correct(wsdlFile);

                  item.setBytes(wsdlFile.toString().getBytes());
               }
            }
         }
         zipItems.updateFile();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * deletes the unused entries.
    * 
    * @param pWsdlFile file object Xml Object
    * 
    * @author Brod
    */
   private void deleteUnusedEntries(XmlObject pWsdlFile)
   {
      // first of all delete all imports of unused files
      XmlObject schema = pWsdlFile.createObject("types").createObject("schema");
      HashSet<String> hsInvalidImports = new HashSet<String>();
      for (XmlObject xmlImportItem : schema.getObjects("import")) {
         if (!checkForFile(_listFiles,
               new StringTokenizer(xmlImportItem.getAttribute("schemaLocation"), "/"))) {
            schema.deleteObjects(xmlImportItem);
            hsInvalidImports.add(xmlImportItem.getAttribute("namespace"));
         }
      }
      // delete xmlns attributes
      HashSet<String> hsInvalidNameSpaces = new HashSet<String>();
      for (String sAttributeName : pWsdlFile.getAttributeNames(true)) {
         // check if related import exists
         if (sAttributeName.startsWith("xmlns:")) {
            String sNameSpace = pWsdlFile.getAttribute(sAttributeName);
            if (hsInvalidImports.contains(sNameSpace)
                  && schema.findSubObject("import", "namespace", sNameSpace) == null) {
               // remove this entry
               pWsdlFile.setAttribute(sAttributeName, null);
               hsInvalidNameSpaces.add(sAttributeName.substring(6));
            }
         }
      }
      // delete invalid messages
      HashSet<String> hsInvalidMessages = new HashSet<String>();
      for (XmlObject message : pWsdlFile.getObjects("message")) {
         XmlObject part = message.getObject("part");
         if (part != null) {
            String element = part.getAttribute("element");
            if (element.contains(":")) {
               if (hsInvalidNameSpaces.contains(element.substring(0, element.indexOf(":")))) {
                  pWsdlFile.deleteObjects(message);
                  hsInvalidMessages.add(message.getAttribute("name"));
               }
            }
         }
      }
      // delete unused operations
      for (XmlObject portType : pWsdlFile.getObjects("portType")) {
         for (XmlObject operation : portType.getObjects("operation")) {
            String sIn = operation.createObject("input").getAttribute("message");
            String sOut = operation.createObject("output").getAttribute("message");
            boolean bInput = checkWsdlMessage(pWsdlFile, sIn);
            boolean bOutput = checkWsdlMessage(pWsdlFile, sOut);
            if (!bInput || !bOutput) {
               portType.deleteObjects(operation);
            }
         }
      }
   }

   /**
    * returns if the AmadeusWsdlZipCorrector checks a wsdl message.
    * 
    * @param pWsdlFile file object Xml Object
    * @param psMessage message String
    * @return true, if the AmadeusWsdlZipCorrector check wsdl message
    * 
    * @author Brod
    */
   private boolean checkWsdlMessage(XmlObject pWsdlFile, String psMessage)
   {
      String sMessage = psMessage.substring(psMessage.lastIndexOf(":") + 1);
      XmlObject message = pWsdlFile.findSubObject("message", "name", sMessage);
      if (message != null) {
         // validate if the schema file for the message exists
         String element = message.createObject("part").getAttribute("element");
         if (element.contains(":")) {
            String nameSpace = element.substring(0, element.indexOf(":"));
            String xmlns = pWsdlFile.getAttribute("xmlns:" + nameSpace);
            XmlObject importFile =
               pWsdlFile.createObject("types").createObject("schema")
                     .findSubObject("import", "namespace", xmlns);
            if (importFile != null) {
               return checkForFile(_listFiles,
                     new StringTokenizer(importFile.getAttribute("schemaLocation"), "/"));
            }
         }
      }
      return false;
   }

   /**
    * adds an operation.
    * 
    * @param psMessage message String
    * @param pImportWsdlFile wsdl file object Xml Object
    * @param pWsdlFile wsdl file object Xml Object
    * 
    * @author Brod
    */
   private static void addOperation(String psMessage, XmlObject pImportWsdlFile, XmlObject pWsdlFile)
   {
      String sMessage = psMessage.substring(psMessage.lastIndexOf(":") + 1);

      XmlObject message = pImportWsdlFile.findSubObject("message", "name", sMessage);
      if (message != null) {
         pWsdlFile.deleteObjects(pWsdlFile.findSubObject("message", "name", sMessage));
         pWsdlFile.addObject(message.copy(), true);
         // validate if the schema file for the message exists
         String element = message.createObject("part").getAttribute("element");
         if (element.contains(":")) {
            String nameSpace = element.substring(0, element.indexOf(":"));
            String xmlns = pImportWsdlFile.getAttribute("xmlns:" + nameSpace);
            pWsdlFile.setAttribute("xmlns:" + nameSpace, xmlns);
            XmlObject importFile =
               pImportWsdlFile.createObject("types").createObject("schema")
                     .findSubObject("import", "namespace", xmlns);
            if (importFile != null) {
               XmlObject schema = pWsdlFile.createObject("types").createObject("schema");
               schema.deleteObjects(pWsdlFile.findSubObject("import", "namespace", xmlns));
               schema.addObject(importFile.copy(), true);
            }
         }
      }
   }

   /**
    * returns if a specific file is contained within the list of zipItems
    * 
    * @param parrListFiles zip item array of list files
    * @param pStringTokenizer String Tokenizer
    * @return true, if the AmadeusWsdlZipCorrector checked for a file
    * 
    * @author Brod
    */
   private boolean checkForFile(ZipItem[] parrListFiles, StringTokenizer pStringTokenizer)
   {
      String nextToken = pStringTokenizer.nextToken();
      if (pStringTokenizer.hasMoreTokens()) {
         // check for directory
         for (ZipItem zipItem : parrListFiles) {
            if (zipItem.isDirectory() && zipItem.getName().equals(nextToken)) {
               return checkForFile(zipItem.listFiles(), pStringTokenizer);
            }
         }
      } else {
         for (ZipItem zipItem : parrListFiles) {
            if (zipItem.getName().equals(nextToken)) {
               return true;
            }
         }
      }
      return false;
   }

}
