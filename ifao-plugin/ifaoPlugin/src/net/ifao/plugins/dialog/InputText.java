package net.ifao.plugins.dialog;


import org.eclipse.swt.widgets.*;


public class InputText
   extends InputTextAdapter
{
   private String sText = "";

   public InputText(Class<?> pAbstractUIPlugin, String sInitText, String sHeader, String sTitle)
   {
      super(pAbstractUIPlugin);
      initAdapter();
      getTextInput().setText(sInitText);
      getCLabelType().setText(sTitle);
      getCLabelHeader().setText(sHeader);
   }

   public static void main(String[] args)
   {
      (new InputText(null, "Init", "Input Text", "...")).show();
   }

   @Override
   protected void modifyTextInput(Text pTextInput)
   {
      getButtonFinish().setEnabled(pTextInput.getText().length() > 0);
   }

   @Override
   protected void clickButtonFinish(Button pButtonFinish)
   {
      sText = getTextInput().getText();
      close();
   }

   @Override
   protected void clickButtonCancel(Button pButtonCancel)
   {
      close();
   }

   public String getSelectedText()
   {
      return sText;
   }

}
