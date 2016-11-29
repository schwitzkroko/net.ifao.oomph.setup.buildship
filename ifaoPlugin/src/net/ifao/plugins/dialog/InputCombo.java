package net.ifao.plugins.dialog;


import java.util.Arrays;
import org.eclipse.swt.widgets.*;


public class InputCombo
   extends InputComboAdapter
{
   private String sComboCalue = "";
   java.util.Hashtable<String, String> items;

   public InputCombo(Class<?> pAbstractUIPlugin, java.util.Hashtable<String, String> pItems,
                     String selectText)
   {
      super(pAbstractUIPlugin);
      items = pItems;
      initAdapter();

      Object[] keys = pItems.keySet().toArray();
      Arrays.sort(keys);
      for (int i = 0; i < keys.length; i++) {
         getComboSelect().add((String) keys[i]);
      }
      getCLabelType().setText(selectText);
      getTextComment().setText("... please select");
   }

   public static void main(String[] args)
   {
      (new InputCombo(null, new java.util.Hashtable<String, String>(), "...")).show();
   }

   @Override
   protected void clickButtonFinish(Button pButtonFinish)
   {
      // add click functionallity
      sComboCalue = getComboSelect().getText();
      close();
   }

   @Override
   protected void clickButtonCancel(Button pButtonCancel)
   {
      // add click functionallity
      close();
   }

   public String getReturnValue()
   {
      // TODO Auto-generated method stub
      return sComboCalue;
   }

   @Override
   protected void clickComboSelect(Combo pComboSelect)
   {
      try {
         getButtonFinish().setEnabled(getComboSelect().getText().length() > 0);
         String sComment = items.get(getComboSelect().getText());
         if (sComment != null) {
            getTextComment().setText(sComment);

         } else {
            getTextComment().setText("... please select");

         }
      }
      catch (Exception e) {}

   }

   @Override
   protected void modifyTextComment(Text pTextComment)
   {
   // TODO Auto-generated method stub

   }

   @Override
   protected void keyPressedComboSelect(org.eclipse.swt.events.KeyEvent e)
   {
      if (e.character == 13 && getButtonFinish().isEnabled()) {
         clickButtonFinish(getButtonFinish());
      }
      if (e.character == 27) {
         clickButtonCancel(getButtonCancel());
      }

   }

}
