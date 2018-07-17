package net.ifao.plugins;
import java.io.*;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;


public class FormatCode
{
   @SuppressWarnings("unchecked")
   public static void main(String[] args)
      throws Exception
   {
      String sV1_5 = "@SuppressWarnings({\"unused\",\"unchecked\"})\n";

      String s = "class Test { \n /**\n * test\n **/" + sV1_5 + "public void test() {System.out.print(123); List<String> offersList   =   new Vector();} }";
      
       BufferedReader reader = new BufferedReader(new FileReader("c:/Programme/eclipse/eclipse/runtime-EclipseApplication/Test/test/txt/Base64.java"));
       String sLine;
       s = "";
       while ((sLine = reader.readLine())!=null){
          s+=sLine+"\n";
       }
      Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();      
      options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
      options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
      options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
      
      CodeFormatter codeFormatter = org.eclipse.jdt.core.ToolFactory.createCodeFormatter(options);
      TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, s, 0, s.length(), 0,
            null);
      IDocument doc = new Document(s);
      edit.apply(doc);
      s = doc.get();
      System.out.println(s);
   }
}
