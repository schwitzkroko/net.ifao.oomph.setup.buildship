package net.ifao.oomph.buildshipimport.impl.buildship;


import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;


/**
 * The code below is copied from the buildship ui plugin -
 * not nice, but the plugin does not export anything, so i don't know
 * how to access it else.
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author hkneissel, Jochen Fliedner
 */
public final class UIUtils
{
   /**
    * view id
    */
   public static final String ID_TASK_VIEW = "org.eclipse.buildship.ui.views.taskview";

   /**
    * view id
    */
   public static final String ID_EXECUTION_VIEW = "org.eclipse.buildship.ui.views.executionview";


   private static final Logger log = LoggerFactory.getLogger(UIUtils.class);


   private UIUtils()
   {
      // utils
   }

   /**
    * Converts the given working set names to {@link org.eclipse.ui.IWorkingSet} instances. Filters
    * out working sets that cannot be found by the {@link IWorkingSetManager}.
    *
    * @param workingSetNames the names of the working sets
    * @return the {@link org.eclipse.ui.IWorkingSet} instances
    */
   public static IWorkingSet[] toWorkingSets(List<String> workingSetNames)
   {
      final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      // @formatter:off
      return FluentIterable.from(workingSetNames)
            .transform(workingSetManager::getWorkingSet)
            .filter(Predicates.notNull())
            .toArray(IWorkingSet.class);
      // @formatter:on
   }

   /**
    * Shows the view with the given id and secondary id in the given mode.
    *
    * @param viewId the id of the view
    * @param secondaryId the secondary id of the view, or {@code null] for no secondary id
    * @param mode the activation mode, must be {@link org.eclipse.ui.IWorkbenchPage#VIEW_ACTIVATE},
    *            {@link org.eclipse.ui.IWorkbenchPage#VIEW_VISIBLE} or
    *            {@link org.eclipse.ui.IWorkbenchPage#VIEW_CREATE}
    * @param <T> the expected type of the view
    * @return the shown view, never null
    * @throws RuntimeException thrown if the view cannot be initialized correctly
    */
   public static <T extends IViewPart> T showView(String viewId, String secondaryId, int mode)
   {
      IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      try {
         @SuppressWarnings("unchecked")
         T view = (T) activeWorkbenchWindow.getActivePage().showView(viewId, secondaryId, mode);
         return view;
      }
      catch (PartInitException e) {

         log.error("error on initializing view.", e);

         throw new RuntimeException(String.format("Cannot show view with id %s and secondary id %s.", viewId, secondaryId), e);
      }
   }

   /**
    * asynchronously set the buildship gradle views visible
    *
    * @param onFalse - if the wrapped value still happens to be false
    */
   public static void asyncSetGradleViewsAreVisible(final AtomicBoolean onFalse)
   {
      log.trace("check for buildship gradle views visible.");

      PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {

         if (!onFalse.getAndSet(Boolean.TRUE)) {

            log.debug("set buildship gradle views visible.");

            showView(UIUtils.ID_TASK_VIEW, null, IWorkbenchPage.VIEW_ACTIVATE);
            showView(UIUtils.ID_EXECUTION_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
         }

      });
   }
}
