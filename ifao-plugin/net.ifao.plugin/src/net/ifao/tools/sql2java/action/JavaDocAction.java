package net.ifao.tools.sql2java.action;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.util.Execute;

import org.eclipse.core.resources.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;


/**
 * The class JavaDocAction is the "entrypoint" for
 * the plugin
 * 
 * <p>
 * Copyright &copy; 2006, i:FAO
 * 
 * @author brod
 */
public class JavaDocAction
   implements IViewActionDelegate, IObjectActionDelegate, IEditorActionDelegate
{

   private ISelection _selection = null;
   private IEditorInput _editorInput = null;

   /**
    * Constructor JavaDocAction
    * 
    * @author brod
    */
   public JavaDocAction()
   {
      super();
   }

   /**
    * The method init makes nothing
    * 
    * @param pView a IViewPart object (which will be
    * ignored)
    * 
    * @author brod
    */
   @Override
   public void init(IViewPart pView)
   {
      // make nothing
   }

   /**
    * The method run is the main method of this class.
    * It handles multiple objects
    * 
    * @param piaAction The IAction object  has to be of the type
    * ObjectPluginAction, which can contain elements of the
    * type FileEditorInput, CompilationUnit or IFile
    * 
    * @author brod
    */
   @Override
   public void run(IAction piaAction)
   {
      String sRoot = "";
      int iRunCounter = 0;
      try {
         // get the WorkSpace
         IWorkspace workspace = ResourcesPlugin.getWorkspace();


         if (_selection == null) {
            // show errormessage
            MessageDialog.openError(new Shell(), "Error",
                  "Please select at least one java file to format!\n");
         } else {

            // get the selection
            IStructuredSelection selection = (IStructuredSelection) _selection; // ((ObjectPluginAction) piaAction).getSelection();

            // ... and iterate over the selection
            for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {

               // get the selectionelement
               Object element = iter.next();


               // get the values
               // if the type is CompilationUnit (right click menu of package explorer)
               if (element instanceof IFile) {

                  // get the IFile object
                  IFile iFile = (IFile) element;
                  IProject project = workspace.getRoot().getProject(iFile.getProject().getName());
                  sRoot = project.getLocation().toOSString();
                  String modifyFile = modifyFile(iFile, sRoot);
                  if (modifyFile != null) {
                     IContainer parent = iFile.getParent();
                     parent.refreshLocal(IResource.DEPTH_INFINITE, null);
                     MessageDialog.openInformation(new Shell(), "Creation",
                           "The following files are created\n\n" + modifyFile);
                     iRunCounter++;
                  }
               }
            }
         }

      }
      catch (Exception e) {
         // make nothing
         Util.showException(e);
      }
      // if there was no compilation
      if (iRunCounter == 0) {
         // show errormessage
         String sFiles = "Root: " + sRoot + "\n";
         MessageDialog.openError(new Shell(), "Error", "Files are not created\n\n" + sFiles);
      }
   }


   /**
    * The method modifyFile modifies a file
    * 
    * @param psFileName The name of the file
    * @param pisStream The inputStream of the file
    * @param psEclipseRoot The root to the eclipseProject (to the
    * the IFAO format file)
    * @return The formated String
    * 
    * @author brod
    * @throws Exception
    */
   private String modifyFile(IFile iFile, String psEclipseRoot)
      throws Exception
   {
      File fRaw = iFile.getRawLocation().toFile();
      String absolutePath = fRaw.getAbsolutePath();

      String sConsole = Execute.start("net.ifao.common.database.ddl.Sql2Java", absolutePath, true);
      if (sConsole != null) {
         BufferedReader reader = new BufferedReader(new StringReader(sConsole));
         String sLine;
         StringBuilder sbFiles = new StringBuilder();
         try {
            while ((sLine = reader.readLine()) != null) {
               if (sLine.startsWith("-")) {
                  sbFiles.append("- " + sLine.substring(sLine.lastIndexOf("\\") + 1));
                  sbFiles.append("\n");
               }
            }
            reader.close();
         }
         catch (IOException e) {
            // should never happen on StringReader
         }

         return sbFiles.toString();
      }


      return null;

   }

   /**
    * Will be called by eclipse when the selection has changed
    * 
    * @param pAction
    * @param pSelection
    * 
    * @author brod
    */
   @Override
   public void selectionChanged(IAction pAction, ISelection pSelection)
   {
      _selection = pSelection;
   }

   /**
    * Method setActivePart
    * overrides @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
    *
    * @param pAction
    * @param pWorkbenchPart
    *
    * @author kaufmann
    */
   @Override
   public void setActivePart(IAction pAction, IWorkbenchPart pWorkbenchPart)
   {
      // ignore this entry
   }

   /**
    * Method setActiveEditor
    * overrides @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
    *
    * @param pAction
    * @param pEditorPart
    *
    * @author kaufmann
    */
   @Override
   public void setActiveEditor(IAction pAction, IEditorPart pEditorPart)
   {
      if (pEditorPart != null) {
         _editorInput = pEditorPart.getEditorInput();
      }
   }

}
