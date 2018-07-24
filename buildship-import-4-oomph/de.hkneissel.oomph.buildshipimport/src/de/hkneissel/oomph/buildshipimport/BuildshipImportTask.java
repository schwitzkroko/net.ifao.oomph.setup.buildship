/**
 */
package de.hkneissel.oomph.buildshipimport;


import org.eclipse.emf.common.util.URI;
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
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getProjectRootDirectory <em>Project Root Directory</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJavaHome <em>Java Home</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleUserHome <em>Gradle User Home</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJvmArguments <em>Jvm Arguments</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getArguments <em>Arguments</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getImportWaitTime <em>Import Wait Time</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleBuildDirectory <em>Gradle Build Directory</em>}</li>
 * </ul>
 *
 * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask()
 * @model annotation="http://www.eclipse.org/oomph/setup/Enablement variableName='p2.buildship.import' repository='http://p2.kneissel.mail-und-web.de/update/buildshipimport' installableUnits='de.hkneissel.oomph.buildshipimport.feature.group'"
 *        annotation="http://www.eclipse.org/oomph/setup/ValidTriggers triggers='STARTUP MANUAL'"
 * @generated
 */
public interface BuildshipImportTask
   extends SetupTask
{
   /**
    * Returns the value of the '<em><b>Project Root Directory</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Project Root Directory</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Project Root Directory</em>' attribute.
    * @see #setProjectRootDirectory(URI)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_ProjectRootDirectory()
    * @model dataType="org.eclipse.oomph.base.URI" required="true"
    * @generated
    */
   URI getProjectRootDirectory();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getProjectRootDirectory <em>Project Root Directory</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Project Root Directory</em>' attribute.
    * @see #getProjectRootDirectory()
    * @generated
    */
   void setProjectRootDirectory(URI value);

   /**
    * Returns the value of the '<em><b>Java Home</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Java Home</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Java Home</em>' attribute.
    * @see #setJavaHome(URI)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_JavaHome()
    * @model dataType="org.eclipse.oomph.base.URI"
    * @generated
    */
   URI getJavaHome();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJavaHome <em>Java Home</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Java Home</em>' attribute.
    * @see #getJavaHome()
    * @generated
    */
   void setJavaHome(URI value);

   /**
    * Returns the value of the '<em><b>Gradle User Home</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Gradle User Home</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Gradle User Home</em>' attribute.
    * @see #setGradleUserHome(URI)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_GradleUserHome()
    * @model dataType="org.eclipse.oomph.base.URI"
    * @generated
    */
   URI getGradleUserHome();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleUserHome <em>Gradle User Home</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Gradle User Home</em>' attribute.
    * @see #getGradleUserHome()
    * @generated
    */
   void setGradleUserHome(URI value);

   /**
    * Returns the value of the '<em><b>Jvm Arguments</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Jvm Arguments</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Jvm Arguments</em>' attribute.
    * @see #setJvmArguments(String)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_JvmArguments()
    * @model
    * @generated
    */
   String getJvmArguments();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJvmArguments <em>Jvm Arguments</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Jvm Arguments</em>' attribute.
    * @see #getJvmArguments()
    * @generated
    */
   void setJvmArguments(String value);

   /**
    * Returns the value of the '<em><b>Arguments</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Arguments</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Arguments</em>' attribute.
    * @see #setArguments(String)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_Arguments()
    * @model
    * @generated
    */
   String getArguments();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getArguments <em>Arguments</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Arguments</em>' attribute.
    * @see #getArguments()
    * @generated
    */
   void setArguments(String value);

   /**
    * Returns the value of the '<em><b>Import Wait Time</b></em>' attribute.
    * The default value is <code>"30"</code>.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Import Wait Time</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Import Wait Time</em>' attribute.
    * @see #setImportWaitTime(int)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_ImportWaitTime()
    * @model default="30"
    * @generated
    */
   int getImportWaitTime();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getImportWaitTime <em>Import Wait Time</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Import Wait Time</em>' attribute.
    * @see #getImportWaitTime()
    * @generated
    */
   void setImportWaitTime(int value);

   /**
    * Returns the value of the '<em><b>Gradle Task</b></em>' attribute.
    * The default value is <code>"eclipse"</code>.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Gradle Task</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Gradle Task</em>' attribute.
    * @see #setGradleTask(String)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_GradleTask()
    * @model default="eclipse"
    * @generated
    */
   String getGradleTask();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Gradle Task</em>' attribute.
    * @see #getGradleTask()
    * @generated
    */
   void setGradleTask(String value);

   /**
    * Returns the value of the '<em><b>Gradle Build Directory</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Gradle Build Directory</em>' attribute isn't clear,
    * there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * @return the value of the '<em>Gradle Build Directory</em>' attribute.
    * @see #setGradleBuildDirectory(URI)
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportPackage#getBuildshipImportTask_GradleBuildDirectory()
    * @model dataType="org.eclipse.oomph.base.URI"
    * @generated
    */
   URI getGradleBuildDirectory();

   /**
    * Sets the value of the '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleBuildDirectory <em>Gradle Build Directory</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @param value the new value of the '<em>Gradle Build Directory</em>' attribute.
    * @see #getGradleBuildDirectory()
    * @generated
    */
   void setGradleBuildDirectory(URI value);

} // BuildshipImportTask
