package net.ifao.plugins.editor;


import net.ifao.plugins.editor.testcase.TestcasePage;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;


public class TestcaseEditor
   extends MultiPageEditorPart
   implements IResourceChangeListener
{

   private FileEditorInput fileEditorInput;
   private TestcasePage businessRulesPage = null;

   public TestcaseEditor()
   {
      super();
      ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
   }


   @Override
   public void init(IEditorSite site, IEditorInput editorInput)
      throws PartInitException
   {
      if (!(editorInput instanceof IFileEditorInput))
         throw new PartInitException("Invalid Input: Must be IFileEditorInput");
      super.init(site, editorInput);
   }

   @Override
   public void dispose()
   {
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
      super.dispose();
   }

   @Override
   protected void createPages()
   {
      createBusinessPage();

   }


   @Override
   public void doSave(IProgressMonitor monitor)
   {
      getEditor(0).doSave(monitor);

   }

   @Override
   public void doSaveAs()
   {
      IEditorPart editor = getEditor(0);
      editor.doSaveAs();
      setPageText(0, editor.getTitle());
      setInput(editor.getEditorInput());

   }


   @Override
   public boolean isSaveAsAllowed()
   {
      return true;
   }

   public void resourceChanged(final IResourceChangeEvent event)
   {
      if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
         Display.getDefault().asyncExec(new Runnable()
         {

            public void run()
            {
               IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
               for (int i = 0; i < pages.length; i++) {
                  IEditorPart editorPart = pages[i].findEditor(fileEditorInput);
                  pages[i].closeEditor(editorPart, true);

               }

            }

         });
      } else if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
         DeltaPrinter deltaPrinter = new DeltaPrinter();
         try {
            event.getDelta().accept(deltaPrinter);
         }
         catch (CoreException e) {
            // catch ... make nothing
         }
         String partName = getPartName();
         if (partName.lastIndexOf(".") > 0) {
            partName = partName.substring(0, partName.lastIndexOf(".") + 1);
            if (deltaPrinter.lastName.startsWith(partName)) {
               Runnable runObject = new Runnable()
               {
                  public void run()
                  {
                     businessRulesPage.reload();
                  }
               };
               Display.getDefault().asyncExec(runObject);
            }
         }
      }
   }

   class DeltaPrinter
      implements IResourceDeltaVisitor
   {
      String lastName = "";

      public boolean visit(IResourceDelta delta)
      {
         IResource res = delta.getResource();
         lastName = res.getName();
         return true; // visit the children
      }
   }

   private void createBusinessPage()
   {
      IEditorInput editorInput = getEditorInput();
      if (editorInput != null && editorInput instanceof FileEditorInput) {
         fileEditorInput = ((FileEditorInput) editorInput);

         int index = 0;
         try {
            businessRulesPage = new TestcasePage();
            index = addPage(businessRulesPage, fileEditorInput);
            setPageText(index, "Properties");
            setPartName(fileEditorInput.getName());
         }
         catch (PartInitException e) {
            e.printStackTrace();
         }

      }

   }


}
