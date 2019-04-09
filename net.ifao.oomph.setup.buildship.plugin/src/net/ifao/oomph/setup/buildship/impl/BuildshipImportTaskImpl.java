package net.ifao.oomph.setup.buildship.impl;


import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.buildship.core.BuildConfiguration;
import org.eclipse.buildship.core.GradleBuild;
import org.eclipse.buildship.core.GradleCore;
import org.eclipse.buildship.core.SynchronizationResult;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.oomph.resources.SourceLocator;
import org.eclipse.oomph.setup.SetupTaskContext;
import org.eclipse.oomph.setup.Trigger;
import org.eclipse.oomph.setup.impl.SetupTaskImpl;
import org.eclipse.oomph.util.PropertyFile;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ifao.oomph.setup.buildship.BuildshipImportPackage;
import net.ifao.oomph.setup.buildship.BuildshipImportPlugin;
import net.ifao.oomph.setup.buildship.BuildshipImportTask;
import net.ifao.oomph.setup.buildship.impl.buildship.UIUtils;
import net.ifao.oomph.setup.buildship.util.HistoryUtil;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link net.ifao.oomph.setup.buildship.impl.BuildshipImportTaskImpl#getGradleTask <em>Gradle Task</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BuildshipImportTaskImpl
   extends SetupTaskImpl
   implements BuildshipImportTask
{

   private static final String PLUGIN_ID = BuildshipImportPlugin.INSTANCE.getSymbolicName();

   private static final PropertyFile HISTORY =
      new PropertyFile(BuildshipImportPlugin.INSTANCE.getStateLocation().append("import-history.properties").toFile());


   private final AtomicBoolean gradleViewsVisible = new AtomicBoolean(Boolean.FALSE);


   private static final Logger log = LoggerFactory.getLogger(BuildshipImportTaskImpl.class);


   /**
    * The cached value of the '{@link #getSourceLocators() <em>Source Locators</em>}' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getSourceLocators()
    * @generated
    * @ordered
    */
   protected EList<SourceLocator> sourceLocators;

   /**
    * The default value of the '{@link #getGradleTask() <em>Gradle Task</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getGradleTask()
    * @generated
    * @ordered
    */
   protected static final String GRADLE_TASK_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getGradleTask() <em>Gradle Task</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see #getGradleTask()
    * @generated
    * @ordered
    */
   protected String gradleTask = GRADLE_TASK_EDEFAULT;

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
    * @generated
    */
   @Override
   public EList<SourceLocator> getSourceLocators()
   {
      if (sourceLocators == null) {
         sourceLocators = new EObjectContainmentEList<>(SourceLocator.class, this,
               BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS);
      }
      return sourceLocators;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String getGradleTask()
   {
      return gradleTask;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void setGradleTask(String newGradleTask)
   {
      String oldGradleTask = gradleTask;
      gradleTask = newGradleTask;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK,
               oldGradleTask, gradleTask));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
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
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            return getSourceLocators();
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
            return getGradleTask();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
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
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
            setGradleTask((String) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void eUnset(int featureID)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            getSourceLocators().clear();
            return;
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
            setGradleTask(GRADLE_TASK_EDEFAULT);
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID)
   {
      switch (featureID) {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            return sourceLocators != null && !sourceLocators.isEmpty();
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
            return GRADLE_TASK_EDEFAULT == null ? gradleTask != null : !GRADLE_TASK_EDEFAULT.equals(gradleTask);
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public String toString()
   {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuilder result = new StringBuilder(super.toString());
      result.append(" (gradleTask: ");
      result.append(gradleTask);
      result.append(')');
      return result.toString();
   }

   /**
    * overrides @see org.eclipse.oomph.setup.SetupTask#isNeeded(org.eclipse.oomph.setup.SetupTaskContext)
    */
   @Override
   public boolean isNeeded(SetupTaskContext arg0)
      throws Exception
   {
      boolean needed = false;

      if (arg0.getTrigger() == Trigger.MANUAL) {
         needed = true;
      }

      final EList<SourceLocator> sourceLocators = getSourceLocators();
      log.debug("checking sourceLocators: {}", sourceLocators);

      outer: for (SourceLocator sourceLocator : sourceLocators) {
         IProject[] projects = HistoryUtil.getProjects(HISTORY, sourceLocator);
         if (projects.length <= 0) {
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
    * overrides @see org.eclipse.oomph.setup.SetupTask#perform(org.eclipse.oomph.setup.SetupTaskContext)
    */
   @Override
   public void perform(final SetupTaskContext context)
      throws Exception
   {
      final EList<SourceLocator> givenLocs = getSourceLocators();
      final Optional<String> givenInitialTask = Optional.ofNullable(getGradleTask());

      final MultiStatus overallStatus = new MultiStatus(PLUGIN_ID, 0, "Buildship import Analysis", null);

      final IProgressMonitor monitor = context.getProgressMonitor(true);
      monitor.beginTask("", 2 * givenLocs.size());


      try {

         final Function<SourceLocator, String> toRootFolder = SourceLocator::getRootFolder;

         final Function<String, BuildConfiguration> folderToBuildConfiguration = folder -> {
            final File folderFile = new File(folder);
            return BuildConfiguration.forRootProjectDirectory(folderFile).overrideWorkspaceConfiguration(false).build();
         };


         final Map<SourceLocator, BuildConfiguration> locToBuildConf =
            givenLocs.stream().collect(Collectors.toMap(loc -> loc, toRootFolder.andThen(folderToBuildConfiguration)));

         locToBuildConf.entrySet().forEach(e -> {

            final SourceLocator loc = e.getKey();
            final BuildConfiguration bconf = e.getValue();

            final GradleBuild build = GradleCore.getWorkspace().createBuild(bconf);


            UIUtils.asyncSetGradleViewsAreVisible(this.gradleViewsVisible);


            //  "This is a long-running operation which blocks the current thread until completion..."
            final SynchronizationResult syncRes = build.synchronize(monitor);
            final IStatus syncResStatus = syncRes.getStatus();

            log.debug("sync result status for {}: {}", loc, syncResStatus);
            // report to multistatus
            overallStatus.add(syncResStatus);


            // using connection api after a successful import
            if (syncResStatus.isOK()) {

               // try to execute a task wehn given
               givenInitialTask.ifPresent(taskName -> {

                  try {
                     log.debug("run task '{}' for loc '{}'", taskName, loc);

                     //  "This is a long-running operation which blocks the current thread until completion..."
                     build.withConnection(conn -> {
                        conn.newBuild().forTasks(taskName).run();
                        return null;
                     }, monitor);
                  }
                  catch (Exception taskEx) {

                     overallStatus.add(createStatus(IStatus.WARNING, "failed to run task '" + taskName + "'", taskEx));
                     log.error("error on running task.", taskEx);
                  }

               });


               // remember projects that were just imported
               try {
                  build.withConnection((conn) -> {

                     final Set<EclipseProject> allEclipseProjects =
                        HistoryUtil.getAllEclipseProjects(conn.getModel(EclipseProject.class));

                     HistoryUtil.setProjects(HISTORY, loc, allEclipseProjects);

                     return null;
                  }, monitor);
               }
               catch (Exception ex) {
                  log.warn("error.", ex);
               }
            }

         });
      }
      catch (Exception e) {

         final String msg = "error on Buildship import.";
         log.error(msg, e);
         overallStatus.add(createStatus(IStatus.ERROR, msg, e));
      }
      finally {

         monitor.done();
      }

      BuildshipImportPlugin.INSTANCE.coreException(overallStatus);
   }

   /**
    * create a new error status for plugin
    *
    * @param statusInt like {@link IStatus#WARNING}
    * @param msg msg to show
    * @param ex
    *
    * @return eclipse core status object
    */
   private static Status createStatus(final int statusInt, final String msg, final Exception ex)
   {
      return new Status(statusInt, PLUGIN_ID, msg, ex);
   }

   /**
    * overrides @see org.eclipse.oomph.setup.impl.SetupTaskImpl#getProgressMonitorWork()
    */
   @Override
   public int getProgressMonitorWork()
   {
      return 50;
   }


} //BuildshipImportTaskImpl
