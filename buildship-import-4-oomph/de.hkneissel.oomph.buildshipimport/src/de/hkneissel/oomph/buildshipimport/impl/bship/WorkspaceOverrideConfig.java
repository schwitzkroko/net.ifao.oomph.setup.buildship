/**
 *
 */
package de.hkneissel.oomph.buildshipimport.impl.bship;


import java.io.File;


/**
 * TODO (Jochen Fliedner) comment the class WorkspaceOverride
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author Jochen Fliedner
 */
public interface WorkspaceOverrideConfig
{
   File getGradleUserHome();

   boolean isBuildScansEnabled();

   boolean isOfflineMode();

   boolean isAutoSync();

}
