/**
 */
package net.ifao.oomph.buildshipimport;


import org.eclipse.emf.ecore.EFactory;


/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see net.ifao.oomph.buildshipimport.BuildshipImportPackage
 * @generated
 */
public interface BuildshipImportFactory
   extends EFactory
{
   /**
    * The singleton instance of the factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   BuildshipImportFactory eINSTANCE = net.ifao.oomph.buildshipimport.impl.BuildshipImportFactoryImpl.init();

   /**
    * Returns a new object of class '<em>Task</em>'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return a new object of class '<em>Task</em>'.
    * @generated
    */
   BuildshipImportTask createBuildshipImportTask();

   /**
    * Returns the package supported by this factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the package supported by this factory.
    * @generated
    */
   BuildshipImportPackage getBuildshipImportPackage();

} //BuildshipImportFactory
