package ifaoplugin;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator
   extends AbstractUIPlugin
{

   // The plug-in ID
   public static final String PLUGIN_ID = "net.ifao.plugin";

   // The shared instance
   private static Activator plugin;

   /**
    * The constructor
    */
   public Activator()
   {
      plugin = this;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context)
      throws Exception
   {
      super.start(context);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context)
      throws Exception
   {
      plugin = null;
      super.stop(context);
   }

   /**
    * Returns the shared instance
    *
    * @return the shared instance
    */
   public static Activator getDefault()
   {
      return plugin;
   }

   /**
    * Returns an image descriptor for the image file at the given
    * plug-in relative path
    *
    * @param path the path
    * @return the image descriptor
    */
   public static ImageDescriptor getImageDescriptor(String path)
   {
      return imageDescriptorFromPlugin(PLUGIN_ID, path);
   }
   //
   //   private void addBuilder(IProject project, String id)
   //      throws CoreException
   //   {
   //      IProjectDescription desc = project.getDescription();
   //      ICommand[] commands = desc.getBuildSpec();
   //      for (int i = 0; i < commands.length; ++i) {
   //         if (commands[i].getBuilderName().equals(id)) {
   //            return;
   //         }
   //      }
   //      //add builder to project
   //      ICommand command = desc.newCommand();
   //      command.setBuilderName(id);
   //      ICommand[] nc = new ICommand[commands.length + 1];
   //      // Add it before other builders.
   //      System.arraycopy(commands, 0, nc, 1, commands.length);
   //      nc[0] = command;
   //      desc.setBuildSpec(nc);
   //      project.setDescription(desc, null);
   //   }
}
