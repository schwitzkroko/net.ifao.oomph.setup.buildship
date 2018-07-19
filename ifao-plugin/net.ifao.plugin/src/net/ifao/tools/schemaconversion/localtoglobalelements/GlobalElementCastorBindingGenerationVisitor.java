package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Iterator;

import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.traverseElementTree;
import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.firstCharToUpperCase;


/**
 * <p>This class contains the global element castor binding generation visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class GlobalElementCastorBindingGenerationVisitor
   extends GenerationVisitorBase
   implements IElementVisitor
{

   // element binding: element path name separator
   protected static final char ELEMENT_PATH_NAME_SEPARATOR = '/';


   /**
    * <p>This class contains the JAVA class name generator
    * @author Jochen Pinder
    */
   static class JavaClassNameGenerator
   {
      // JAVA class name hash map,
      // which maps JAVA class names, which have been converted into lower case, to the number of occurrences
      private HashMap<String, Integer> _hsetLowerCaseJavaClassNameToCount;

      /**
       * <p>Constructor.
       */
      public JavaClassNameGenerator()
      {
         _hsetLowerCaseJavaClassNameToCount = new HashMap<String, Integer>();
      }

      /**
       * <p>This method generates the JAVA class name.
       * @param psElementName element name
       * @param psElementPrefixName element prefix name
       * @return JAVA class name
       * @author Jochen Pinder
       */
      public String generate(String psElementName, String psElementPrefixName)
      {
         // Create the JAVA class name
         String sJavaClassName =
            (psElementPrefixName != null) ? firstCharToUpperCase(psElementPrefixName)
                  + firstCharToUpperCase(psElementName) : firstCharToUpperCase(psElementName);

         // Avoid double occurrences of the same JAVA class name
         String sLowerCaseJavaClassName = sJavaClassName.toLowerCase();
         if (_hsetLowerCaseJavaClassNameToCount.containsKey(sLowerCaseJavaClassName)) {
            int iCount =
               _hsetLowerCaseJavaClassNameToCount.get(sLowerCaseJavaClassName).intValue() + 1;
            _hsetLowerCaseJavaClassNameToCount
                  .put(sLowerCaseJavaClassName, Integer.valueOf(iCount));
            sJavaClassName = sJavaClassName + iCount;
         } else {
            _hsetLowerCaseJavaClassNameToCount.put(sLowerCaseJavaClassName, Integer.valueOf(1));
         }

         return sJavaClassName;
      }

      /**
       * <p>This method generates the JAVA class name.
       * @param psElementName element name
       * @return JAVA class name
       * @author Jochen Pinder
       */
      public String generate(String psElementName)
      {
         return generate(psElementName, null);
      }
   }


   /**
    * <p>This class contains the complex local element castor binding generation visitor.
    * @author Jochen Pinder
    */
   class ComplexLocalElementCastorBindingGenerationVisitor
      extends GenerationVisitorBase
      implements IElementVisitor
   {
      // element path name stack, which contains the current element path name
      // from the lowest complex global element above in the hierarchy
      private Stack<String> _stackElementPathName;

      /**
       * <p>Constructor.
       * @param parentElement parent complex global schema element
       * @param lstLine generated data
       * @param schemaHandler schema handler
       */
      public ComplexLocalElementCastorBindingGenerationVisitor(TElement parentElement,
                                                               ArrayList<String> lstLine,
                                                               SchemaHandler schemaHandler)
      {
         super();

         _lstLine = lstLine;
         _schemaHandler = schemaHandler;

         _stackElementPathName = new Stack<String>();
         _stackElementPathName.push(parentElement.getName());
      }

      /**
       * <p>This method performs the visit before visiting the child schema elements.
       * @param ptElement schema element
       * @author Jochen Pinder
       */
      @Override
      public void visitBeforeChildren(TElement ptElement)
      {
         // Check for a simple type schema element
         if (!ptElement.isComplexElement())
            return;

         // Store the schema element name
         _lstComplexLocalElementName.add(ptElement.getName());

         // Determine the JAVA class name
         String sJavaClassName =
            _javaClassNameGenerator.generate(ptElement.getName(), _stackElementPathName.peek());

         // Add the schema element name to the element path name stack
         _stackElementPathName.push(ptElement.getName());

         // Create the schema element path name
         String sElementPathName = getElementPathName();

         // Write the castor element binding
         _schemaHandler.createCastorElementBinding(sElementPathName, sJavaClassName);
      }

      /**
       * <p>This method determines if the child schema elements are visited.
       * @param ptElement schema element
       * @return true if the child schema elements are visited, false otherwise
       * @author Jochen Pinder
       */
      @Override
      public boolean visitChildren(TElement ptElement)
      {
         return true;
      }

      /**
       * <p>This method performs the visit after visiting the child schema elements.
       * @param ptElement schema element
       * @author Jochen Pinder
       */
      @Override
      public void visitAfterChildren(TElement ptElement)
      {
         // Check for a simple type schema element
         if (!ptElement.isComplexElement())
            return;

         // Remove the schema element name from the element path name stack
         _stackElementPathName.pop();
      }

      /**
       * <p>This method gets the element path name.
       * @return element path name
       * @author Jochen Pinder
       */
      private String getElementPathName()
      {
         StringBuffer sb = new StringBuffer();

         Iterator<String> it = _stackElementPathName.iterator();
         while (it.hasNext()) {
            sb.append(ELEMENT_PATH_NAME_SEPARATOR);
            sb.append(it.next());
         }

         return sb.toString();
      }
   }


   // element name
   //  - hash set for complex global schema elements
   //  - list for complex local schema elements
   private HashSet<String> _hsetComplexGlobalElementName;
   protected ArrayList<String> _lstComplexLocalElementName;

   // JAVA class name generator
   protected JavaClassNameGenerator _javaClassNameGenerator;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public GlobalElementCastorBindingGenerationVisitor()
   {
      super();

      _hsetComplexGlobalElementName = new HashSet<String>();
      _lstComplexLocalElementName = new ArrayList<String>();

      _javaClassNameGenerator = new JavaClassNameGenerator();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method commences the global element castor binding generation visitor.
     * @author Jochen Pinder
     */
   public void commence()
   {

      _schemaHandler.createCastorBindingHeader();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method terminates the global element castor binding generation visitor.
     * @author Jochen Pinder
     */
   public void terminate()
   {

      _schemaHandler.createCastorBindingFooter();
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the complex global element binding count.
    * @return complex global element binding count
    * @author Jochen Pinder
    */
   public int getComplexGlobalElementBindingCount()
   {

      return _hsetComplexGlobalElementName.size();
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the complex local element binding count.
    * @return complex local element binding count
    * @author Jochen Pinder
    */
   public int getComplexLocalElementBindingCount()
   {

      return _lstComplexLocalElementName.size();
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit before visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitBeforeChildren(TElement ptElement)
   {

      // Check for a simple type schema element
      if (!ptElement.isComplexElement())
         return;

      // For a double different schema element:
      if (ptElement.isDoubleDifferentElement()) {
         ComplexLocalElementCastorBindingGenerationVisitor complexLocalElementCastorBindingGenerationVisitor =
            new ComplexLocalElementCastorBindingGenerationVisitor(ptElement.getParentElement(),
                  _lstLine, _schemaHandler);
         traverseElementTree(ptElement, complexLocalElementCastorBindingGenerationVisitor);
      }

      // For all other schema elements:
      else {

         // Get the schema element name
         String sElementName = ptElement.getName();

         // Check if the binding for the schema element name has already been created
         if (!_hsetComplexGlobalElementName.add(sElementName))
            return;

         // Determine the JAVA class name
         String sJavaClassName = _javaClassNameGenerator.generate(sElementName);

         // Write the castor element binding
         _schemaHandler.createCastorElementBinding(ELEMENT_PATH_NAME_SEPARATOR + sElementName,
               sJavaClassName);
      }

   } // end visitBeforeChilds

   //------------------------------------------------------------------------------

   /**
    * <p>This method determines if the child schema elements are visited.
    * @param ptElement schema element
    * @return true if the child schema elements are visited, false otherwise
    * @author Jochen Pinder
    */
   @Override
   public boolean visitChildren(TElement ptElement)
   {

      return (!ptElement.isDoubleDifferentElement());
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit after visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitAfterChildren(TElement ptElement)
   {}
}
