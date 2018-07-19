package net.ifao.plugins.action;


import java.io.*;
import java.util.Iterator;

import net.ifao.plugins.tools.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;


public abstract class ActionAdapter
   implements IViewActionDelegate, IObjectActionDelegate, IEditorActionDelegate
{

   private ISelection _selection = null;
   private IEditorInput _editorInput = null;
   private int _iRunCounter;

   /**
    * Constructor JavaDocAction
    * 
    * @author brod
    */
   public ActionAdapter()
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
      // make nothing special
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
      _iRunCounter = 0;
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

               if (update(element, sRoot)) {
                  String modifyFile = getNewFileContent();
                  if (modifyFile != null) {
                     // and set the contents
                     element.setContents(new CharArrayInputStream(modifyFile.toCharArray()),
                           IResource.FORCE, null);
                  }
               }

            } else if (_editorInput instanceof org.eclipse.ui.ide.FileStoreEditorInput) {
               // show error message
               openError("Files outside a project are currently not supported");
               return;
            } else {
               // show error message
               openError("Invalid editor Type");
               return;
            }
         } else if (_selection == null) {
            // show error message
            openError("Please select at least one file !");
            return;
         } else {

            // get the selection
            IStructuredSelection selection = (IStructuredSelection) _selection; // ((ObjectPluginAction) piaAction).getSelection();

            // ... and iterate over the selection
            for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {

               // get the selection element
               Object element = iter.next();


               // get the values
               if (element instanceof org.eclipse.jdt.core.ICompilationUnit) {
                  // if the type is CompilationUnit (right click menu of package explorer)

                  // get the compilationUnit
                  org.eclipse.jdt.core.ICompilationUnit compilationUnit =
                     (org.eclipse.jdt.core.ICompilationUnit) element;
                  IPath compilationPath = compilationUnit.getPath();
                  String sFileName = new String(compilationPath.toString());
                  String path = compilationPath.segment(0);
                  IProject project = workspace.getRoot().getProject(path);
                  sRoot = project.getLocation().toOSString();

                  if (update(sFileName, new CharArrayInputStream(compilationUnit.getSource()
                        .toCharArray()), sRoot, compilationPath.toFile())) {
                     String modifyFile = getNewFileContent();
                     if (modifyFile != null) {
                        // set the contents
                        compilationUnit.getBuffer().setContents(modifyFile);
                        compilationUnit.save(null, true);
                     }
                  }

               } else if (element instanceof IFile) {
                  // if the type is IFile (right click menu of navigator)

                  // get the IFile object
                  IFile iFile = (IFile) element;
                  IProject project = workspace.getRoot().getProject(iFile.getProject().getName());
                  sRoot = project.getLocation().toOSString();

                  if (update(iFile, sRoot)) {
                     String modifyFile = getNewFileContent();
                     if (modifyFile != null) {
                        // and set the contents
                        iFile.setContents(new CharArrayInputStream(modifyFile.toCharArray()),
                              IResource.FORCE, null);
                     }
                  }

               }
            }
         }
      }
      catch (Exception e) {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         e.printStackTrace(new PrintStream(out));
         // show error message
         openError(out.toString());
         return;
      }
      //      // if there was no compilation
      //      if (_iRunCounter == 0) {
      //         // show errormessage
      //         String sFiles = "Root: " + sRoot + "\n";
      //         openError("No files selected !!!\n\n" + sFiles);
      //      }
   }

   protected void openError(String psText)
   {
      MessageDialog.openError(new Shell(), "Error", psText);
   }


   protected boolean openConfirm(String psText)
   {
      return MessageDialog.openConfirm(new Shell(), "Confirm", psText);
   }

   private boolean update(String psFileName, InputStream pisStream, String psEclipseRoot,
                          File psAbsoluteFileName)
   {
      boolean doAction = doAction(psFileName, pisStream, psEclipseRoot, psAbsoluteFileName);

      if (doAction) {

         _iRunCounter++;
      }

      return doAction;
   }

   private boolean update(IFile pFile, String psEclipseRoot)
      throws CoreException, IOException
   {

      IPath location = pFile.getLocation();
      File file = location.toFile();
      String psFileName = pFile.getName();
      InputStream pisStream = pFile.getContents();

      boolean update = update(psFileName, pisStream, psEclipseRoot, file);

      if (update) {
         if (refreshDirectory()) {
            pFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
         }
         _iRunCounter++;
      }

      return update;
   }

   protected String getNewFileContent()
   {
      return null;
   }

   protected abstract boolean refreshDirectory();

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
   public abstract boolean doAction(String psFileName, InputStream pisStream, String psEclipseRoot,
                                    File psAbsoluteFileName);

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
      _editorInput = null;
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
      // make nothing
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
      _editorInput = pEditorPart.getEditorInput();
   }

}
