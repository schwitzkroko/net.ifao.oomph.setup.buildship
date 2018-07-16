package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import net.ifao.xml.*;


/**
 * Class ImportNvsResponseMsgs
 *
 * <p>
 * Copyright &copy; 2010, i:FAO Group GmbH
 * @author kaufmann
 */
public class ImportNvsResponseMsgs
{

   private String _sBaseDir = null;
   private String[] _directories = null;
   private StringBuilder _sbResult = null;
   private String _forceOptional = "true";

   /**
    * Constructor ImportNvsResponseMsgs
    *
    * @param psBaseDir arctic project directory
    * @param pDirectories list of names of the directories below net\ifao\providerdata\bahn\nvs\response\
    *
    * @author kaufmann
    */
   public ImportNvsResponseMsgs(String psBaseDir, String[] pDirectories)
      throws Exception
   {
      // cut off a backslash at the end
      if (psBaseDir.endsWith("\\") || psBaseDir.endsWith("/")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
      }

      _sBaseDir = psBaseDir.replace('\\', '/');

      _directories = pDirectories;
      if (_directories.length == 0) {
         throw new Exception("No directory selected");
      }

      _sbResult = new StringBuilder();

   }

   /**
    * "Main" method of this class, starts the transformation of NVS msg.xml/vb.xml (which must be
    * named as data.xml on disk) to data.xsd
    *
    *
    * @author kaufmann
    */
   public void startGeneration()
   {
      for (String sDirectory : _directories) {
         String sFullPath =
            Util.getProviderDataPath(_sBaseDir, "net/ifao/providerdata/bahn/nvs/response/"
                  + sDirectory);
         File directory = new File(sFullPath);
         if (directory.exists() && directory.isDirectory() /*&& sFullPath.toLowerCase().endsWith("buchenpepres")*/) {
            String sDataXmlFile = sFullPath + "/data.xml";
            if (new File(sDataXmlFile).exists()) {
               String sDataXsdFile = sFullPath + "/data.xsd";
               convertNvsXml2Schema(sDataXmlFile, sDataXsdFile);
            } else {
               _sbResult.append("did not find data.xml in directory ").append(sFullPath)
                     .append("\n");
            }
         } else {
            _sbResult.append("could not create data.xsd for directory ").append(sFullPath)
                  .append("\n");
         }
      }

      // add a final comment to the result "log"
      if (_sbResult.indexOf("* ") >= 0) {
         _sbResult.append("_______________________________________\n\n");
         _sbResult.append("\nFiles/directories marked with * have to be submitted (in CVS) !\n");
      }

   }

   /**
    * Method getResult returns the string for the result ("log")
    *
    * @return result of the operation
    *
    * @author kaufmann
    */
   public String getResult()
   {
      return _sbResult.toString();
   }


   /** 
    * Method convertNvsXml2Schema reads the XML file and starts the creation of the schema 
    * 
    * @param psXmlFile XML file describing the structure of the UPDATA elements
    * @param psXsdFile the schema file to create
    * 
    * @author kaufmann 
    */
   private void convertNvsXml2Schema(String psXmlFile, String psXsdFile)
   {
      // read the XML data into the internal data structure
      List<TagData> allTags = readNvsXml(psXmlFile);
      if (allTags != null) {
         if (allTags.size() > 0) {
            _sbResult.append("Elements read:").append(allTags.size()).append("\n");
            // write the schema to disk
            writeSchemaAndBinding(psXsdFile, allTags);
         } else {
            _sbResult.append("No Elements found!").append("\n");
         }
      }
   }

   /** 
    * Method readNvsXml reads the NVS xml file into the internal data structure 
    * 
    * @param psXmlFile file name of the XML file
    * @return list of elements
    * 
    * @author kaufmann 
    */
   private List<TagData> readNvsXml(String psXmlFile)
   {
      _sbResult.append("___________________________________________________\nReading file ")
            .append(psXmlFile).append("\n");
      List<TagData> allTags = new ArrayList<TagData>();
      File xmlFile = new File(psXmlFile);
      BufferedReader xmlReader = null;
      try {
         xmlReader = new BufferedReader(new FileReader(xmlFile));

         // stack, used to create the path infos
         Stack<TagData> tags = new Stack<TagData>();

         // pattern for the start of an element
         Pattern startTagPattern =
            Pattern.compile("<([^> ]+)( +type=\"(.+?)\")?( +optional=\"(.+?)\")? *>");
         TagData currentTag = null;
         int inComment = 0;
         int inTag = 0;
         String sLine = null;
         // read the file
         while ((sLine = xmlReader.readLine()) != null) {
            sLine = sLine.trim();
            if (sLine.startsWith("<?xml")) {
               // ignore
            } else if (sLine.endsWith("-->") && inComment > 0) {
               // if we are inside a comment and a comment has closed, reduce number of "open"
               // comments
               inComment--;
            } else if (sLine.startsWith("<!--")) {
               // a new comment has started, increase the number of "open" comments
               inComment++;
            } else if (inComment > 0) {
               // If we are in a comment, add this line to the comments of the current element
               if (inTag > 0) {
                  currentTag.comments.add(sLine);
               }
            } else if (sLine.matches("</[^> ]+>") && inTag > 0) {
               // the end of an element has been found.
               inTag--;
               currentTag = tags.pop();
            } else {
               Matcher matcher = startTagPattern.matcher(sLine);
               if (matcher.matches()) {
                  // the start of an element has been found, create element data
                  currentTag = new TagData();
                  currentTag.sTagName = matcher.group(1);
                  currentTag.sType = matcher.group(3);
                  currentTag.sOptional = matcher.group(5);
                  // add the element as sub tag to the last element on the stack
                  if (!tags.empty()) {
                     tags.lastElement().subElements.add(currentTag);
                  }
                  // put the current tag on top of the stack
                  tags.push(currentTag);
                  // create the XPath of the current element
                  currentTag.paths.add(calcPath(tags));
                  // add the element to the list of all elements
                  allTags.add(currentTag);
                  inTag++;
               }
            }
         }
      }
      catch (FileNotFoundException pException) {
         _sbResult.append("File not found\n").append(pException.getMessage()).append("\n");
      }
      catch (IOException pException) {
         _sbResult.append("IOException\n").append(pException.getMessage()).append("\n");
      }
      finally {
         try {
            if (xmlReader != null) {
               xmlReader.close();
            }
         }
         catch (IOException pException) {
            _sbResult.append("IOException\n").append(pException.getMessage()).append("\n");
         }
      }
      return allTags;
   }


   /** 
    * Method writeSchema writes the schema to disk 
    * 
    * @param psXsdFile the schema file name
    * @param pAllTags the list of tags
    * 
    * @author kaufmann 
    */
   private void writeSchemaAndBinding(String psXsdFile, List<TagData> pAllTags)
   {
      BufferedWriter xsdWriter = null;
      StringWriter xsdStringWriter = new StringWriter();
      try {
         xsdWriter = new BufferedWriter(xsdStringWriter);
         writeXSDHeader(xsdWriter);
         // write the root element (and recursively the sub elements)
         writeElement(xsdWriter, pAllTags.get(0), "  ", true, false);

         // write the type definitions
         writeTypes(xsdWriter, pAllTags, "  ");

         // finalize the xsd
         writeXSDFooter(xsdWriter);
      }
      catch (IOException pException) {
         _sbResult.append("IOException\n").append(pException.getMessage()).append("\n");
      }
      finally {
         try {
            if (xsdWriter != null) {
               xsdWriter.flush();
               String sSchema = xsdStringWriter.toString();
               Util.updateFile(psXsdFile, sSchema, _sbResult);
               xsdWriter.close();
               xsdStringWriter.close();
               writeBinding(sSchema, psXsdFile);
            }
         }
         catch (IOException pException) {
            _sbResult.append("IOException\n").append(pException.getMessage()).append("\n");
         }
      }
   }

   /**
    * Creates a binding for a schema and writes it to disk
    *
    * @param psSchema the schema 
    * @param psXsdFile the file name of the schema
    *
    * @author kaufmann
    */
   private void writeBinding(String psSchema, String psXsdFile)
   {
      XmlObject schema = new XmlObject(psSchema).getFirstObject();
      String sDataBinding = WsdlObject.getDataBinding(schema, null, "", false, false);
      String sBindingFile = psXsdFile.replaceFirst(".xsd$", "Binding.xml");
      Util.updateFile(sBindingFile, sDataBinding, _sbResult);
   }

   /**
    * Writes the type definitions (complexType name="..." ...) into the schema 
    *
    * @param pXsdWriter schema writer
    * @param pAllTags list of all collected tags
    * @param psIndent indentation
    *
    * @author kaufmann
    * @throws IOException 
    */
   private void writeTypes(BufferedWriter pXsdWriter, List<TagData> pAllTags, String psIndent)
      throws IOException
   {
      List<TagData> uniqueTypes = getUniqueTypes(pAllTags);
      for (TagData tag : uniqueTypes) {
         pXsdWriter.write(psIndent + "<xs:complexType name=\"" + tag.sType + "\">");
         pXsdWriter.newLine();
         pXsdWriter.write(psIndent + "  <xs:sequence>");
         pXsdWriter.newLine();
         if (tag.sTagName.equals(tag.sType) && tag.subElements.size() == 1
               && tag.sTagName.equals(tag.subElements.get(0).sTagName)) {
            for (TagData subTag : tag.subElements.get(0).subElements) {
               writeElement(pXsdWriter, subTag, psIndent + "    ", false,
                     tag.sType.endsWith("List") && tag.subElements.size() == 1);
            }
         } else {
            for (TagData subTag : tag.subElements) {
               writeElement(pXsdWriter, subTag, psIndent + "    ", false,
                     tag.sType.endsWith("List") && tag.subElements.size() == 1);
            }
         }
         pXsdWriter.write(psIndent + "  </xs:sequence>");
         pXsdWriter.newLine();
         pXsdWriter.write(psIndent + "</xs:complexType>");
         pXsdWriter.newLine();
      }
   }


   /**
    * Collects the unique type definitions from the tag list
    *
    * @param pAllTags list of all tags
    * @return list containing the unique types (complexTypes)
    *
    * @author kaufmann
    */
   private List<TagData> getUniqueTypes(List<TagData> pAllTags)
   {
      Map<String, List<TagData>> types = new HashMap<String, List<TagData>>();
      List<TagData> uniqueTypes = new ArrayList<>();
      for (TagData tag : pAllTags) {
         if (!tag.subElements.isEmpty() && tag.sType != null) {
            if (types.containsKey(tag.sType)) {
               boolean bFound = false;
               for (TagData old : types.get(tag.sType)) {
                  if (old.isSameType(tag)) {
                     bFound = true;
                     break;
                  }
               }
               if (!bFound) {
                  types.get(tag.sType).add(tag);
                  uniqueTypes.add(tag);
               }
            } else {
               // remind the element and write its definition to the schema
               List<TagData> newElement = new ArrayList<>();
               newElement.add(tag);
               types.put(tag.sType, newElement);
               uniqueTypes.add(tag);
            }

         }
      }
      return uniqueTypes;
   }

   /** 
    * Method writeXSDHeader writes the start of the schema
    * 
    * @param pXsdWriter schema writer
    * @throws IOException 
    * 
    * @author kaufmann 
    */
   private void writeXSDHeader(BufferedWriter pXsdWriter)
      throws IOException
   {
      pXsdWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      pXsdWriter.newLine();
      pXsdWriter.write("<!-- W3C Schema generated by Nvsxml2xsd.java -->");
      pXsdWriter.newLine();
      pXsdWriter.write("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
            + " elementFormDefault=\"qualified\">");
      pXsdWriter.newLine();
   }


   /** 
    * Method writeElement writes an elements definition to the schema (and its sub elements)
    * 
    * @param pXsdWriter the schema writer
    * @param pData the element
    * @param psIndent indentation for a more pretty schema ;-)
    * @param pbRoot indicates a root element
    * @param pbList indicates a list element
    * @throws IOException 
    * 
    * @author kaufmann 
    */
   private void writeElement(BufferedWriter pXsdWriter, TagData pData, String psIndent,
                             boolean pbRoot, boolean pbList)
      throws IOException
   {
      if (pData.subElements.size() == 0) {
         // leafs are written as they are (never as reference)
         writeElementHead(pXsdWriter, pData, psIndent, true, pbRoot, pbList);
         writeAnnotation(pXsdWriter, pData, psIndent + "  ");
         pXsdWriter.write(psIndent + "</xs:element>");
         pXsdWriter.newLine();
      } else {
         // write element as non-ref and recursively its subelements
         writeElementHead(pXsdWriter, pData, psIndent, false, pbRoot, pbList);
         writeAnnotation(pXsdWriter, pData, psIndent + "  ");
         if (pData.sType == null) {
            pXsdWriter.write(psIndent + "  <xs:complexType>");
            pXsdWriter.newLine();
            pXsdWriter.write(psIndent + "    <xs:sequence>");
            pXsdWriter.newLine();
            for (TagData subTag : pData.subElements) {
               writeElement(pXsdWriter, subTag, psIndent + "      ", false, pData.sType != null
                     && pData.sType.endsWith("List"));
            }
            pXsdWriter.write(psIndent + "    </xs:sequence>");
            pXsdWriter.newLine();
            pXsdWriter.write(psIndent + "  </xs:complexType>");
            pXsdWriter.newLine();
         }
         pXsdWriter.write(psIndent + "</xs:element>");
         pXsdWriter.newLine();
      }
   }


   /** 
    * Method writeElementHead writes the start tag of an element 
    * 
    * @param pXsdWriter schema writer
    * @param pData element to write
    * @param psIndent indentation for a more pretty schema ;-)
    * @param pbType write type of element
    * @param pbRoot indicates a root element
    * @param pbList indicates a list element
    * @throws IOException
    * 
    * @author kaufmann 
    */
   private void writeElementHead(BufferedWriter pXsdWriter, TagData pData, String psIndent,
                                 boolean pbType, boolean pbRoot, boolean pbList)
      throws IOException
   {
      pXsdWriter.write(psIndent + "<xs:element name=\"" + pData.sTagName + "\"");
      // write the type, if necessary
      if (pbType) {
         // schema type
         pXsdWriter.write(" type=\"xs:" + getSchemaType(pData.sType) + "\"");
      } else if (pData.sType != null) {
         // type defined by the NVS xml file
         pXsdWriter.write(" type=\"" + pData.sType + "\"");
      }
      // root elements will never be a list or mandatory
      if (!pbRoot) {
         String sOptional = _forceOptional == null ? pData.sOptional : _forceOptional;
         // write optional indicator
         if (sOptional != null && sOptional.equals("true")) {
            pXsdWriter.write(" minOccurs=\"0\"");
         }
         // write list indicator
         if (pbList) {
            pXsdWriter.write(" maxOccurs=\"unbounded\"");
         }
      }
      pXsdWriter.write(">");
      pXsdWriter.newLine();
   }


   /**
    * Method getSchemaType transforms the NVS type to a schema type
    *
    *        "string"         => "string"
    *        "String"         => "string"
    *        "integer"        => "integer"
    *        "Integer"        => "integer"
    *        "short"          => "short"
    *        "Short"          => "short"
    *        "boolean"        => "boolean"
    *        "Boolean"        => "boolean"
    *        "date"           => "date"
    *        "java.util.Date" => "date"
    *        "time"           => "time"
    *        otherwise        => "string"
    * @param psType NVS type
    * @return schema type
    *
    * @author kaufmann
    */
   private String getSchemaType(String psType)
   {
      String sNewType = psType.toLowerCase();
      if (sNewType.equals("string")) {
         sNewType = "string";
      } else if (sNewType.equals("integer")) {
         sNewType = "integer";
      } else if (sNewType.equals("short")) {
         sNewType = "short";
      } else if (sNewType.equals("boolean")) {
         sNewType = "boolean";
      } else if (sNewType.equals("date")) {
         sNewType = "date";
      } else if (sNewType.equals("java.util.date")) {
         sNewType = "date";
      } else if (sNewType.equals("time")) {
         sNewType = "time";
      } else {
         sNewType = "string";
      }

      return sNewType;
   }


   /** 
    * Method writeAnnotation writes the annotation info of an element 
    * 
    * @param pXsdWriter schema writer
    * @param pData element to write
    * @param psIndent indentation for a more pretty schema ;-)
    * @throws IOException 
    * 
    * @author kaufmann 
    */
   private void writeAnnotation(BufferedWriter pXsdWriter, TagData pData, String psIndent)
      throws IOException
   {
      pXsdWriter.write(psIndent + "<xs:annotation>");
      pXsdWriter.newLine();
      pXsdWriter.write(psIndent + "  <xs:documentation>");
      for (String s : pData.comments) {
         if (s.trim().length() > 0 && !s.trim().toLowerCase().startsWith("description of member")) {
            pXsdWriter.write(s.replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue")
                  .replaceAll("ß", "ss").replaceAll("Ä", "AE").replaceAll("Ö", "OE")
                  .replaceAll("Ü", "UE"));
            pXsdWriter.newLine();
         }
      }
      pXsdWriter.write(psIndent + "  </xs:documentation>");
      pXsdWriter.newLine();
      pXsdWriter.write(psIndent + "</xs:annotation>");
      pXsdWriter.newLine();
   }


   /** 
    * Method writeXSDFooter writes the end of the schema 
    * 
    * @param pXsdWriter schema writer
    * @throws IOException
    * 
    * @author kaufmann 
    */
   private void writeXSDFooter(BufferedWriter pXsdWriter)
      throws IOException
   {
      pXsdWriter.write("</xs:schema>");
      pXsdWriter.newLine();
   }


   /** 
    * Method calcPath creates the XPath based on the stack passed
    * 
    * @param pTags stack containing the current way through the "tree" to the current element 
    * @return XPath of the element on top of the stack
    * 
    * @author kaufmann 
    */
   private String calcPath(Stack<TagData> pTags)
   {
      StringBuilder sb = new StringBuilder();
      for (TagData t : pTags) {
         if (sb.length() > 0) {
            sb.append("/");
         }
         sb.append(t.sTagName);
      }
      return sb.toString();
   }

   /** 
    * Method adjustPathes corrects the XPathes of all elements "below" the newly created
    * intermediate element
    * 
    * @param pElements list of elements to change
    * @param psOldPath old path
    * @param psNewPath new path
    * 
    * @author kaufmann 
    */
   private void adjustPathes(List<TagData> pElements, String psOldPath, String psNewPath)
   {
      for (TagData tag : pElements) {
         for (int i = 0; i < tag.paths.size(); i++) {
            tag.paths.set(i, tag.paths.get(i).replaceAll(psOldPath, psNewPath));
         }
         adjustPathes(tag.subElements, psOldPath, psNewPath);
      }
   }


   /** 
    * This class represents a single xml tag. 
    * 
    * <p> 
    * Copyright &copy; 2006, i:FAO 
    * 
    * @author kaufmann 
    */
   private class TagData
   {
      private String sTagName;
      private String sOptional;
      private String sType;
      private List<TagData> subElements;
      private List<String> comments;
      private List<String> paths;

      private TagData()
      {
         initLists();
      }

      private void initLists()
      {
         subElements = new ArrayList<TagData>();
         comments = new ArrayList<String>();
         paths = new ArrayList<String>();
      }

      /**
       * Method toString
       * overrides @see java.lang.Object#toString()
       *
       * @return
       *
       * @author kaufmann
       */
      @Override
      public String toString()
      {
         StringBuilder sb = new StringBuilder();
         sb.append("<").append(sTagName);
         if (sType != null) {
            sb.append(" type=\"").append(sType).append("\"");
         }
         if (sOptional != null) {
            sb.append(" optional=\"").append(sOptional).append("\"");
         }
         sb.append(">\nPath :").append(paths);
         sb.append("\nSubElements:");
         for (TagData sub : subElements) {
            sb.append("<").append(sub.sTagName).append("> ");
         }
         sb.append("\nComments:").append(comments.size()).append("\n\n");

         return sb.toString();
      }

      /**
       * Method isSame compares this tag with another one
       *
       * @param pOther the other tag to compare
       * @return true, if both tags are defined the same way
       *
       * @author kaufmann
       */
      public boolean isSame(TagData pOther)
      {
         // check the tag name
         if (!sTagName.equals(pOther.sTagName)) {
            return false;
         }

         // check the tag type
         if (sType == null && pOther.sType != null) {
            return false;
         }
         if (sType != null && !sType.equals(pOther.sType)) {
            return false;
         }

         // check the number of sub tags
         if (subElements.size() != pOther.subElements.size()) {
            return false;
         }

         // check the definitions of the sub tags
         for (int i = 0; i < subElements.size(); i++) {
            if (!subElements.get(i).isSame(pOther.subElements.get(i))) {
               return false;
            }
         }

         // if we are here, the tags are the same
         return true;
      }

      /**
       * Compares this tag's type with the type of another one
       *
       * @param pOther the other tag to compare
       * @return true, if both tags are defined the same way
       *
       * @author kaufmann
       */
      public boolean isSameType(TagData pOther)
      {

         // check the tag type
         if (sType == null && pOther.sType != null) {
            return false;
         }
         if (sType != null && !sType.equals(pOther.sType)) {
            return false;
         }

         // check the number of sub tags
         if (subElements.size() != pOther.subElements.size()) {
            return false;
         }

         // check the definitions of the sub tags
         for (int i = 0; i < subElements.size(); i++) {
            if (!subElements.get(i).isSame(pOther.subElements.get(i))) {
               return false;
            }
         }

         // if we are here, the tags are the same
         return true;
      }
   }

}
