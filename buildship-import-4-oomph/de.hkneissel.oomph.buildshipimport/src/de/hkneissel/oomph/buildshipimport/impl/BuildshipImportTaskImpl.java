package de.hkneissel.oomph.buildshipimport.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.BuildConfiguration;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.workspace.GradleBuild;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.EObjectOutputStream;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.oomph.resources.SourceLocator;
import org.eclipse.oomph.setup.SetupTaskContext;
import org.eclipse.oomph.setup.Trigger;
import org.eclipse.oomph.setup.impl.SetupTaskImpl;
import org.eclipse.oomph.util.IOUtil;
import org.eclipse.oomph.util.PropertyFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gradleware.tooling.toolingclient.GradleDistribution;

import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportPlugin;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;
import de.hkneissel.oomph.buildshipimport.impl.buildship.BuildsUtil;
import de.hkneissel.oomph.buildshipimport.impl.buildship.WorkingSetsAddingProjectHandler;


/**
* <!-- begin-user-doc -->
* An implementation of the model object '<em><b>Task</b></em>'.
* <!-- end-user-doc -->
* <p>
* The following features are implemented:
* </p>
* <ul>
*   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getSourceLocators <em>Source Locators</em>}</li>
* </ul>
*
* @generated
*/
public class BuildshipImportTaskImpl
   extends SetupTaskImpl
   implements BuildshipImportTask
{

   private static final PropertyFile HISTORY =
      new PropertyFile(BuildshipImportPlugin.INSTANCE.getStateLocation().append("import-history.properties").toFile());


   private static final IWorkspaceRoot ROOT = EcorePlugin.getWorkspaceRoot();

   /**
   * The cached value of the '{@link #getSourceLocators() <em>Source Locators</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceLocators()
   * @generated
   * @ordered
   */
   protected EList<SourceLocator> sourceLocators;


   private static final Logger log = LoggerFactory.getLogger(BuildshipImportTaskImpl.class);

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
   protected BuildshipImportTaskImpl()
   {
      super();
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return TODO (Fliedner) add text for returnValue
   *
   * @generated
   */
   @Override
   protected EClass eStaticClass()
   {
      return BuildshipImportPackage.Literals.BUILDSHIP_IMPORT_TASK;
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return TODO (Fliedner) add text for returnValue
   *
   * @generated
   */
   @Override
   public EList<SourceLocator> getSourceLocators()
   {
      if (this.sourceLocators == null) {
         this.sourceLocators = new EObjectContainmentEList<>(SourceLocator.class, this,
               BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS);
      }
      return this.sourceLocators;
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <p> TODO rename otherEnd to pEnd, featureID to piID, msgs to pMsgs
   * @param otherEnd TODO (Fliedner) add text for param otherEnd
   * @param featureID TODO (Fliedner) add text for param featureID
   * @param msgs TODO (Fliedner) add text for param msgs
   * @return TODO (Fliedner) add text for returnValue
   *
   * @generated
   */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            return ((InternalEList<?>) getSourceLocators()).basicRemove(otherEnd, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <p> TODO rename featureID to piID, resolve to pbResolve, coreType to pbType
   * @param featureID TODO (Fliedner) add text for param featureID
   * @param resolve TODO (Fliedner) add text for param resolve
   * @param coreType TODO (Fliedner) add text for param coreType
   * @return TODO (Fliedner) add text for returnValue
   *
   * @generated
   */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            return getSourceLocators();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <p> TODO rename featureID to piID, newValue to pValue
   * @param featureID
   * @param newValue
   *
   * @generated
   */
   @SuppressWarnings("unchecked")
   @Override
   public void eSet(int featureID, Object newValue)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            getSourceLocators().clear();
            getSourceLocators().addAll((Collection<? extends SourceLocator>) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <p> TODO rename featureID to piID
   * @param featureID
   *
   * @generated
   */
   @Override
   public void eUnset(int featureID)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            getSourceLocators().clear();
            return;
      }
      super.eUnset(featureID);
   }

   /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <p> TODO rename featureID to piID
   * @param featureID
   * @return returnValue
   *
   * @generated
   */
   @Override
   public boolean eIsSet(int featureID)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            return this.sourceLocators != null && !this.sourceLocators.isEmpty();

         default:
            // do nothing
      }
      return super.eIsSet(featureID);
   }

   /**
   * TODO (Fliedner) add comment for method getProgressMonitorWork
   *
   * @return returnValue
   */
   @Override
   public int getProgressMonitorWork()
   {
      return 50;
   }

   /**
   * TODO (Fliedner) add comment for method setProjects
   *
   * @param sourceLocator
   * @param projects
   */
   private void setProjects(SourceLocator sourceLocator, IProject[] projects)
   {
      String key = getDigest(sourceLocator);
      StringBuilder value = new StringBuilder();
      for (IProject project : projects) {
         if (value.length() != 0) {
            value.append(' ');
         }

         value.append(URI.encodeSegment(project.getName(), false));
      }

      HISTORY.setProperty(key, value.toString());
   }

   /**
    * TODO: correct implementation (this one
    *
    * overrides @see org.eclipse.oomph.setup.SetupTask#isNeeded(org.eclipse.oomph.setup.SetupTaskContext)
    */
   @Override
   public boolean isNeeded(SetupTaskContext context)
      throws Exception
   {
      boolean needed = false;

      if (context.getTrigger() == Trigger.MANUAL) {
         needed = true;
      }

      final EList<SourceLocator> sourceLocators = getSourceLocators();
      log.debug("checking sourceLocators: {}", sourceLocators);

      outer: for (SourceLocator sourceLocator : sourceLocators) {
         IProject[] projects = getProjects(sourceLocator);
         if (projects == null) {
            needed = true;
            break;
         }

         for (IProject project : projects) {
            if (!project.exists()) {
               needed = true;
               break outer;
            }
         }
      }

      log.debug("import needed? {}", needed);
      return needed;
   }

   /**
    *
    * overrides @see org.eclipse.oomph.setup.SetupTask#perform(org.eclipse.oomph.setup.SetupTaskContext)
    */
   @Override
   public void perform(final SetupTaskContext context)
      throws Exception
   {
      final GradleDistribution gradleDistribution = GradleDistribution.fromBuild();

      final EList<SourceLocator> locs = getSourceLocators();
      final int size = this.sourceLocators.size();

      final MultiStatus status =
         new MultiStatus(BuildshipImportPlugin.INSTANCE.getSymbolicName(), 0, "Buildship import Analysis", null);

      final IProgressMonitor monitor = context.getProgressMonitor(true);
      monitor.beginTask("", 2 * size);

      try {
         List<BuildConfiguration> buildConfigurations = locs.stream().map(loc -> {
            String rootFolder = loc.getRootFolder();

            return BuildsUtil.createBuildConfiguration(rootFolder, gradleDistribution);
         }).collect(Collectors.toList());

         buildConfigurations.forEach(bconf -> {
            performImportProject(bconf, AsyncHandler.NO_OP, NewProjectHandler.IMPORT_AND_MERGE);
         });
      }
      finally {

         monitor.done();
      }

      BuildshipImportPlugin.INSTANCE.coreException(status);
   }


   public boolean performImportProject(final BuildConfiguration buildConfig, final AsyncHandler initializer,
                                       final NewProjectHandler newProjectHandler)
   {
      // TODO: we could populate this by "Buildship import" param (not by sourceLocator)
      Optional<List<String>> workingSetNames = Optional.empty();

      WorkingSetsAddingProjectHandler workingSetsAddingNewProjectHandler =
         new WorkingSetsAddingProjectHandler(newProjectHandler, workingSetNames);
      GradleBuild build = CorePlugin.gradleWorkspaceManager().getGradleBuild(buildConfig);
      build.synchronize(workingSetsAddingNewProjectHandler, initializer);
      return true;
   }


   private static IProject[] getProjects(SourceLocator sourceLocator)
   {
      String key = getDigest(sourceLocator);
      String value = HISTORY.getProperty(key, null);
      if (value != null) {
         List<IProject> projects = new ArrayList<>();
         for (String element : XMLTypeFactory.eINSTANCE.createNMTOKENS(value)) {
            projects.add(ROOT.getProject(URI.decode(element)));
         }

         return projects.toArray(new IProject[projects.size()]);
      }

      return null;
   }


   private static String getDigest(SourceLocator sourceLocator)
   {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try {
         EObjectOutputStream eObjectOutputStream = new BinaryResourceImpl.EObjectOutputStream(bytes, null);
         eObjectOutputStream.saveEObject((InternalEObject) sourceLocator, BinaryResourceImpl.EObjectOutputStream.Check.NOTHING);
         bytes.toByteArray();
         return XMLTypeFactory.eINSTANCE.convertBase64Binary(IOUtil.getSHA1(new ByteArrayInputStream(bytes.toByteArray())));
      }
      catch (IOException | NoSuchAlgorithmException ex) {
         BuildshipImportPlugin.INSTANCE.log(ex);
      }

      return null;
   }

}
