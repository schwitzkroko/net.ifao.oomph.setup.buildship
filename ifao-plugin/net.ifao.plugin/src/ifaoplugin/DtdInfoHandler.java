package ifaoplugin;


import org.eclipse.core.commands.*;

import dtdinfo.gui.DtdFrame;


public class DtdInfoHandler
   extends AbstractHandler
{
   private static DtdFrame frame;

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
               if (frame == null) {
                  Util.initSwing();
                  frame = new DtdFrame(null);


                  // Validate frames that have preset sizes
                  // Pack frames that have useful preferred size info, e.g. from their layout
                  frame.validate();
               } else {
                  if (!frame.isVisible()) {
                     frame.init();
                  }
               }
               frame.setVisible(true);
            }
            catch (Exception ex) {
               Util.showException(ex);
            }
         }
      }.start();
      return null;
   }

}
