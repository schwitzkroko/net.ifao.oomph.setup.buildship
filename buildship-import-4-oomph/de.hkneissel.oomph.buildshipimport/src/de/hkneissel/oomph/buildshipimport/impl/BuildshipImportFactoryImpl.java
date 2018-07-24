/**
 */
package de.hkneissel.oomph.buildshipimport.impl;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import de.hkneissel.oomph.buildshipimport.BuildshipImportFactory;
import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class BuildshipImportFactoryImpl
   extends EFactoryImpl
   implements BuildshipImportFactory
{
   /**
    * Creates the default factory implementation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public static BuildshipImportFactory init()
   {
      try {
         BuildshipImportFactory theBuildshipImportFactory =
            (BuildshipImportFactory) EPackage.Registry.INSTANCE.getEFactory(BuildshipImportPackage.eNS_URI);
         if (theBuildshipImportFactory != null) {
            return theBuildshipImportFactory;
         }
      }
      catch (Exception exception) {
         EcorePlugin.INSTANCE.log(exception);
      }
      return new BuildshipImportFactoryImpl();
   }

   /**
    * Creates an instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public BuildshipImportFactoryImpl()
   {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public EObject create(EClass eClass)
   {
      switch (eClass.getClassifierID()) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK:
            return createBuildshipImportTask();
         default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public BuildshipImportTask createBuildshipImportTask()
   {
      BuildshipImportTaskImpl buildshipImportTask = new BuildshipImportTaskImpl();
      return buildshipImportTask;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public BuildshipImportPackage getBuildshipImportPackage()
   {
      return (BuildshipImportPackage) getEPackage();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @deprecated
    * @generated
    */
   @Deprecated
   public static BuildshipImportPackage getPackage()
   {
      return BuildshipImportPackage.eINSTANCE;
   }

} //BuildshipImportFactoryImpl
