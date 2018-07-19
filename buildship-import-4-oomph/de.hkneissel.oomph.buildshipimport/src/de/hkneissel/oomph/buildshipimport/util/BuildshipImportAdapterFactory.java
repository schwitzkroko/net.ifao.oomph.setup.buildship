package de.hkneissel.oomph.buildshipimport.util;

import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.oomph.base.ModelElement;
import org.eclipse.oomph.setup.SetupTask;

public class BuildshipImportAdapterFactory extends AdapterFactoryImpl {

   protected static BuildshipImportPackage modelPackage;

   public BuildshipImportAdapterFactory() {
      if (modelPackage == null) {
         modelPackage = BuildshipImportPackage.eINSTANCE;
      }
   }

   @Override
   public boolean isFactoryForType(Object object) {
      if (object == modelPackage) {
         return true;
      }
      if ((object instanceof EObject)) {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

   protected BuildshipImportSwitch<Adapter> modelSwitch = new BuildshipImportSwitch() {

      @Override
      public Adapter caseBuildshipImportTask(BuildshipImportTask object) {
         return BuildshipImportAdapterFactory.this.createBuildshipImportTaskAdapter();
      }

      @Override
      public Adapter caseModelElement(ModelElement object) {
         return BuildshipImportAdapterFactory.this.createModelElementAdapter();
      }

      @Override
      public Adapter caseSetupTask(SetupTask object) {
         return BuildshipImportAdapterFactory.this.createSetupTaskAdapter();
      }

      @Override
      public Adapter defaultCase(EObject object) {
         return BuildshipImportAdapterFactory.this.createEObjectAdapter();
      }
   };

   @Override
   public Adapter createAdapter(Notifier target) {
      return (Adapter) this.modelSwitch.doSwitch((EObject) target);
   }

   public Adapter createBuildshipImportTaskAdapter() {
      return null;
   }

   public Adapter createModelElementAdapter() {
      return null;
   }

   public Adapter createSetupTaskAdapter() {
      return null;
   }

   public Adapter createEObjectAdapter() {
      return null;
   }
}
