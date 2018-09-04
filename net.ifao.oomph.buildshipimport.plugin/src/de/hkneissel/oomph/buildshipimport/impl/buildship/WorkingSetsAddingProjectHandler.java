package de.hkneissel.oomph.buildshipimport.impl.buildship;


import java.util.List;
import java.util.Optional;

import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.gradleware.tooling.toolingmodel.OmniEclipseProject;


/**
 * very much the private 'ProjectImportWizardController$ImportWizardNewProjectHandler' from buildship ui
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author Jochen Fliedner
 */
public final class WorkingSetsAddingProjectHandler
   implements NewProjectHandler
{
   private final NewProjectHandler importedBuildDelegate;
   private final List<String> workingSetNames;

   private volatile boolean gradleViewsVisible;


   private static final Logger log = LoggerFactory.getLogger(WorkingSetsAddingProjectHandler.class);

   public WorkingSetsAddingProjectHandler(NewProjectHandler delegate, Optional<List<String>> workingSetNames)
   {
      this.importedBuildDelegate = delegate;
      this.workingSetNames = workingSetNames.orElse(ImmutableList.<String> of());
   }

   @Override
   public boolean shouldImport(OmniEclipseProject projectModel)
   {
      return this.importedBuildDelegate.shouldImport(projectModel);
   }

   @Override
   public void afterImport(IProject project, OmniEclipseProject projectModel)
   {
      this.importedBuildDelegate.afterImport(project, projectModel);
      addWorkingSets(project);
      ensureGradleViewsAreVisible();
   }

   private void addWorkingSets(IProject project)
   {
      log.debug("adding to working sets: {}", this.workingSetNames);
      IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      IWorkingSet[] workingSets = UIUtils.toWorkingSets(this.workingSetNames);
      workingSetManager.addToWorkingSets(project, workingSets);
   }

   private void ensureGradleViewsAreVisible()
   {
      log.trace("show buildship views.");

      PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {

         if (!WorkingSetsAddingProjectHandler.this.gradleViewsVisible) {

            WorkingSetsAddingProjectHandler.this.gradleViewsVisible = true;
            UIUtils.showView(UIUtils.ID_TASK_VIEW, null, IWorkbenchPage.VIEW_ACTIVATE);
            UIUtils.showView(UIUtils.ID_EXECUTION_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
         }

      });
   }
}