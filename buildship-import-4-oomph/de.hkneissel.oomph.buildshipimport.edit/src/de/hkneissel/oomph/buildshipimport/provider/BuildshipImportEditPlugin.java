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

   @Override
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