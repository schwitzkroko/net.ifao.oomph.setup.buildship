package net.ifao.plugins.action;


import java.io.*;
import java.util.Iterator;

import net.ifao.plugins.tools.*;

import org.eclipse.core.resources.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Shell;
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
   {}

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

         // if the element is of type FileEditorInput (right click menu of editor)
         if (_editorInput != null) {
            if (_editorInput instanceof org.eclipse.ui.IFileEditorInput) {
               org.eclipse.ui.IFileEditorInput input =
                  (org.eclipse.ui.part.FileEditorInput) _editorInput;
               // ... get the according file
               IFile element = input.getFile();
               IProject project = workspace.getRoot().getProject(element.getProject().getName());
               sRoot = project.getLocation().toOSString();
               String modifyFile = modifyFile(element.getName(), element.getContents(), sRoot);
               if (modifyFile != null) {
                  // and set the contents
                  element.setContents(new CharArrayInputStream(modifyFile.toCharArray()),
                        IResource.FORCE, null);
                  iRunCounter++;
               }
            } else if (_editorInput instanceof org.eclipse.ui.ide.FileStoreEditorInput) {
               // Files outside a project are currently not supported
            }
         } else {
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
                  if (element instanceof org.eclipse.jdt.core.ICompilationUnit) {

                     // get the compilationUnit
                     org.eclipse.jdt.core.ICompilationUnit compilationUnit =
                        (org.eclipse.jdt.core.ICompilationUnit) element;
                     String sFileName = new String(compilationUnit.getPath().toString());
                     String path = compilationUnit.getPath().segment(0);
                     IProject project = workspace.getRoot().getProject(path);
                     sRoot = project.getLocation().toOSString();

                     String modifyFile =
                        modifyFile(sFileName, new CharArrayInputStream(compilationUnit.getSource()
                              .toCharArray()), sRoot);
                     if (modifyFile != null) {
                        // set the contents
                        compilationUnit.getBuffer().setContents(modifyFile);
                        iRunCounter++;
                        compilationUnit.save(null, true);
                     }

                     // if the type is IFile (right click menu of navigator)
                  } else if (element instanceof IFile) {

                     // get the IFile object
                     IFile iFile = (IFile) element;
                     IProject project =
                        workspace.getRoot().getProject(iFile.getProject().getName());
                     sRoot = project.getLocation().toOSString();
                     String modifyFile = modifyFile(iFile.getName(), iFile.getContents(), sRoot);
                     if (modifyFile != null) {
                        // and set the contents
                        iFile.setContents(new CharArrayInputStream(modifyFile.toCharArray()),
                              IResource.FORCE, null);
                        iRunCounter++;
                     }
                  }
               }
            }
         }
      }
      catch (Exception e) {
         // make nothing
         e.printStackTrace();
      }
      // if there was no compilation
      if (iRunCounter == 0) {
         // show errormessage
         String sFiles = "Root: " + sRoot + "\n";
         MessageDialog.openError(new Shell(), "Error", "Build was not created\n\n" + sFiles);
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
    */
   public static String modifyFile(String psFileName, InputStream pisStream, String psEclipseRoot)
   {
      // read File
      try {
         return CodeFormatterApplication.formatCode(pisStream, psEclipseRoot, psFileName, true);
      }
      catch (Exception e) {
         // make nothing
      }
      finally {
         try {
            pisStream.close();
         }
         catch (IOException e) {
            // make nothing
         }

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
   {}

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
