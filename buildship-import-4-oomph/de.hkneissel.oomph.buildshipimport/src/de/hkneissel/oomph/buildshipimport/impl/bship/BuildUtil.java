/**
 *
 */
package de.hkneissel.oomph.buildshipimport.impl.bship;


import java.io.File;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.BuildConfiguration;

import com.gradleware.tooling.toolingclient.GradleDistribution;


/**
 * TODO (Jochen Fliedner) comment the class UtilBuildship
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author Jochen Fliedner
 */
public class BuildUtil
{

   private enum WorkspaceOverride
      implements WorkspaceOverrideConfig {
                                          // TODO
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
    * TODO (Jochen Fliedner) comment the Method xy
    *
    * @param gradleDistribution
    * @param loc
    * @return
    *
    * @author Jochen Fliedner
    */
   public static BuildConfiguration createBuildConfiguration(String projectDir, final GradleDistribution gradleDistribution)
   {
      return createBuildConfiguration(projectDir, gradleDistribution, WorkspaceOverride.NONE);
   }

   /**
    * TODO (Jochen Fliedner) comment the Method createBuildConfiguration
    *
    * @param projectDir
    * @param gradleDistribution
    * @param override
    * @return
    *
    * @author Jochen Fliedner
    */
   public static BuildConfiguration createBuildConfiguration(String projectDir, final GradleDistribution gradleDistribution,
                                                             WorkspaceOverrideConfig override)
   {
      return CorePlugin.configurationManager().createBuildConfiguration(new File(projectDir), override != WorkspaceOverride.NONE,
            gradleDistribution, override.getGradleUserHome(), override.isBuildScansEnabled(), override.isOfflineMode(),
            override.isAutoSync());
   }


}
