/**
 */
package net.ifao.oomph.setup.buildship;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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
 * @see net.ifao.oomph.setup.buildship.BuildshipImportFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore schemaLocation='http://www.ifao.net/oomph/buildshipimport/schemas/Buildshipimport-1.0.ecore'"
 *        annotation="http://www.eclipse.org/oomph/setup/Enablement variableName='setup.projects.p2' repository='${oomph.update.url}' installableUnits='net.ifao.oomph.setup.buildship.feature.group'"
 *        annotation="http://www.eclipse.org/oomph/base/LabelProvider imageBaseURI='http://git.eclipse.org/c/oomph/org.eclipse.oomph.git/plain/plugins/net.ifao.oomph.setup.buildship.edit/icons/full/obj16'"
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
   String eNS_URI = "http://www.ifao.net/oomph/buildshipimport/1.0";

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
   BuildshipImportPackage eINSTANCE = net.ifao.oomph.setup.buildship.impl.BuildshipImportPackageImpl.init();

   /**
    * The meta object id for the '{@link net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl <em>Task</em>}' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl
    * @see net.ifao.oomph.setup.buildship.impl.BuildshipImportPackageImpl#getBuildshipImportTask()
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
    * The feature id for the '<em><b>Source Locators</b></em>' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS = SetupPackage.SETUP_TASK_FEATURE_COUNT + 0;

   /**
    * The feature id for the '<em><b>Gradle Task</b></em>' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK__GRADLE_TASK = SetupPackage.SETUP_TASK_FEATURE_COUNT + 1;

   /**
    * The number of structural features of the '<em>Task</em>' class.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    * @ordered
    */
   int BUILDSHIP_IMPORT_TASK_FEATURE_COUNT = SetupPackage.SETUP_TASK_FEATURE_COUNT + 2;


   /**
    * Returns the meta object for class '{@link net.ifao.oomph.setup.buildship.BuildshipImportTask <em>Task</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for class '<em>Task</em>'.
    * @see net.ifao.oomph.setup.buildship.BuildshipImportTask
    * @generated
    */
   EClass getBuildshipImportTask();

   /**
    * Returns the meta object for the containment reference list '{@link net.ifao.oomph.setup.buildship.BuildshipImportTask#getSourceLocators <em>Source Locators</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the containment reference list '<em>Source Locators</em>'.
    * @see net.ifao.oomph.setup.buildship.BuildshipImportTask#getSourceLocators()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EReference getBuildshipImportTask_SourceLocators();

   /**
    * Returns the meta object for the attribute '{@link net.ifao.oomph.setup.buildship.BuildshipImportTask#getGradleTask <em>Gradle Task</em>}'.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @return the meta object for the attribute '<em>Gradle Task</em>'.
    * @see net.ifao.oomph.setup.buildship.BuildshipImportTask#getGradleTask()
    * @see #getBuildshipImportTask()
    * @generated
    */
   EAttribute getBuildshipImportTask_GradleTask();

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
       * The meta object literal for the '{@link net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl <em>Task</em>}' class.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @see net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl
       * @see net.ifao.oomph.setup.buildship.impl.BuildshipImportPackageImpl#getBuildshipImportTask()
       * @generated
       */
      EClass BUILDSHIP_IMPORT_TASK = eINSTANCE.getBuildshipImportTask();

      /**
       * The meta object literal for the '<em><b>Source Locators</b></em>' containment reference list feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EReference BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS = eINSTANCE.getBuildshipImportTask_SourceLocators();

      /**
       * The meta object literal for the '<em><b>Gradle Task</b></em>' attribute feature.
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated
       */
      EAttribute BUILDSHIP_IMPORT_TASK__GRADLE_TASK = eINSTANCE.getBuildshipImportTask_GradleTask();

   }

} //BuildshipImportPackage
