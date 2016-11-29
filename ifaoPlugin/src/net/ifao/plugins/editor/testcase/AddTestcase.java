package net.ifao.plugins.editor.testcase;


import net.ifao.xml.Xml;

import org.eclipse.swt.widgets.*;


public class AddTestcase
   extends AddTestcaseAdapter
{

   private String testCase = "";
   private Xml templates;
   private Xml[] xmlTemplates = null;
   private Xml selectedTemplate = null;

   /**
    * @return the testCase
    */
   public String getTestCase()
   {
      return testCase;
   }

   public AddTestcase(Class pAbstractUIPlugin, Xml pTemplates)
   {
      super(pAbstractUIPlugin);
      templates = pTemplates;
      initAdapter();
   }

   public static void main(String[] args)
   {
      (new AddTestcase(null, null)).show();
   }

   @Override
   protected void initTextTestCaseName(Text pTestCaseName)
   {
   // Init ...
   }

   @Override
   protected void modifyTextTestCaseName(Text pTextTestCaseName)
   {
      // add modify functionallity
      getButtonFinish().setEnabled(pTextTestCaseName.getText().length() > 0);
   }

   @Override
   protected void initButtonCheck(Button pCheck)
   {
   // Init ...
   }

   @Override
   protected void clickButtonCheck(Button pButtonCheck)
   {
      // add click functionallity
      getListTemplates().setEnabled(pButtonCheck.getSelection());

      clickListTemplates(getListTemplates());
   }

   public Xml getSelectedTemplate()
   {
      return selectedTemplate;
   }

   @Override
   protected void initListTemplates(List pTemplates)
   {
      xmlTemplates = templates.getObjects("");
      for (int i = 0; i < xmlTemplates.length; i++) {
         pTemplates.add(xmlTemplates[i].getAttribute("name"));
      }
   }

   @Override
   protected void clickListTemplates(List pListTemplates)
   {
      selectedTemplate = null;
      // add click functionallity
      if (xmlTemplates != null && getButtonCheck().getSelection()) {
         int index = pListTemplates.getSelectionIndex();
         if (index >= 0 && index < xmlTemplates.length) {
            selectedTemplate = xmlTemplates[index].copy();
            getTextDetail().setText(selectedTemplate.getAttribute("description"));
         }
      }

   }

   @Override
   protected void initButtonFinish(Button pFinish)
   {
   // Init ...

   }

   @Override
   protected void clickButtonFinish(Button pButtonFinish)
   {
      // add click functionallity

      testCase = getTextTestCaseName().getText();

      close();
   }

   @Override
   protected void initButtonCancel(Button pCancel)
   {
   // Init ...
   }

   @Override
   protected void clickButtonCancel(Button pButtonCancel)
   {
      // add click functionallity
      close();
   }

   @Override
   protected void modifyTextDetail(Text pTextDetail)
   {
   // make nothing 

   }

   @Override
   protected void keyPressedTextTestCaseName(org.eclipse.swt.events.KeyEvent e)
   {
      if (e.character == 13 && getButtonFinish().isEnabled()) {
         clickButtonFinish(getButtonFinish());
      }
      if (e.character == 27) {
         clickButtonCancel(getButtonCancel());
      }
   }

   @Override
   protected void keyPressedListTemplates(org.eclipse.swt.events.KeyEvent e)
   {
      keyPressedTextTestCaseName(e);
   }

   @Override
   protected void keyPressedButtonCheck(org.eclipse.swt.events.KeyEvent e)
   {
      keyPressedTextTestCaseName(e);
   }

}
