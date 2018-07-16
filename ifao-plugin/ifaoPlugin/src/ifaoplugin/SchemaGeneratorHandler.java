package ifaoplugin;


import org.eclipse.core.commands.*;

import schemagenerator.SchemaGenerator;


public class SchemaGeneratorHandler
   extends AbstractHandler

{

   @Override
   public Object execute(ExecutionEvent arg0)
      throws ExecutionException
   {
      SchemaGenerator.openSchemaGenerator();
      return null;
   }
}
