/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package de.hkneissel.oomph.buildshipimport.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.BuildConfiguration;
import org.eclipse.buildship.core.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.util.progress.ToolingApiJob;
import org.eclipse.buildship.core.workspace.GradleBuild;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.buildship.core.workspace.internal.SynchronizeGradleBuildsJob;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.oomph.setup.SetupTaskContext;
import org.eclipse.oomph.setup.impl.SetupTaskImpl;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.gradleware.tooling.toolingmodel.repository.FixedRequestAttributes;

import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Buildship Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getProjectRootDirectory <em>Project Root Directory</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getJavaHome <em>Java Home</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getGradleUserHome <em>Gradle User Home</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getJvmArguments <em>Jvm Arguments</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getArguments <em>Arguments</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getImportWaitTime <em>Import Wait Time</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getGradleTask <em>Gradle Task</em>}</li>
 *   <li>{@link de.hkneissel.oomph.buildshipimport.impl.BuildshipImportTaskImpl#getGradleBuildDirectory <em>Gradle Build Directory</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BuildshipImportTaskImpl extends SetupTaskImpl implements BuildshipImportTask
{
  private static final boolean DEBUG = false;

  /**
   * The default value of the '{@link #getProjectRootDirectory() <em>Project Root Directory</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProjectRootDirectory()
   * @generated
   * @ordered
   */
  protected static final URI PROJECT_ROOT_DIRECTORY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getProjectRootDirectory() <em>Project Root Directory</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProjectRootDirectory()
   * @generated
   * @ordered
   */
  protected URI projectRootDirectory = PROJECT_ROOT_DIRECTORY_EDEFAULT;

  /**
   * The default value of the '{@link #getJavaHome() <em>Java Home</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJavaHome()
   * @generated
   * @ordered
   */
  protected static final URI JAVA_HOME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getJavaHome() <em>Java Home</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJavaHome()
   * @generated
   * @ordered
   */
  protected URI javaHome = JAVA_HOME_EDEFAULT;

  /**
   * The default value of the '{@link #getGradleUserHome() <em>Gradle User Home</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGradleUserHome()
   * @generated
   * @ordered
   */
  protected static final URI GRADLE_USER_HOME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getGradleUserHome() <em>Gradle User Home</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGradleUserHome()
   * @generated
   * @ordered
   */
  protected URI gradleUserHome = GRADLE_USER_HOME_EDEFAULT;

  /**
   * The default value of the '{@link #getJvmArguments() <em>Jvm Arguments</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJvmArguments()
   * @generated
   * @ordered
   */
  protected static final String JVM_ARGUMENTS_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getJvmArguments() <em>Jvm Arguments</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJvmArguments()
   * @generated
   * @ordered
   */
  protected String jvmArguments = JVM_ARGUMENTS_EDEFAULT;

  /**
   * The default value of the '{@link #getArguments() <em>Arguments</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArguments()
   * @generated
   * @ordered
   */
  protected static final String ARGUMENTS_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getArguments() <em>Arguments</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArguments()
   * @generated
   * @ordered
   */
  protected String arguments = ARGUMENTS_EDEFAULT;

  /**
   * The default value of the '{@link #getImportWaitTime() <em>Import Wait Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImportWaitTime()
   * @generated
   * @ordered
   */
  protected static final int IMPORT_WAIT_TIME_EDEFAULT = 30;

  /**
   * The cached value of the '{@link #getImportWaitTime() <em>Import Wait Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImportWaitTime()
   * @generated
   * @ordered
   */
  protected int importWaitTime = IMPORT_WAIT_TIME_EDEFAULT;

  /**
   * The default value of the '{@link #getGradleTask() <em>Gradle Task</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGradleTask()
   * @generated
   * @ordered
   */
  protected static final String GRADLE_TASK_EDEFAULT = "eclipse";

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
   * The default value of the '{@link #getGradleBuildDirectory() <em>Gradle Build Directory</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGradleBuildDirectory()
   * @generated
   * @ordered
   */
  protected static final URI GRADLE_BUILD_DIRECTORY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getGradleBuildDirectory() <em>Gradle Build Directory</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGradleBuildDirectory()
   * @generated
   * @ordered
   */
  protected URI gradleBuildDirectory = GRADLE_BUILD_DIRECTORY_EDEFAULT;

  private static final int PRIORITY = PRIORITY_DEFAULT;

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
  public URI getProjectRootDirectory()
  {
    return this.projectRootDirectory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setProjectRootDirectory(URI newProjectRootDirectory)
  {
    URI oldProjectRootDirectory = this.projectRootDirectory;
    this.projectRootDirectory = newProjectRootDirectory;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY, oldProjectRootDirectory, this.projectRootDirectory));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public URI getJavaHome()
  {
    return this.javaHome;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJavaHome(URI newJavaHome)
  {
    URI oldJavaHome = this.javaHome;
    this.javaHome = newJavaHome;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JAVA_HOME, oldJavaHome, this.javaHome));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public URI getGradleUserHome()
  {
    return this.gradleUserHome;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setGradleUserHome(URI newGradleUserHome)
  {
    URI oldGradleUserHome = this.gradleUserHome;
    this.gradleUserHome = newGradleUserHome;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME, oldGradleUserHome, this.gradleUserHome));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * The contents of this attribute is currently ignored.
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getJvmArguments()
  {
    return this.jvmArguments;
  }

  /**
   * <!-- begin-user-doc -->
   * The contents of this attribute is currently ignored.
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJvmArguments(String newJvmArguments)
  {
    String oldJvmArguments = this.jvmArguments;
    this.jvmArguments = newJvmArguments;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS, oldJvmArguments, this.jvmArguments));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getArguments()
  {
    return this.arguments;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setArguments(String newArguments)
  {
    String oldArguments = this.arguments;
    this.arguments = newArguments;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__ARGUMENTS, oldArguments, this.arguments));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getImportWaitTime()
  {
    return this.importWaitTime;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setImportWaitTime(int newImportWaitTime)
  {
    int oldImportWaitTime = this.importWaitTime;
    this.importWaitTime = newImportWaitTime;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME, oldImportWaitTime, this.importWaitTime));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getGradleTask()
  {
    return this.gradleTask;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setGradleTask(String newGradleTask)
  {
    String oldGradleTask = this.gradleTask;
    this.gradleTask = newGradleTask;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK, oldGradleTask, this.gradleTask));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public URI getGradleBuildDirectory()
  {
    return this.gradleBuildDirectory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setGradleBuildDirectory(URI newGradleBuildDirectory)
  {
    URI oldGradleBuildDirectory = this.gradleBuildDirectory;
    this.gradleBuildDirectory = newGradleBuildDirectory;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY, oldGradleBuildDirectory, this.gradleBuildDirectory));
    }
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
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY:
        return getProjectRootDirectory();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JAVA_HOME:
        return getJavaHome();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME:
        return getGradleUserHome();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS:
        return getJvmArguments();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__ARGUMENTS:
        return getArguments();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME:
        return getImportWaitTime();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
        return getGradleTask();
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY:
        return getGradleBuildDirectory();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID) {
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY:
        setProjectRootDirectory((URI)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JAVA_HOME:
        setJavaHome((URI)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME:
        setGradleUserHome((URI)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS:
        setJvmArguments((String)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__ARGUMENTS:
        setArguments((String)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME:
        setImportWaitTime((Integer)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
        setGradleTask((String)newValue);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY:
        setGradleBuildDirectory((URI)newValue);
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
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY:
        setProjectRootDirectory(PROJECT_ROOT_DIRECTORY_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JAVA_HOME:
        setJavaHome(JAVA_HOME_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME:
        setGradleUserHome(GRADLE_USER_HOME_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS:
        setJvmArguments(JVM_ARGUMENTS_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__ARGUMENTS:
        setArguments(ARGUMENTS_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME:
        setImportWaitTime(IMPORT_WAIT_TIME_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
        setGradleTask(GRADLE_TASK_EDEFAULT);
        return;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY:
        setGradleBuildDirectory(GRADLE_BUILD_DIRECTORY_EDEFAULT);
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
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__PROJECT_ROOT_DIRECTORY:
        return PROJECT_ROOT_DIRECTORY_EDEFAULT == null ? this.projectRootDirectory != null : !PROJECT_ROOT_DIRECTORY_EDEFAULT.equals(this.projectRootDirectory);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JAVA_HOME:
        return JAVA_HOME_EDEFAULT == null ? this.javaHome != null : !JAVA_HOME_EDEFAULT.equals(this.javaHome);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_USER_HOME:
        return GRADLE_USER_HOME_EDEFAULT == null ? this.gradleUserHome != null : !GRADLE_USER_HOME_EDEFAULT.equals(this.gradleUserHome);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__JVM_ARGUMENTS:
        return JVM_ARGUMENTS_EDEFAULT == null ? this.jvmArguments != null : !JVM_ARGUMENTS_EDEFAULT.equals(this.jvmArguments);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__ARGUMENTS:
        return ARGUMENTS_EDEFAULT == null ? this.arguments != null : !ARGUMENTS_EDEFAULT.equals(this.arguments);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__IMPORT_WAIT_TIME:
        return this.importWaitTime != IMPORT_WAIT_TIME_EDEFAULT;
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
        return GRADLE_TASK_EDEFAULT == null ? this.gradleTask != null : !GRADLE_TASK_EDEFAULT.equals(this.gradleTask);
      case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_BUILD_DIRECTORY:
        return GRADLE_BUILD_DIRECTORY_EDEFAULT == null ? this.gradleBuildDirectory != null : !GRADLE_BUILD_DIRECTORY_EDEFAULT.equals(this.gradleBuildDirectory);
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
    result.append(" (ProjectRootDirectory: ");
    result.append(this.projectRootDirectory);
    result.append(", JavaHome: ");
    result.append(this.javaHome);
    result.append(", GradleUserHome: ");
    result.append(this.gradleUserHome);
    result.append(", JvmArguments: ");
    result.append(this.jvmArguments);
    result.append(", Arguments: ");
    result.append(this.arguments);
    result.append(", ImportWaitTime: ");
    result.append(this.importWaitTime);
    result.append(", GradleTask: ");
    result.append(this.gradleTask);
    result.append(", GradleBuildDirectory: ");
    result.append(this.gradleBuildDirectory);
    result.append(')');
    return result.toString();
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }

  /**
   * Lets assume, that a project freshly retrieved from version control has no
   * or an invalid .settings/gradle.prefs file. So we check if the file
   * exists and its contents is consistent with the configuration
   * of this task.
   */
  @Override
  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    // is the settings file present?
    final File projectRootDir = determineImportDirectory();
    if (projectRootDir == null)
    {
      context.log("ProjectRootDirectory not set");
      return true;
    }

    final File javaHomeDir = asFile(getJavaHome(), "JavaHome");

    File settings = new File(projectRootDir, ".settings/gradle.prefs");
    if (!settings.exists())
    {
      if (DEBUG)
      {
        context.log("Not found: " + settings);
      }
      return true;
    }

    try
    {
      Map<String, String> entries = readGradleSettings(settings);

      String dir = entries.get("project_dir");
      if (DEBUG)
      {
        context.log("project_dir := " + dir);
      }
      if (dir == null)
      {
        return true;
      }
      if (!projectRootDir.equals(new File(dir).getAbsoluteFile()))
      {
        if (DEBUG)
        {
          context.log("projectRootDir := " + projectRootDir);
        }
        return true;
      }

      dir = entries.get("connection_project_dir");
      if (DEBUG)
      {
        context.log("connection_project_dir := " + dir);
      }
      if (dir == null)
      {
        return true;
      }
      if (!projectRootDir.equals(new File(dir).getAbsoluteFile()))
      {
        if (DEBUG)
        {
          context.log("projectRootDir := " + projectRootDir);
        }
        return true;
      }

      dir = entries.get("connection_java_home");
      if (DEBUG)
      {
        context.log("connection_java_home := " + dir);
      }
      if (dir == null)
      {
        if (javaHomeDir != null)
        {
          return true;
        }
      }
      else
      {
        if (javaHomeDir != null && !javaHomeDir.equals(new File(dir).getAbsoluteFile()))
        {
          if (DEBUG)
          {
            context.log("javaHomeDir := " + javaHomeDir);
          }
          return true;
        }
      }
    }
    catch (IOException e)
    {
      if (DEBUG)
      {
        context.log(e);
      }
      return true;
    }
    catch (JsonSyntaxException e)
    {
      if (DEBUG)
      {
        context.log(e);
      }
      return true;
    }

    // Seems the gradle settings are already ok, so there is nothing to be done any more.

    context.log(".settings/gradle.prefs up-to-date, nothing to be done.");
    return false;
  }

  @Override
  public void perform(final SetupTaskContext context) throws Exception
  {
    final File projectRootDir = determineImportDirectory();
    if (projectRootDir == null)
    {
      throw new NullPointerException("ProjectRootDirectory not set");
    }

    final GradleDistribution gradleDistribution = GradleDistribution.fromBuild();
    final File gradleUserDir = asFile(getGradleUserHome(), "GradleUserHome");
    final File javaHomeDir = asFile(getJavaHome(), "JavaHome");

    // TODO: initialize argument lists
    final List<String> jvmArguments = Collections.emptyList();
    final List<String> arguments = Collections.emptyList();

    final FixedRequestAttributes requestAttributes = new FixedRequestAttributes(projectRootDir, gradleUserDir, gradleDistribution, javaHomeDir, jvmArguments,
        arguments);
    final GradleBuild gradleBuild = CorePlugin.gradleWorkspaceManager().getGradleBuild(requestAttributes);

    // Working sets are handled by oomph, so we do not need to handle them here.
    List<String> workingSets = Collections.emptyList();

    final CountDownLatch latch = new CountDownLatch(1);
    AsyncHandler initializer = AsyncHandler.NO_OP;

    context.log("Importing gradle projects from " + projectRootDir);

    // new SynchronizeGradleProjectJob(requestAttributes, workingSets, initializer);
    ToolingApiJob synchronizeJob = SynchronizeGradleBuildsJob.forSingleGradleBuild(gradleBuild, NewProjectHandler.IMPORT_AND_MERGE, initializer); // new SynchronizeGradleProjectJob(requestAttributes, workingSets, initializer);
    synchronizeJob.addJobChangeListener(new JobChangeAdapter()
    {
      @Override
      public void done(IJobChangeEvent event)
      {
        try
        {
          if (event.getResult().isOK())
          {
            context.log("Import completed successfuly.");

            // Maybe this should be moved into an extra oomph setup task?
            ensureGradleViewsAreVisible();

            // Run the initial gradle task ...

            String task = getGradleTask();
            if (task != null && task.length() > 0)
            {
              List<String> tasks = new ArrayList<>();
              tasks.add(task);

              File workingDir = asFile(getGradleBuildDirectory(), "GradleBuildDirectory");
              if (workingDir == null)
              {
                workingDir = projectRootDir;
              }

              context.log("Executing gradle task '" + task + "' in " + workingDir);

              final GradleRunConfigurationAttributes attr = new GradleRunConfigurationAttributes(tasks,
                  workingDir.toString(), null,
                  gradleUserDir == null ? "" : gradleUserDir.toString(),
                  javaHomeDir == null ? "" : javaHomeDir.toString(), jvmArguments, arguments, true,
                  true, false, false, false);

              // Create/reuse a launch configuration for the given attributes ...
              ILaunchConfiguration launchConfiguration = CorePlugin.gradleLaunchConfigurationManager().getOrCreateRunConfiguration(attr);

              // ... and launch the launch configuration.
              DebugUITools.launch(launchConfiguration, ILaunchManager.RUN_MODE);
            }
            else
            {
              context.log("No gradle task defined.");
            }
          }
          else
          {
            throw new RuntimeException("Buildship import failed for " + projectRootDir);
          }
        }
        finally
        {
          latch.countDown();
        }
      }
    });

    synchronizeJob.schedule();

    int timeout = getImportWaitTime();
    if (timeout > 0)
    {
      context.log("Waiting up to " + timeout + " seconds for import to complete");
      if (!latch.await(timeout, TimeUnit.SECONDS))
      {
        throw new RuntimeException("Timeout waiting for import task to complete");
      }
    }
  }

  private File asFile(URI uri_, String name_)
  {
    File result = null;

    if (uri_ == null || "".equals(uri_.toString()))
    {
      return null;
    }

    String scheme = uri_.scheme();
    if (scheme == null)
    {
      // The property may be set to the path directly, not to the locations uri.
      // We will accept this too.
      result = new File(uri_.path());
    }
    else if ("file".equals(scheme))
    {
      result = new File(uri_.toFileString());
    }
    else
    {
      throw new IllegalArgumentException(name_ + " must use 'file:' scheme, not '" + scheme + ":' (" + uri_ + ")");
    }

    try
    {
      return result.getCanonicalFile();
    }
    catch (IOException e)
    {
      return result.getAbsoluteFile();
    }
  }

  /**
   * Determines the directory from which the projects will be imported.
   *
   * @return the import directory nor null if no valid directory could be determined.
   */
  private File determineImportDirectory()
  {
    File projectRootDir = asFile(getProjectRootDirectory(), "ProjectRootDirectory");
    return projectRootDir;
  }

  @Override
  public void dispose()
  {
  }

  private Map<String, String> readGradleSettings(File settings) throws IOException, JsonSyntaxException
  {
    String json = null;
    InputStream inputStream = null;

    try
    {
      inputStream = new FileInputStream(settings);
      json = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
    }
    finally
    {
      try
      {
        inputStream.close();
      }
      catch (IOException e)
      {
        // ignore
      }
    }

    // Settings file found, verify that project directory and java home are correct

    JsonParser parser = new JsonParser();
    return extractEntries(null, parser.parse(json));
  }

  private Map<String, String> extractEntries(String name, JsonElement jsonElement)
  {
    Map<String, String> result = new HashMap<>();

    if (jsonElement.isJsonNull())
    {
      // ignore
    }
    else if (jsonElement.isJsonObject())
    {
      result.putAll(extractEntries(jsonElement.getAsJsonObject()));
    }
    else if (jsonElement.isJsonArray())
    {

    }
    else if (name != null)
    {
      result.put(name, jsonElement.getAsString());
    }

    return result;
  }

  private Map<String, String> extractEntries(JsonObject jsonObject)
  {
    Map<String, String> result = new HashMap<>();

    for (Entry<String, JsonElement> entry : jsonObject.entrySet())
    {
      result.putAll(extractEntries(entry.getKey(), entry.getValue()));
    }
    return result;
  }

  // ----------------------------------------------------------------------
  // The code below is copied from the buildship ui plugin -
  // not nice, but the plugin does not export anything, so i don't know
  // how to access it else.
  // ----------------------------------------------------------------------

  private void ensureGradleViewsAreVisible()
  {
    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        showView("org.eclipse.buildship.ui.views.taskview", null, IWorkbenchPage.VIEW_ACTIVATE);
        showView("org.eclipse.buildship.ui.views.executionview", null, IWorkbenchPage.VIEW_VISIBLE);
      }
    });
  }

  /**
   * Shows the view with the given id and secondary id in the given mode.
   *
   * @param viewId the id of the view
   * @param secondaryId the secondary id of the view, or {@code null} for no secondary id
   * @param mode the activation mode, must be {@link org.eclipse.ui.IWorkbenchPage#VIEW_ACTIVATE},
   *            {@link org.eclipse.ui.IWorkbenchPage#VIEW_VISIBLE} or
   *            {@link org.eclipse.ui.IWorkbenchPage#VIEW_CREATE}
   * @param <T> the expected type of the view
   * @return the shown view, never null
   * @throws RuntimeException thrown if the view cannot be initialized correctly
   */
  public static <T extends IViewPart> T showView(String viewId, String secondaryId, int mode)
  {
    IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    try
    {
      @SuppressWarnings("unchecked")
      T view = (T)activeWorkbenchWindow.getActivePage().showView(viewId, secondaryId, mode);
      return view;
    }
    catch (PartInitException e)
    {
      throw new RuntimeException(String.format("Cannot show view with id %s and secondary id %s.", viewId, secondaryId));
    }
  }

}
