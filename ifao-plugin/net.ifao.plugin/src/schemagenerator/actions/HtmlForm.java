package schemagenerator.actions;


import java.net.URLEncoder;
import java.util.*;


/** 
 * This class is the representation of a Html Form element (with
 * it's input parameters) 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class HtmlForm
{
   /** 
    * The private method getElement returns the specific element. 
    * 
    * @param psHtmlCode html code
    * @param pbEndTag if true the endtag will be expected
    * @param psTag the name of the tag
    * @param piCounter a counter if there are multiple elements
    * @return the related element
    * 
    * @author brod 
    */
   private static String getElement(String psHtmlCode, boolean pbEndTag, String psTag, int piCounter)
   {
      String sTag = "<" + psTag.toLowerCase() + " ";
      String sHtmlCode = psHtmlCode.toLowerCase();
      int iPos = sHtmlCode.indexOf(sTag);
      int iCounter = piCounter;
      while (iPos > 0 && iCounter >= 0) {
         if (iCounter == 0) {
            int iEnd;
            if (pbEndTag)
               iEnd = sHtmlCode.indexOf("</" + psTag.toLowerCase() + ">", iPos);
            else
               iEnd = sHtmlCode.indexOf(">", iPos);
            if (iEnd > iPos) {
               String sRet = psHtmlCode.substring(iPos, iEnd);
               return sRet;
            }
         }
         iPos = sHtmlCode.indexOf(sTag, iPos + 1);
         iCounter--;
      }
      return "";
   }

   /** 
    * This method returns the available forms of a html page 
    * 
    * @param psHtmlCode html page
    * @return a list of from (within a html page)
    * 
    * @author brod 
    */
   public static List<HtmlForm> getForms(String psHtmlCode)
   {
      List<HtmlForm> lst = new ArrayList<HtmlForm>();

      int i = 0;
      String sForm;
      while ((sForm = getElement(psHtmlCode, true, "form", i)).length() > 0) {
         lst.add(new HtmlForm(sForm));
         i++;
      }

      return lst;
   }

   /** 
    * The private method getValues returns the values of a html string
    * <p>
    * e.g. "&lt;element name="Test" age="18" /&gt;"<br>
    * would return the hashtable :<br> 
    * [name="Test",age="18"]
    * 
    * @param psHtml html code
    * @param psTag tagname
    * @param piCounter a counter, if there are multiple elements
    * within the html code
    * @return value of a specific tag element
    * 
    * @author brod 
    */
   private static Hashtable<String, String> getValues(String psHtml, String psTag, int piCounter)
   {
      Hashtable<String, String> ht = new Hashtable<String, String>();

      String sTag = ("<" + psTag + " ").toLowerCase();
      int iCounter = piCounter;
      String sHtml = psHtml.toLowerCase();
      int iPos = sHtml.indexOf(sTag);
      while (iPos >= 0 && iCounter >= 0) {
         if (iCounter == 0) {
            int iEnd = sHtml.indexOf(">", iPos);
            if (iEnd > 0) {
               char[] charArray = psHtml.substring(iPos + sTag.length(), iEnd).toCharArray();
               String sQuote = null;
               String sName = "";
               for (int i = 0; i < charArray.length; i++) {
                  char c = charArray[i];
                  switch (c) {
                     case '=':
                        if (sQuote != null)
                           sQuote += c;
                        break;
                     case '\'':
                     case '\"':
                        if (sQuote == null)
                           sQuote = "";
                        else {
                           if (sName.length() > 0)
                              ht.put(sName, sQuote);
                           sQuote = null;
                        }
                        break;
                     case ' ':
                        if (sQuote != null)
                           sQuote += c;
                        else
                           sName = "";
                        break;
                     default:
                        if (sQuote != null)
                           sQuote += c;
                        else
                           sName += c;
                  }
               }
            }
         }
         iCounter--;
         iPos = sHtml.indexOf(sTag, iPos + 1);
      }
      return ht;
   }

   private Hashtable<String, String> formInputs = new Hashtable<String, String>();

   private Hashtable<String, String> formValues;

   /** 
    * Constructor HtmlForm 
    * 
    * @param psHtmlContent The html content (with typically a form)
    * 
    * @author brod 
    */
   public HtmlForm(String psHtmlContent)
   {
      formValues = getValues(psHtmlContent, "form", 0);
      int iPos = 0;
      // get the input values
      Hashtable<String, String> values;
      while ((values = getValues(psHtmlContent, "input", iPos)).size() > 0) {
         String sName = values.get("name");
         if (sName == null)
            sName = values.get("id");
         if (sName != null) {
            putInput(sName, values.get("value"));
         }
         iPos++;
      }
   }

   /** 
    * @return the action value of the form
    * 
    * @author brod 
    */
   public String getAction()
   {
      String sAction = formValues.get("action");
      if (sAction == null)
         return "";
      return sAction;
   }

   /** 
    * @return the request string (of the input parameters)
    * 
    * @author brod 
    */
   public String getInputString()
   {
      String[] keys = formInputs.keySet().toArray(new String[0]);
      String data = "";
      for (int i = 0; i < keys.length; i++) {
         if (i > 0)
            data += "&";
         try {
            data +=
               URLEncoder.encode(keys[i], "UTF-8") + "="
                     + URLEncoder.encode(formInputs.get(keys[i]), "UTF-8");
         }
         catch (Exception e) {
            // ignore this entry
         }
      }
      return data;
   }

   /** 
    * @return the method values (of the form)
    * 
    * @author brod 
    */
   public String getMethod()
   {
      String sMethod = formValues.get("method");
      if (sMethod == null)
         return "GET";
      return sMethod.toUpperCase();
   }

   /** 
    * @return the name of the form (or empty if not defined)
    * 
    * @author brod 
    */
   public String getName()
   {
      String sName = formValues.get("name");
      if (sName == null)
         sName = formValues.get("id");
      if (sName == null)
         sName = "";
      return sName;
   }

   /** 
    * This method changes the Input parameters 
    * 
    * @param psName name of the input field
    * @param psValue value of the input field
    * 
    * @author brod 
    */
   public void putInput(String psName, String psValue)
   {
      String sValue;
      if (psValue == null)
         sValue = "";
      else
         sValue = psValue;
      formInputs.put(psName, sValue);
   }

}
