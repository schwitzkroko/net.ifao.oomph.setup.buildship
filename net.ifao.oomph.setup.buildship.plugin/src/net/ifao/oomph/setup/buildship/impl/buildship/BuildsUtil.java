package net.ifao.oomph.setup.buildship.impl.buildship;


import java.io.File;

import org.eclipse.buildship.core.BuildConfiguration;
import org.eclipse.buildship.core.GradleDistribution;


/**
 * util for creating build configs
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author Jochen Fliedner
 */
@Deprecated
public class BuildsUtil
{

   private enum WorkspaceOverride
      implements WorkspaceOverrideConfig {
                                          NONE;

      @Override
      public File getGradleUserHome()
      {
         return null;
      }

      @Override
      public boolean isBuildScansEnabled()
      {
         return false;
      }

      @Override
      public boolean isOfflineMode()
      {
         return false;
      }

      @Override
      public boolean isAutoSync()
      {
         return false;
      }
   }

   /**
    * create default build configuration w/o workspace gradle settings override
    *
    * @param projectDir of the build
    * @param gradleDistribution
    *
    * @return the default build config obj
    */
   public static BuildConfiguration createBuildConfiguration(String projectDir, final GradleDistribution gradleDistribution)
   {
      WorkspaceOverride workspaceOverride = WorkspaceOverride.NONE;

      boolean overrideWorkspaceSettings = workspaceOverride != WorkspaceOverride.NONE;

      //      return CorePlugin.configurationManager().createBuildConfiguration(new File(projectDir), overrideWorkspaceSettings,
      //            gradleDistribution, workspaceOverride.getGradleUserHome(), workspaceOverride.isBuildScansEnabled(),
      //            workspaceOverride.isOfflineMode(), workspaceOverride.isAutoSync());

      return null;
   }

}
