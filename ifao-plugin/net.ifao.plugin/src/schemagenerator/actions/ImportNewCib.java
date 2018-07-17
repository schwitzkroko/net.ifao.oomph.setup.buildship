package schemagenerator.actions;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import net.ifao.tools.schemaconversion.localtoglobalelements.Util;
import net.ifao.xml.XmlObject;
import schemagenerator.actions.jaxb.ImportWsdl;


public class ImportNewCib
{

   private File _ffDirectory;
   private String _sWsdlFile;
   private String _sBaseArctic;

   public ImportNewCib(String sBaseArctic, String psWsdlFile)
   {
      _sWsdlFile = psWsdlFile;
      _sBaseArctic = sBaseArctic;
      _ffDirectory =
         new File(ifaoplugin.Util.getProviderDataRootDirectory(sBaseArctic, "net/ifao/newcib"));
   }

   public String start(OutputStream swtConsoleStream)
   {

      if (_ffDirectory.exists()) {

         PrintStream pOut = new PrintStream(swtConsoleStream);

         ImportWsdl importWsdl = new ImportWsdl(pOut);

         try {
            importWsdl.importWsdl(_sWsdlFile, _ffDirectory.getAbsolutePath(),
                  "net.ifao.newcib.soap", true);

            // validate the possible enumerations
            validateArcticResponseDtd(pOut);
         }
         catch (Exception e) {
            e.printStackTrace(pOut);
         }
         pOut.println("Finished");

         return "";
      } else {
         return "Directory " + _ffDirectory.getAbsolutePath() + " does not exist";
      }
   }

   private void validateArcticResponseDtd(PrintStream pOut)
      throws IOException
   {
      // get the xml object
      XmlObject dataXsd =
         new XmlObject(new File(_ffDirectory, "net/ifao/newcib/soap/type/data.xsd"))
               .getFirstObject();

      StringBuilder sbCode = getEnumAncillaryCodeType(dataXsd);
      if (sbCode != null) {
         StringBuilder sbMapper = new StringBuilder();

         StringBuilder sbDep = getEnumDependencyType(dataXsd, sbMapper);
         StringBuilder sbMeasure = getEnumAncillaryMeasure(dataXsd);

         File createMapper = createMapper(sbMapper.toString());
         pOut.println("\n------------------------------------------" + "\nATTENTION: "
               + createMapper.getName() + " was changed "
               + "\n-----------------------------------------");

         // read the ArcticResponse.dtd
         File arcticResponseDtd = new File(_sBaseArctic, "conf/definitions/ArcticResponse.dtd");

         ArrayList<String> readFile = Util.readFile(arcticResponseDtd.getAbsolutePath());
         boolean bChanged = false;
         for (int i = 0; i < readFile.size(); i++) {
            String sLine = readFile.get(i).trim();
            if (sLine.startsWith("<!ENTITY % EnumAncillaryCodeType ")) {
               String sNewType = sbCode.toString();
               if (!sLine.equals(sNewType)) {
                  // update the ArcticResponse.dtd
                  readFile.set(i, sNewType);
                  bChanged = true;
               }
            } else if (sLine.startsWith("<!ENTITY % EnumDependencyType ")) {
               String sNewType = sbDep.toString();
               if (!sLine.equals(sNewType)) {
                  // update the ArcticResponse.dtd
                  readFile.set(i, sNewType);
                  bChanged = true;
               }
            } else if (sLine.startsWith("<!ENTITY % EnumAncillaryMeasure ")) {
               String sNewType = sbMeasure.toString();
               if (!sLine.equals(sNewType)) {
                  // update the ArcticResponse.dtd
                  readFile.set(i, sNewType);
                  bChanged = true;
               }
            }
         }
         if (bChanged) {
            Util.writeFile(readFile, arcticResponseDtd.getAbsolutePath(), false);
            pOut.println("\n------------------------------------------"
                  + "\nATTENTION: ArcticResponse.dtd was changed "
                  + "\n-----------------------------------------");
         }
      }
   }

   private StringBuilder getEnumAncillaryMeasure(XmlObject dataXsd)
   {
      XmlObject cibMeasure = dataXsd.findSubObject("simpleType", "name", "Measure");
      if (cibMeasure == null) {
         return null;
      }
      StringBuilder sbCode = new StringBuilder("<!ENTITY % EnumAncillaryMeasure \"(");
      XmlObject[] subObject = cibMeasure.createObject("restriction").getObjects("enumeration");
      int iStartLen = sbCode.length();
      for (XmlObject enumeration : subObject) {
         if (sbCode.length() > iStartLen) {
            sbCode.append(" | ");
         }
         sbCode.append(enumeration.getAttribute("value"));
      }
      sbCode.append(")\">");
      return sbCode;
   }

   private StringBuilder getEnumDependencyType(XmlObject dataXsd, StringBuilder sb)
   {
      XmlObject cibDependencies = dataXsd.findSubObject("complexType", "name", "CibDependencies");

      StringBuilder sbDep = new StringBuilder("<!ENTITY % EnumDependencyType \"(");
      int iStartLen = sbDep.length();
      // add the generics
      XmlObject[] subObject =
         dataXsd.createObject("simpleType", "name", "DependencyType", true)
               .createObject("restriction").getObjects("enumeration");
      for (XmlObject dependencyType : subObject) {
         if (sbDep.length() > iStartLen) {
            sbDep.append(" | ");
         }
         sbDep.append(dependencyType.getAttribute("value"));
      }
      subObject = cibDependencies.createObject("sequence").getObjects("element");

      for (XmlObject element : subObject) {
         String sType = element.getAttribute("type");
         if (sType.length() == 0) {
            XmlObject simpleType = element.getObject("simpleType");
            if (simpleType != null) {
               XmlObject restriction = simpleType.getObject("restriction");
               if (restriction != null) {
                  sType = restriction.getAttribute("base");
               }
            }
         }
         if (sType.endsWith("ArrayOfDependencies")) {
            // ignore this
            sb.append("      for (CibDependency cibDependency : pCibDependencies.getDependencyList().getItem()) {\n");
            sb.append("         addDependency(pResAncillaryInfoItem,\n");
            sb.append("               ResEnumDependencyType.valueOf(cibDependency.getType().value().toUpperCase()),\n");
            sb.append("               cibDependency.getCode());\n");
            sb.append("      }\n");
            sb.append("\n");
         } else {
            if (sbDep.length() > iStartLen) {
               sbDep.append(" | ");
            }
            String name = element.getAttribute("name");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            String sName =
               upperCase(name).toLowerCase().replaceAll("except_", "exception_").toUpperCase();
            if (sType.contains("ArrayOfString")) {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         for (String s" + name + " : pCibDependencies.get" + name
                     + "().getItem()) {\n");
               sb.append("            if (s" + name + ".length() > 0) {\n");
               sb.append("               addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ", s" + name + ");\n");
               sb.append("            }\n");
               sb.append("         }\n");
               sb.append("      }\n");
            } else if (sType.contains("ArrayOfCibCode")) {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         for (CibCode cc" + name + " : pCibDependencies.get" + name
                     + "().getItem()) {\n");
               sb.append("            if (cc" + name + " != null) {\n");
               sb.append("               addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ", cc" + name + ".getCode());\n");
               sb.append("            }\n");
               sb.append("         }\n");
               sb.append("      }\n");
            } else if (sType.contains("string")) {
               sbDep.append(sName);
               sb.append("      if ((pCibDependencies.get" + name
                     + "() != null) && (pCibDependencies.get" + name + "().length() > 0)) {\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ",\n");
               sb.append("               pCibDependencies.get" + name + "());\n");
               sb.append("      }\n");
            } else if (sType.contains("DependencyDateRangeAttribute")) {
               sbDep.append(sName + "_FROM | " + sName + "_TO");
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + "_FROM,\n");
               sb.append("               pCibDependencies.get" + name
                     + "().getValueFrom().getValue()).setUnit(getResEnumAncillaryMeasure(\n");
               sb.append("               pCibDependencies.get" + name
                     + "().getValueFrom().getUnit().value()));\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + "_TO,\n");
               sb.append("               pCibDependencies.get" + name
                     + "().getValueTo().getValue()).setUnit(getResEnumAncillaryMeasure(\n");
               sb.append("               pCibDependencies.get" + name
                     + "().getValueTo().getUnit().value()));\n");
               sb.append("      }\n");
            } else if (sType.contains("DependencyDateRange")) {
               sbDep.append(sName + "_FROM | " + sName + "_TO");
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + "_FROM,\n");
               sb.append("               pCibDependencies.get" + name + "().getDateFrom());\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + "_TO,\n");
               sb.append("               pCibDependencies.get" + name + "().getDateTo());\n");
               sb.append("      }\n");
            } else if (sType.contains("DependencySingleAttribute")) {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ",\n");
               sb.append("               pCibDependencies.get" + name
                     + "().getValue()).setUnit(getResEnumAncillaryMeasure(\n");
               sb.append("               pCibDependencies.get" + name + "().getUnit().value()));\n");
               sb.append("      }\n");
            } else if (sType.contains("CubeChannel")) {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         for (CubeChannel cc" + name + " : pCibDependencies.get" + name
                     + "().getItem()) {\n");
               sb.append("            if (cc" + name + " != null) {\n");
               sb.append("               addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ", cc" + name + ".name());\n");
               sb.append("            }\n");
               sb.append("         }\n");
               sb.append("      }\n");

            } else if (sType.contains("boolean")) {
               sbDep.append(sName);
               //The boolean is actually Boolean and we can check whether it is != null  
               sb.append("      if ((pCibDependencies.is" + name + "() != null)) {\n");
               sb.append("     addDependency(pResAncillaryInfoItem, ResEnumDependencyType." + sName
                     + ", pCibDependencies.is" + name + "());\n");
               sb.append("      }\n");
               
            } else if (sType.contains("ArrayOfChannelType")) {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         for (ChannelType ct" + name + " : pCibDependencies.get" + name
                     + "().getItem()) {\n");
               sb.append("            if (ct" + name + " != null) {\n");
               sb.append("               addDependency(pResAncillaryInfoItem, ResEnumDependencyType."
                     + sName + ", ct" + name + ".name());\n");
               sb.append("            }\n");
               sb.append("         }\n");
               sb.append("      }\n");
            } else {
               sbDep.append(sName);
               sb.append("      if (pCibDependencies.get" + name + "() != null) {\n");
               sb.append("         ERROR: Invalid Type \"" + sType + "\"\n");
               sb.append("         Please add a handling within the following file:\n");
               sb.append("         /ifaoPlugin/src/schemagenerator/actions/ImportNewCib.java\n");
               sb.append("      }\n");
            }
            sb.append("\n");
         }
      }
      sbDep.append(")\">");
      return sbDep;
   }

   private StringBuilder getEnumAncillaryCodeType(XmlObject dataXsd)
   {
      XmlObject cibCodeType = dataXsd.findSubObject("simpleType", "name", "CibCodeType");
      if (cibCodeType == null) {
         return null;
      }
      StringBuilder sbCode = new StringBuilder("<!ENTITY % EnumAncillaryCodeType \"(");
      XmlObject[] subObject = cibCodeType.createObject("restriction").getObjects("enumeration");
      int iStartLen = sbCode.length();
      for (XmlObject enumeration : subObject) {
         if (sbCode.length() > iStartLen) {
            sbCode.append(" | ");
         }
         sbCode.append(enumeration.getAttribute("value"));
      }
      sbCode.append(")\">");
      return sbCode;
   }

   private String upperCase(String attribute)
   {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < attribute.length(); i++) {
         char c = attribute.charAt(i);
         if ((c >= 'a') && (c <= 'z')) {
            sb.append((char) (c - 32));
         } else {
            if ((sb.length() > 0) && !sb.toString().endsWith("_")) {
               sb.append("_");
            }
            sb.append(c);
         }
      }
      return sb.toString();
   }

   private File createMapper(String psValues)
      throws IOException
   {
      StringBuilder sb = new StringBuilder();
      sb.append("package net.ifao.arctic.agents.newcib.wsdl;\n");
      sb.append("\n");
      sb.append("import javax.xml.datatype.XMLGregorianCalendar;\n");
      sb.append("\n");
      sb.append("import net.ifao.arctic.xml.response.*;\n");
      sb.append("import net.ifao.arctic.xml.response.types.*;\n");
      sb.append("import net.ifao.newcib.soap.type.*;\n");
      sb.append("\n");
      sb.append("\n");
      sb.append("/** \n");
      sb.append("* This class implements a DependencyTypeMapper. The mapper can be used, to map a \n");
      sb.append("* germanwings response to the arctic response \n");
      sb.append("* <p> \n");
      sb.append("* <b>This class is automatically generated</b> \n");
      sb.append("* <p> \n");
      sb.append("* Copyright &copy; 2012, i:FAO \n");
      sb.append("* \n");
      sb.append("* @author brod \n");
      sb.append("*/\n");
      sb.append("public class DependencyTypeMapper\n");
      sb.append("{\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * starts the mapping, by copying the CibDependencies into the arctic\n");
      sb.append("    * response AncillaryInfoItem\n");
      sb.append("    * \n");
      sb.append("    * @param pCibDependencies cib dependencies object\n");
      sb.append("    * @param pResAncillaryInfoItem res ancillary info item object\n");
      sb.append("    * \n");
      sb.append("    * @author brod \n");
      sb.append("    */\n");
      sb.append("   public static void startMapping(CibDependencies pCibDependencies,\n");
      sb.append("                                   ResAncillaryInfoItem pResAncillaryInfoItem)\n");
      sb.append("   {\n");
      sb.append("\n" + psValues + "\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("   * adds a dependency. \n");
      sb.append("   * \n");
      sb.append("   * @param presAncillaryInfoItem ancillary info item object \n");
      sb.append("   * @param pType type String \n");
      sb.append("   * @param psCode code String \n");
      sb.append("   * @return dependency \n");
      sb.append("   * \n");
      sb.append("   * @author brod \n");
      sb.append("   */\n");
      sb.append("   private static ResDependency addDependency(ResAncillaryInfoItem presAncillaryInfoItem,\n");
      sb.append("                                              ResEnumDependencyType pType, String psCode)\n");
      sb.append("   {\n");
      sb.append("      ResDependency vDependency = new ResDependency();\n");
      sb.append("      if (psCode == null) {\n");
      sb.append("         return vDependency;\n");
      sb.append("      }\n");
      sb.append("      ResDependencies dependencies = presAncillaryInfoItem.getDependencies();\n");
      sb.append("      if (dependencies == null) {\n");
      sb.append("         dependencies = new ResDependencies();\n");
      sb.append("         presAncillaryInfoItem.setDependencies(dependencies);\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      vDependency.setName(pType);\n");
      sb.append("      vDependency.setValue(psCode);\n");
      sb.append("      dependencies.addDependency(vDependency);\n");
      sb.append("      return vDependency;\n");
      sb.append("\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /**\n");
      sb.append("    * @param pResAncillaryInfoItem ancillary info item object\n");
      sb.append("    * @param pType type Object\n");
      sb.append("    * @param pDate date object\n");
      sb.append("    */\n");
      sb.append("   private static void addDependency(ResAncillaryInfoItem pResAncillaryInfoItem,\n");
      sb.append("                                     ResEnumDependencyType pType, XMLGregorianCalendar pDate)\n");
      sb.append("   {\n");
      sb.append("      if (pDate != null) {\n");
      sb.append("         addDependency(pResAncillaryInfoItem, pType,\n");
      sb.append("               new java.text.SimpleDateFormat(\"yyyyMMdd\").format(pDate.toGregorianCalendar()\n");
      sb.append("                     .getTime()));\n");
      sb.append("      }\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /**\n");
      sb.append("    * @param pResAncillaryInfoItem ancillary info item object\n");
      sb.append("    * @param pType type object\n");
      sb.append("    * @param pValue Boolean object\n");
      sb.append("    */\n");
      sb.append("   private static void addDependency(ResAncillaryInfoItem pResAncillaryInfoItem,\n");
      sb.append("                                     ResEnumDependencyType pType, boolean pbValue)\n");
      sb.append("   {\n");
      sb.append("      addDependency(pResAncillaryInfoItem, pType, Boolean.valueOf(pbValue).toString());\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * returns a ResEnumAncillaryMeasure (or null)\n");
      sb.append("    * \n");
      sb.append("    * @param psValue value String\n");
      sb.append("    * @return return ResEnumAncillaryMeasure\n");
      sb.append("    * \n");
      sb.append("    * @author brod\n");
      sb.append("    */\n");
      sb.append("   private static ResEnumAncillaryMeasure getResEnumAncillaryMeasure(String psValue)\n");
      sb.append("   {\n");
      sb.append("      if (psValue != null) {\n");
      sb.append("         return ResEnumAncillaryMeasure.valueOf(psValue);\n");
      sb.append("      }\n");
      sb.append("      return null;\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("}\n");
      sb.append("\n");
      File file =
         new File(_sBaseArctic, "src/net/ifao/arctic/agents/newcib/wsdl/DependencyTypeMapper.java");
      Utils.writeFile(file, sb.toString());
      return file;
   }
}
