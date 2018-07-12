package de.hkneissel.oomph.buildshipimport.provider;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.EMFPlugin.EclipsePlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.oomph.base.provider.BaseEditPlugin;
import org.eclipse.oomph.setup.provider.SetupEditPlugin;

public final class BuildshipImportEditPlugin extends EMFPlugin {

   public static final BuildshipImportEditPlugin INSTANCE = new BuildshipImportEditPlugin();

   private static Implementation plugin;

   public BuildshipImportEditPlugin() {
      super(new ResourceLocator[] { BaseEditPlugin.INSTANCE, SetupEditPlugin.INSTANCE });
   }

   public ResourceLocator getPluginResourceLocator() {
      return plugin;
   }

   public static Implementation getPlugin() {
      return plugin;
   }

   public static class Implementation extends EMFPlugin.EclipsePlugin {

      public Implementation() {
         BuildshipImportEditPlugin.plugin = this;
      }
   }
}

/*
 * Location: C:\Users\fliedner\Desktop\buildship-import-4-oomph-8.0.0\plugins\de.hkneissel.oomph.
 * buildshipimport.edit_0.7.0.201604211538.jar!\de\hkneissel\oomph\buildshipimport\provider\
 * BuildshipImportEditPlugin.class Java compiler version: 7 (51.0) JD-Core Version: 0.7.1
 */
