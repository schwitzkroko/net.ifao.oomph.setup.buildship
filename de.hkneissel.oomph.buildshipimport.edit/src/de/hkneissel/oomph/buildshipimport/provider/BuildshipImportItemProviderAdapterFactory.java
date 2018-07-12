 package de.hkneissel.oomph.buildshipimport.provider;

 import de.hkneissel.oomph.buildshipimport.BuildshipImportFactory;
 import de.hkneissel.oomph.buildshipimport.util.BuildshipImportAdapterFactory;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import org.eclipse.emf.common.notify.Adapter;
 import org.eclipse.emf.common.notify.Notification;
 import org.eclipse.emf.common.notify.Notifier;
 import org.eclipse.emf.common.util.ResourceLocator;
 import org.eclipse.emf.ecore.EObject;
 import org.eclipse.emf.edit.command.CommandParameter;
 import org.eclipse.emf.edit.domain.EditingDomain;
 import org.eclipse.emf.edit.provider.ChangeNotifier;
 import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
 import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
 import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
 import org.eclipse.emf.edit.provider.IChangeNotifier;
 import org.eclipse.emf.edit.provider.IChildCreationExtender;
 import org.eclipse.emf.edit.provider.IDisposable;
 import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
 import org.eclipse.emf.edit.provider.IItemLabelProvider;
 import org.eclipse.emf.edit.provider.IItemPropertySource;
 import org.eclipse.emf.edit.provider.INotifyChangedListener;
 import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
 import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
 import org.eclipse.oomph.base.Annotation;
 import org.eclipse.oomph.base.BasePackage.Literals;
 import org.eclipse.oomph.base.util.BaseSwitch;
import org.eclipse.oomph.setup.SetupPackage;
 import org.eclipse.oomph.setup.SetupTaskContainer;
 import org.eclipse.oomph.setup.util.SetupSwitch;




































 public class BuildshipImportItemProviderAdapterFactory
   extends BuildshipImportAdapterFactory
   implements ComposeableAdapterFactory, IChangeNotifier, IDisposable, IChildCreationExtender
 {
   protected ComposedAdapterFactory parentAdapterFactory;
   protected IChangeNotifier changeNotifier = new ChangeNotifier();







   protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(BuildshipImportEditPlugin.INSTANCE, "http://www.hkneissel.de/oomph/buildshipimport/1.0");







   protected Collection<Object> supportedTypes = new ArrayList();



   protected BuildshipImportTaskItemProvider buildshipImportTaskItemProvider;



   public BuildshipImportItemProviderAdapterFactory()
   {
     this.supportedTypes.add(IEditingDomainItemProvider.class);
     this.supportedTypes.add(IStructuredItemContentProvider.class);
     this.supportedTypes.add(ITreeItemContentProvider.class);
     this.supportedTypes.add(IItemLabelProvider.class);
     this.supportedTypes.add(IItemPropertySource.class);
   }
















   @Override
   public Adapter createBuildshipImportTaskAdapter()
   {
     if (this.buildshipImportTaskItemProvider == null)
     {
       this.buildshipImportTaskItemProvider = new BuildshipImportTaskItemProvider(this);
     }

     return this.buildshipImportTaskItemProvider;
   }








   @Override
   public ComposeableAdapterFactory getRootAdapterFactory()
   {
     return this.parentAdapterFactory == null ? this : this.parentAdapterFactory.getRootAdapterFactory();
   }








   @Override
   public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
   {
     this.parentAdapterFactory = parentAdapterFactory;
   }







   @Override
   public boolean isFactoryForType(Object type)
   {
     return (this.supportedTypes.contains(type)) || (super.isFactoryForType(type));
   }








   @Override
   public Adapter adapt(Notifier notifier, Object type)
   {
     return super.adapt(notifier, this);
   }







   @Override
   public Object adapt(Object object, Object type)
   {
     if (isFactoryForType(type))
     {
       Object adapter = super.adapt(object, type);
       if ((!(type instanceof Class)) || (((Class)type).isInstance(adapter)))
       {
         return adapter;
       }
     }

     return null;
   }






   public List<IChildCreationExtender> getChildCreationExtenders()
   {
     return this.childCreationExtenderManager.getChildCreationExtenders();
   }







   @Override
   public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain)
   {
     return this.childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
   }







   @Override
   public ResourceLocator getResourceLocator()
   {
     return this.childCreationExtenderManager;
   }








   @Override
   public void addListener(INotifyChangedListener notifyChangedListener)
   {
     this.changeNotifier.addListener(notifyChangedListener);
   }








   @Override
   public void removeListener(INotifyChangedListener notifyChangedListener)
   {
     this.changeNotifier.removeListener(notifyChangedListener);
   }








   @Override
   public void fireNotifyChanged(Notification notification)
   {
     this.changeNotifier.fireNotifyChanged(notification);

     if (this.parentAdapterFactory != null)
     {
       this.parentAdapterFactory.fireNotifyChanged(notification);
     }
   }








   @Override
   public void dispose()
   {
     if (this.buildshipImportTaskItemProvider != null) { this.buildshipImportTaskItemProvider.dispose();
     }
   }











   public static class BaseChildCreationExtender
     implements IChildCreationExtender
   {
     protected static class CreationSwitch
       extends BaseSwitch<Object>
     {
       protected List<Object> newChildDescriptors;









       protected EditingDomain editingDomain;










       CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain)
       {
         this.newChildDescriptors = newChildDescriptors;
         this.editingDomain = editingDomain;
       }






       @Override
      public Object caseAnnotation(Annotation object)
       {
         this.newChildDescriptors.add(
           createChildParameter(
           Literals.ANNOTATION__CONTENTS,
           BuildshipImportFactory.eINSTANCE.createBuildshipImportTask()));

         return null;
       }






       protected CommandParameter createChildParameter(Object feature, Object child)
       {
         return new CommandParameter(null, feature, child);
       }
     }








     @Override
   public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain)
     {
       ArrayList<Object> result = new ArrayList();
       new CreationSwitch(result, editingDomain).doSwitch((EObject)object);
       return result;
     }







     @Override
   public ResourceLocator getResourceLocator()
     {
       return BuildshipImportEditPlugin.INSTANCE;
     }
   }











   public static class SetupChildCreationExtender
     implements IChildCreationExtender
   {
     protected static class CreationSwitch
       extends SetupSwitch<Object>
     {
       protected List<Object> newChildDescriptors;










       protected EditingDomain editingDomain;










       CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain)
       {
         this.newChildDescriptors = newChildDescriptors;
         this.editingDomain = editingDomain;
       }






       @Override
      public Object caseSetupTaskContainer(SetupTaskContainer object)
       {
         this.newChildDescriptors.add(
           createChildParameter(
           SetupPackage.SETUP_TASK_CONTAINER__SETUP_TASKS,
           BuildshipImportFactory.eINSTANCE.createBuildshipImportTask()));

         return null;
       }






       protected CommandParameter createChildParameter(Object feature, Object child)
       {
         return new CommandParameter(null, feature, child);
       }
     }








     @Override
   public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain)
     {
       ArrayList<Object> result = new ArrayList();
       new CreationSwitch(result, editingDomain).doSwitch((EObject)object);
       return result;
     }







     @Override
   public ResourceLocator getResourceLocator()
     {
       return BuildshipImportEditPlugin.INSTANCE;
     }
   }
 }


/* Location:              C:\Users\fliedner\Desktop\buildship-import-4-oomph-8.0.0\plugins\de.hkneissel.oomph.buildshipimport.edit_0.7.0.201604211538.jar!\de\hkneissel\oomph\buildshipimport\provider\BuildshipImportItemProviderAdapterFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
