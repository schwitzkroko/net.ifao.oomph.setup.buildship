package net.ifao.tools.sql2java;


import ifaoplugin.*;
import net.ifao.util.Execute;

import org.eclipse.core.commands.*;


public class Sql2JavaHandler
   extends AbstractHandler
{

   @Override
   public Object execute(ExecutionEvent arg0)
      throws ExecutionException
   {
      new Thread()
      {

         @Override
         public void run()
         {
            try {
               // execute/arcticTools/src/net/ifao/tools/dbgenerator/GuiSqlToJava.java
               Execute.start("net.ifao.tools.dbgenerator.GuiSqlToJava", "", true);

            }
            catch (Exception ex) {
               Util.showException(ex);
            }
         }
      }.start();
      return null;
   }

}
