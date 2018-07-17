package ifaoplugin;

import java.util.*;
import java.util.List;
import java.util.regex.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Class JsonToTree tries to create a tree view for a JSON object
 *
 * <p>
 * Copyright &copy; 2015, i:FAO Group GmbH
 * @author kaufmann
 */
public class JsonToTree
{
   private static final String NO_NEW_ELEMENT = "_______NO_NEW_ELEMENT_______";
   private static final Font TREE_FONT = new Font(Display.getDefault(), "Courier New", 9, SWT.NONE);

   /**
    * Builds the tree
    *
    * @param pContainer SWT container for the tree object
    * @param psRootName root element's name
    * @param psJson JSON object as String
    *
    * @author kaufmann
    */
   public static void buildTree(Composite pContainer, String psRootName, String psJson) {
      Tree tree = new Tree(pContainer, SWT.BORDER);
      tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

      TreeItem rootItem = new TreeItem(tree, SWT.NONE);
      rootItem.setText(psRootName);
      rootItem.setFont(TREE_FONT);

      showObject(NO_NEW_ELEMENT, psJson, rootItem);
      rootItem.setExpanded(true);

   }
   
   /**
    * Creates a tree item for an object
    *
    * @param psName name of the object; used as text for the tree item
    * @param psContent content of the object as JSON string 
    * @param pParent parent tree item
    *
    * @author kaufmann
    */
   private static void showObject(String psName, String psContent, TreeItem pParent)
   {
      psContent = psContent.trim();

      // add the element to the tree
      Pattern p = Pattern.compile("\\{(.*)\\}");
      Matcher m = p.matcher(psContent);
      if (m.find()) {
         String sContent = m.group(1).trim();
         TreeItem treeItem;
         if (psName.equals(NO_NEW_ELEMENT)) {
            treeItem = pParent;
         } else {
            treeItem = buildTreeItem(pParent, psName);
         }

         // handle subelements
         String[] sSubElements = splitAtComma(sContent);
         for (String s : sSubElements) {
            String sName = s.substring(0, s.indexOf(':')).trim();
            String sValue = s.substring(s.indexOf(':') + 1).trim();
            if (sValue.startsWith("{")) {
               showObject(sName, sValue, treeItem);
            } else if (sValue.startsWith("[")) {
               showArray(sName, sValue, treeItem);
            } else {
               buildTreeItem(treeItem, s);
               treeItem.setExpanded(true);
            }
         }
         pParent.setExpanded(true);
      }
   }


   /**
    * Creates a tree element for an array
    *
    * @param psName name of the object; used as text for the tree item
    * @param psContent content of the array as JSON string
    * @param pParent parent tree item
    *
    * @author kaufmann
    */
   private static void showArray(String psName, String psContent, TreeItem pParent)
   {
      psContent = psContent.trim();

      Pattern p = Pattern.compile("\\[(.*)\\]");
      Matcher m = p.matcher(psContent);
      if (m.find()) {
         // add the element to the tree
         String sContent = m.group(1).trim();
         TreeItem treeItem = buildTreeItem(pParent, psName + "*");

         // handle empty array
         if (sContent.trim().length() == 0) {
            buildTreeItem(treeItem, "<empty>");
            treeItem.setExpanded(true);
         } else {
            // handle array elements 
            String[] sArrayElement = splitAtComma(sContent);
            int iCounter = 0;
            for (String s : sArrayElement) {
               if (s.startsWith("{")) {
                  // unnamed object
                  showObject("<" + iCounter++ + ">", s, pParent);
               } else {
                  if (s.indexOf(":") >= 0) {
                     String sName = s.substring(0, s.indexOf(':')).trim();
                     String sValue = s.substring(s.indexOf(':') + 1).trim();
                     if (sValue.startsWith("{")) {
                        showObject("<" + iCounter++ + "> " + sName, sValue, treeItem);
                     } else if (sValue.startsWith("[")) {
                        showArray("<" + iCounter++ + "> " + sName, sValue, treeItem);
                     } else {
                        buildTreeItem(treeItem, "<" + iCounter++ + "> " + s);
                        treeItem.setExpanded(true);
                     }
                  } else {
                     buildTreeItem(treeItem, "<" + iCounter++ + "> " + s);
                     treeItem.setExpanded(true);
                  }
               }
            }
         }
         pParent.setExpanded(true);
      }
   }

   /**
    * Splits the JSON object string at relevant commata
    *
    * @param psJSON JSON object string
    * @return array of elements of the object
    *
    * @author kaufmann
    */
   private static final String[] splitAtComma(String psJSON)
   {
      List<String> result = new ArrayList<String>();

      StringBuilder sbSubString = new StringBuilder();
      int nBracketLevel = 0;
      boolean bInString = false;
      for (int i = 0; i < psJSON.length(); i++) {
         char charAt = psJSON.charAt(i);
         if (charAt == '{' || charAt == '[') {
            nBracketLevel++;
            sbSubString.append(psJSON.charAt(i));
         } else if (charAt == '}' || charAt == ']') {
            sbSubString.append(psJSON.charAt(i));
            nBracketLevel--;
         } else if (charAt == '"') {
            sbSubString.append(psJSON.charAt(i));
            bInString = !bInString;
         } else if (charAt == ',' && nBracketLevel == 0 && !bInString) {
            result.add(sbSubString.toString().trim());
            sbSubString = new StringBuilder();
         } else {
            sbSubString.append(psJSON.charAt(i));
         }
      }
      result.add(sbSubString.toString().trim());
      return result.toArray(new String[]{});
   }


   /**
    * Creates a tree item
    *
    * @param pParent parent element of the tree
    * @param psText name of the tree element
    * @return created tree element
    *
    * @author kaufmann
    */
   private static TreeItem buildTreeItem(TreeItem pParent, String psText)
   {
      TreeItem treeItem = new TreeItem(pParent, SWT.NONE);
      treeItem.setFont(TREE_FONT);
      treeItem.setText(psText);
      return treeItem;
   }

}
