package net.ifao.plugins.editor.testcase;


public class TAnnotation
{

   String value;
   String desc;
   String components;

   public TAnnotation(String psValue, String psDesc, String psComponents)
   {
      value = psValue;
      desc = psDesc;
      components = psComponents;

      if (value == null || value.length() == 0) {
         value = desc;
      }
      if (desc == null || desc.length() == 0) {
         desc = value;
      }
      if (components == null || components.length() == 0) {
         components = "ConsistencyRule|BusinessElment|Testable";
      }

   }


}
