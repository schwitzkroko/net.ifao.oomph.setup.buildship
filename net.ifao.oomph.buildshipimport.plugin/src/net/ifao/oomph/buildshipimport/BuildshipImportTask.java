/**
 */
package net.ifao.oomph.buildshipimport;


import org.eclipse.emf.common.util.EList;
import org.eclipse.oomph.resources.SourceLocator;
import org.eclipse.oomph.setup.SetupTask;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link net.ifao.oomph.buildshipimport.BuildshipImportTask#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link net.ifao.oomph.buildshipimport.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}</li>
 * </ul>
 *
 * @see net.ifao.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask()
 * @model annotation="http://www.eclipse.org/oomph/setup/ValidTriggers triggers='STARTUP MANUAL'"
 * @generated
 */
public interface BuildshipImportTask
   extends SetupTask
{
   /**
    * Returns the value of the '<em><b>Source Locators</b></em>' containment reference list.
    * The list contents are of type {@link org.eclipse.oomph.resources.SourceLocator}.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Source Locators</em>' containment reference list isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Source Locators</em>' containment reference list.
    * @see net.ifao.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_SourceLocators()
    * @model containment="true" required="true"
    *        extendedMetaData="name='sourceLocator'"
    * @generated
    */
   EList<SourceLocator> getSourceLocators();

   /**
    * Returns the value of the '<em><b>Gradle Task</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Gradle Task</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Gradle Task</em>' attribute.
    * @see #setGradleTask(String)
    * @see net.ifao.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_GradleTask()
    * @model
    * @generated
    */
   String getGradleTask();

   /**
    * Sets the value of the '{@link net.ifao.oomph.buildshipimport.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Gradle Task</em>' attribute.
    * @see #getGradleTask()
    * @generated
    */
   void setGradleTask(String value);

} // BuildshipImportTask
