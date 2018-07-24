/**
 */
package de.hkneissel.oomph.buildshipimport;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.oomph.setup.SetupPackage;


/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see de.hkneissel.oomph.buildshipimport.BuildshipImportFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore schemaLocation='http://www.hkneissel.de/oomph/buildshipimport/schemas/Buildshipimport-1.0.ecore'"
 * @generated
 */
public interface BuildshipImportPackage
   extends EPackage
{
   /**
    * The package name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNAME = "buildshipimport";

   /**
    * The package namespace URI.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNS_URI = "http://www.hkneissel.de/oomph/buildshipimport/1.0";

   /**
    * The package namespace name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   String eNS_PREFIX = "buildshipimport";

   /**
    * The singleton instance of the package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   BuildshipImportPackage eINSTANCE = de.hkneissel.oomph.buildshipimport.impl.BuildshipImportPackageImpl.init();

   /**
    * The meta object id for the '{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl <em>Task</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl
    * @see de.hkneissel.oomph.buildshipimport.impl.BuildshipImportPackageImpl#getBuildshipImportTask()
    * @generated
    */
   int BUILDSHIP_IMPORT_TASK = 0;

   /**
    * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__ANNOTATIONS = SetupPackage.SETUP_TASK__ANNOTATIONS;

   /**
    * The feature id for the '<em><b>ID</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__ID = SetupPackage.SETUP_TASK__ID;

   /**
    * The feature id for the '<em><b>Description</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__DESCRIPTION = SetupPackage.SETUP_TASK__DESCRIPTION;

   /**
    * The feature id for the '<em><b>Scope Type</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__SCOPE_TYPE = SetupPackage.SETUP_TASK__SCOPE_TYPE;

   /**
    * The feature id for the '<em><b>Excluded Triggers</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__EXCLUDED_TRIGGERS = SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS;

   /**
    * The feature id for the '<em><b>Disabled</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__DISABLED = SetupPackage.SETUP_TASK__DISABLED;

   /**
    * The feature id for the '<em><b>Predecessors</b></em>' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__PREDECESSORS = SetupPackage.SETUP_TASK__PREDECESSORS;

   /**
    * The feature id for the '<em><b>Successors</b></em>' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__SUCCESSORS = SetupPackage.SETUP_TASK__SUCCESSORS;

   /**
    * The feature id for the '<em><b>Restrictions</b></em>' reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__RESTRICTIONS = SetupPackage.SETUP_TASK__RESTRICTIONS;

   /**
    * The feature id for the '<em><b>Filter</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__FILTER = SetupPackage.SETUP_TASK__FILTER;

   /**
    * The feature id for the '<em><b>Project Root Directory</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY = SetupPackage.SETUP_TASK_FEATURE_COUNT + 0;

   /**
    * The feature id for the '<em><b>Java Home</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__JAVA_HOME = SetupPackage.SETUP_TASK_FEATURE_COUNT + 1;

   /**
    * The feature id for the '<em><b>Gradle User Home</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME = SetupPackage.SETUP_TASK_FEATURE_COUNT + 2;

   /**
    * The feature id for the '<em><b>Jvm Arguments</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS = SetupPackage.SETUP_TASK_FEATURE_COUNT + 3;

   /**
    * The feature id for the '<em><b>Arguments</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__ARGUMENTS = SetupPackage.SETUP_TASK_FEATURE_COUNT + 4;

   /**
    * The feature id for the '<em><b>Import Wait Time</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME = SetupPackage.SETUP_TASK_FEATURE_COUNT + 5;

   /**
    * The feature id for the '<em><b>Gradle Task</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__GRADLE_TASK = SetupPackage.SETUP_TASK_FEATURE_COUNT + 6;

   /**
    * The feature id for the '<em><b>Gradle Build Directory</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY = SetupPackage.SETUP_TASK_FEATURE_COUNT + 7;

   /**
    * The number of structural features of the '<em>Task</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK_FEATURE_COUNT = SetupPackage.SETUP_TASK_FEATURE_COUNT + 8;


   /**
    * Returns the meta object for class '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask <em>Task</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for class '<em>Task</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask
    * @generated
    */
   EClass getBuildshipImportTask();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getProjectRootDirectory <em>Project Root Directory</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Project Root Directory</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getProjectRootDirectory()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_ProjectRootDirectory();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJavaHome <em>Java Home</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Java Home</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJavaHome()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_JavaHome();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleUserHome <em>Gradle User Home</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Gradle User Home</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleUserHome()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_GradleUserHome();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJvmArguments <em>Jvm Arguments</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Jvm Arguments</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getJvmArguments()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_JvmArguments();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getArguments <em>Arguments</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Arguments</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getArguments()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_Arguments();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getImportWaitTime <em>Import Wait Time</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Import Wait Time</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getImportWaitTime()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_ImportWaitTime();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Gradle Task</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleTask()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_GradleTask();

   /**
    * Returns the meta object for the attribute '{@link de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleBuildDirectory <em>Gradle Build Directory</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Gradle Build Directory</em>'.
    * @see de.hkneissel.oomph.buildshipimport.BuildshipImportTask#getGradleBuildDirectory()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_GradleBuildDirectory();

   /**
    * Returns the factory that creates the instances of the model.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the factory that creates the instances of the model.
    * @generated
    */
   BuildshipImportFactory getBuildshipImportFactory();

   /**
    * <!-- begin-user-doc -->
    * Defines literals for the meta objects that represent
    * <ul>
    *   <li>each class,</li>
    *   <li>each feature of each class,</li>
    *   <li>each enum,</li>
    *   <li>and each data type</li>
    * </ul>
    * <!-- end-user-doc -->
    * @generated
    */
   interface Literals
   {
      /**
       * The meta object literal for the '{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl <em>Task</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @see de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl
       * @see de.hkneissel.oomph.buildshipimport.impl.BuildshipImportPackageImpl#getBuildshipImportTask()
       * @generated
       */
      EClass BUILDSHIP_IMPORT_TASK = eINSTANCE.getBuildshipImportTask();

      /**
       * The meta object literal for the '<em><b>Project Root Directory</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY = eINSTANCE.getBuildshipImportTask_ProjectRootDirectory();

      /**
       * The meta object literal for the '<em><b>Java Home</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__JAVA_HOME = eINSTANCE.getBuildshipImportTask_JavaHome();

      /**
       * The meta object literal for the '<em><b>Gradle User Home</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME = eINSTANCE.getBuildshipImportTask_GradleUserHome();

      /**
       * The meta object literal for the '<em><b>Jvm Arguments</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS = eINSTANCE.getBuildshipImportTask_JvmArguments();

      /**
       * The meta object literal for the '<em><b>Arguments</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__ARGUMENTS = eINSTANCE.getBuildshipImportTask_Arguments();

      /**
       * The meta object literal for the '<em><b>Import Wait Time</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME = eINSTANCE.getBuildshipImportTask_ImportWaitTime();

      /**
       * The meta object literal for the '<em><b>Gradle Task</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__GRADLE_TASK = eINSTANCE.getBuildshipImportTask_GradleTask();

      /**
       * The meta object literal for the '<em><b>Gradle Build Directory</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY = eINSTANCE.getBuildshipImportTask_GradleBuildDirectory();

   }

} //BuildshipImportPackage
