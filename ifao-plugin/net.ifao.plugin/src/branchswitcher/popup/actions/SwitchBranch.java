package branchswitcher.popup.actions;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public class SwitchBranch
   implements IObjectActionDelegate
{


   private java.io.File applicationFolder;

   private Shell shell;


   /**
    * Constructor for Action1.
    */
   public SwitchBranch()
   {
      super();
   }

   /**
    * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
    */
   @Override
   public void setActivePart(IAction action, IWorkbenchPart targetPart)
   {
      // ignore this
      shell = targetPart.getSite().getShell();
   }

   /**
    * @see IActionDelegate#run(IAction)
    */
   @Override
   public void run(IAction action)
   {
      if (applicationFolder != null) {
         try {
            new ProgressMonitorDialog(shell).run(true, true, new CheckoutJob(shell, applicationFolder));
         }
         catch (InvocationTargetException | InterruptedException e) { // NOSONAR
            //should not happen

         }
      }

   }


   /**
    * @see IActionDelegate#selectionChanged(IAction, ISelection)
    */
   @SuppressWarnings("restriction")
   @Override
   public void selectionChanged(IAction action, ISelection selection)
   {
      File application = null;
      try {
         Object firstElement = ((StructuredSelection) selection).getFirstElement();

         if (firstElement instanceof Resource) {
            Resource versionProperties = (Resource) firstElement;
            java.io.File versionPropertiesFile = versionProperties.getRawLocation().toFile().getCanonicalFile();
            // get the folder
            if (versionPropertiesFile.isFile()) {
               application = versionPropertiesFile.getParentFile();
            } else {
               application = versionPropertiesFile;
            }
            // search for a folder which contains the version properties
            application = searchVersionPropertiesFolder(application, 2);
         } else {
            application = null;
         }
      }
      catch (Exception e) { // NOSONAR
         application = null;
      }
      finally {
         // finally set the application folder
         this.applicationFolder = application;
         action.setEnabled(applicationFolder != null);
      }
   }

   private File searchVersionPropertiesFolder(File application, int counter)
      throws IOException
   {
      // if there is no version properties file within the application folder
      if (application != null && !versionPropertiesExist(application)) {
         // search for application one directory up
         File parentFolder = application.getParentFile();
         if (parentFolder != null && counter > 0) {
            File applicationRelative = new File(parentFolder, "application").getCanonicalFile();
            if (versionPropertiesExist(applicationRelative)) {
               // if this directory contains the version.properties file
               // ... use this
               return applicationRelative;
            }
            // search one folder up for directory
            return searchVersionPropertiesFolder(parentFolder, counter - 1);
         }
         return null;
      }
      return application;
   }

   private boolean versionPropertiesExist(java.io.File application)
   {
      return application != null && application.exists() && new File(application, "version.properties").exists();
   }


}
