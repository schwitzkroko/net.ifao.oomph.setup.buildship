
package branchswitcher.popup.actions;


public class LoggerAction
{
   StringBuilder sb = new StringBuilder();

   private boolean warning = false;


   public void warning(String line, Exception e)
   {
      warning = true;
      println("WARNING: " + line + " " + e.getMessage());
   }


   public void println(String line)
   {
      sb.append(line + "\n");
      System.out.println(line);
   }


   public boolean hasWarnings()
   {
      return warning;
   }

   @Override
   public String toString()
   {
      return sb.toString();
   }
}
