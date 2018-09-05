package net.ifao.oomph.buildshipimport.impl.buildship;


import java.io.File;

import org.eclipse.buildship.core.configuration.WorkspaceConfiguration;


/**
 * config obj interface mimicking (a subset of the final class) {@link WorkspaceConfiguration}
 * representing a workspace settings overrride
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author Jochen Fliedner
 */
public interface WorkspaceOverrideConfig
{
   /**
    * @return override of {@link WorkspaceConfiguration}{@link #getGradleUserHome()}
    */
   File getGradleUserHome();

   /**
    * @return override of {@link WorkspaceConfiguration}{@link #isBuildScansEnabled()}
    */
   boolean isBuildScansEnabled();

   /**
    * @return override of {@link WorkspaceConfiguration}{@link #isOfflineMode()}
    */
   boolean isOfflineMode();

   /**
    * @return override of {@link WorkspaceConfiguration}{@link #isAutoSync()}
    */
   boolean isAutoSync();

}
