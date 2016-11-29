package net.ifao.plugins.action;


import java.util.*;

import org.eclipse.core.internal.resources.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.*;
import org.eclipse.ui.internal.*;


public class TscAction
   implements IViewActionDelegate, IEditorActionDelegate

{

   private IViewPart _view;
   private IEditorInput _editorInput = null;

   @Override
   public void init(IViewPart view)
   {
      setView(view);

   }

   @Override
   @SuppressWarnings({ "restriction" })
   public void run(IAction action)
   {
      // if the element is of type FileEditorInput (right click menu of editor)
      if (_editorInput != null) {
         if (_editorInput instanceof org.eclipse.ui.IFileEditorInput) {
            org.eclipse.ui.IFileEditorInput input = (org.eclipse.ui.IFileEditorInput) _editorInput;
            // ... get the according file
            IFile file = input.getFile();
            String sPath = file.getProjectRelativePath().toString();

            if (sPath.endsWith(".java")) {
               sPath =
                  "jUnitTest" + sPath.substring(sPath.indexOf("/"), sPath.length() - 4) + "tsc";
               file = file.getProject().getFile(sPath);
            }

            IWorkbenchPage page =
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try {
               IDE.openEditor(page, file);
            }
            catch (Exception e) {}


         } else if (_editorInput instanceof org.eclipse.ui.ide.FileStoreEditorInput) {
            // Files outside a project are currently not supported
         }
      } else {
         StructuredSelection selection =
            (StructuredSelection) ((ObjectPluginAction) action).getSelection();
         for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
            //       get the selectionelement
            Object element = iter.next();

            // if the type is CompilationUnit (right click menu of package explorer)
            if (element instanceof CompilationUnit) {
               CompilationUnit el2 = (CompilationUnit) element;

               // IPath path = el2.getJavaProject().findElement(null);


               try {
                  org.eclipse.core.internal.resources.File resource = (File) el2.getResource();

                  String sPath = resource.getProjectRelativePath().toString();

                  if (sPath.endsWith(".java")) {
                     sPath =
                        "jUnitTest" + sPath.substring(sPath.indexOf("/"), sPath.length() - 4)
                              + "tsc";
                     element = resource.getProject().getFile(sPath);
                  }
               }
               catch (Exception e) {}
            }

            // validate iFile
            if (element instanceof IFile) {
               IFile file = (IFile) element;

               String sPath = file.getProjectRelativePath().toString();

               if (sPath.endsWith(".java")) {
                  sPath =
                     "jUnitTest" + sPath.substring(sPath.indexOf("/"), sPath.length() - 4) + "tsc";
                  file = file.getProject().getFile(sPath);
               }

               IWorkbenchPage page =
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               try {
                  IDE.openEditor(page, file);
               }
               catch (Exception e) {}
            }
         }
      }
   }


   @Override
   public void selectionChanged(IAction action, ISelection selection)
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
      if (pEditorPart != null) {
         _editorInput = pEditorPart.getEditorInput();
      }
   }

   public void setView(IViewPart _view)
   {
      this._view = _view;
   }

   public IViewPart getView()
   {
      return _view;
   }

}
