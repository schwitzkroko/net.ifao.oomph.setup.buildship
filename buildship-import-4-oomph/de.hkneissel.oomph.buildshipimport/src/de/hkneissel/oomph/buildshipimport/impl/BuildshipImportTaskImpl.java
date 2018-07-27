/**
 */
package de.hkneissel.oomph.buildshipimport.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.oomph.resources.EclipseProjectFactory;
import org.eclipse.oomph.resources.ProjectHandler;
import org.eclipse.oomph.resources.ResourcesUtil.ImportResult;
import org.eclipse.oomph.resources.SourceLocator;
import org.eclipse.oomph.resources.backend.BackendContainer;
import org.eclipse.oomph.resources.impl.SourceLocatorImpl;
import org.eclipse.oomph.setup.SetupTaskContext;
import org.eclipse.oomph.setup.Trigger;
import org.eclipse.oomph.setup.impl.SetupTaskImpl;
import org.eclipse.oomph.util.IOUtil;
import org.eclipse.oomph.util.MonitorUtil;
import org.eclipse.oomph.util.PropertyFile;
import org.eclipse.oomph.util.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportPlugin;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;

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
public class BuildshipImportTaskImpl extends SetupTaskImpl implements BuildshipImportTask {
	private static final PropertyFile HISTORY = new PropertyFile(
			BuildshipImportPlugin.INSTANCE.getStateLocation().append("import-history.properties").toFile());

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
	protected BuildshipImportTaskImpl() {
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
	protected EClass eStaticClass() {
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
	public EList<SourceLocator> getSourceLocators() {
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
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
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
	* @param featureID TODO (Fliedner) add text for param featureID
	* @param newValue TODO (Fliedner) add text for param newValue
	*
	* @generated
	*/
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
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
	* @param featureID TODO (Fliedner) add text for param featureID
	*
	* @generated
	*/
	@Override
	public void eUnset(int featureID) {
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
	* @param featureID TODO (Fliedner) add text for param featureID
	* @return TODO (Fliedner) add text for returnValue
	*
	* @generated
	*/
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
			return this.sourceLocators != null && !this.sourceLocators.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	* TODO (Fliedner) add comment for method getProgressMonitorWork
	*
	* @return TODO (Fliedner) add text for returnValue
	*
	* @author Fliedner
	*/
	@Override
	public int getProgressMonitorWork() {
		return 50;
	}

	/**
	* TODO (Fliedner) add comment for method setProjects
	*
	* <p> TODO rename sourceLocator to pLocator
	* @param sourceLocator TODO (Fliedner) add text for param sourceLocator
	* @param projects TODO (Fliedner) add text for param projects
	*
	* @author Fliedner
	*/
	private void setProjects(SourceLocator sourceLocator, IProject[] projects) {
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
	* overrides @see org.eclipse.oomph.setup.SetupTask#isNeeded(org.eclipse.oomph.setup.SetupTaskContext)
	* <p> TODO rename context to pContext
	* @param context TODO (Fliedner) add text for param context
	* @return TODO (Fliedner) add text for returnValue
	* @throws Exception
	*
	*/
	@Override
	public boolean isNeeded(SetupTaskContext context) throws Exception {
		if (context.getTrigger() == Trigger.MANUAL) {
			return true;
		}

		final EList<SourceLocator> sourceLocators = getSourceLocators();
		log.debug("checking sourceLocators: {}", sourceLocators);

		for (SourceLocator sourceLocator : sourceLocators) {
			IProject[] projects = getProjects(sourceLocator);
			if (projects == null) {
				return true;
			}

			for (IProject project : projects) {
				if (!project.exists()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	* TODO (Fliedner) add comment for method perform
	*
	* <p> TODO rename context to pfContext
	* @param context TODO (Fliedner) add text for param context
	* @throws Exception
	*
	* @author Fliedner
	*/
	@Override
	public void perform(final SetupTaskContext context) throws Exception {
		final EList<SourceLocator> locs = getSourceLocators();
		final int size = this.sourceLocators.size();

		final MultiStatus performStatus = new MultiStatus(BuildshipImportPlugin.INSTANCE.getSymbolicName(), 0,
				"Buildship import Analysis", null);

		final IProgressMonitor monitor = context.getProgressMonitor(true);
		monitor.beginTask("", 2 * size);

		try {
			Map<BackendContainer, IProject> backendContainers = locs.stream().flatMap((loc) -> {

				final String folder = loc.getRootFolder();
				context.log("Buildship import from " + folder);

				final MultiStatus childStatus = new MultiStatus(BuildshipImportPlugin.INSTANCE.getSymbolicName(), 0,
						"Buildship import Analysis of '" + folder + "'", null);

				final ProjectHandler.Collector collector = new ProjectHandler.Collector();
				try {
					loc.handleProjects(EclipseProjectFactory.LIST, collector, childStatus,
							MonitorUtil.create(monitor, 1));
				} catch (Exception ex) {

					SourceLocatorImpl.addStatus(performStatus, BuildshipImportPlugin.INSTANCE, folder, ex);
				}

				if (childStatus.getSeverity() >= IStatus.ERROR) {

					performStatus.add(childStatus);
					return Stream.empty();
				} else {

					final Map<IProject, BackendContainer> projectMap = collector.getProjectMap();

					Set<IProject> projects = projectMap.keySet();
					if (projects.isEmpty()) {

						log.debug("no projects at loc '{}'.", folder);

						context.log("No projects were found");
					}

					// SIDE EFFECT - FIXME: refactor
					setProjects(loc, projects.toArray(new IProject[projectMap.size()]));

					return projectMap.entrySet().stream();
				}
			})
					// key value switcheroo
					.collect(Collectors.toMap(Entry::getValue, Entry::getKey));

			importProjects(backendContainers, MonitorUtil.create(monitor, size));

		} finally {

			monitor.done();
		}

		BuildshipImportPlugin.INSTANCE.coreException(performStatus);
	}

	/**
	* TODO (Fliedner) add comment for method importProjects
	*
	* <p> TODO rename Map<BackendContainer to pfMap<BackendContainer, backendContainers to pContainers, monitor to pMonitor
	* @param Map<BackendContainer TODO (Fliedner) add text for param Map<BackendContainer
	* @param backendContainers TODO (Fliedner) add text for param backendContainers
	* @param monitor TODO (Fliedner) add text for param monitor
	* @return TODO (Fliedner) add text for returnValue
	* @throws CoreException
	*
	* @author Fliedner
	*/
	private static int importProjects(final Map<BackendContainer, IProject> backendContainers, IProgressMonitor monitor)
			throws CoreException {
		if (backendContainers.isEmpty()) {
			log.warn("no backendContainers");
			return 0;
		}

		log.debug("backendContainers - size {} - {}", backendContainers.size(), backendContainers);

		final AtomicInteger count = new AtomicInteger();

		final IWorkspace workspace = org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
		workspace.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				SubMonitor progress = SubMonitor.convert(monitor, backendContainers.size()).detectCancelation();

				try {
					for (Map.Entry<BackendContainer, IProject> entry : backendContainers.entrySet()) {
						BackendContainer backendContainer = entry.getKey();
						IProject project = entry.getValue();
						if (backendContainer.importIntoWorkspace(project,
								progress.newChild()) == ImportResult.IMPORTED) {
							count.incrementAndGet();
						}
					}
				} catch (Exception ex) {
					BuildshipImportPlugin.INSTANCE.coreException(ex);
				} finally {
					progress.done();
				}
			}
		}, monitor);

		return count.get();
	}

	/**
	* TODO (Fliedner) add comment for method getProjects
	*
	* <p> TODO rename sourceLocator to pLocator
	* @param sourceLocator TODO (Fliedner) add text for param sourceLocator
	* @return TODO (Fliedner) add text for returnValue
	*
	* @author Fliedner
	*/
	private static IProject[] getProjects(SourceLocator sourceLocator) {
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

	/**
	* TODO (Fliedner) add comment for method getDigest
	*
	* <p> TODO rename sourceLocator to pLocator
	* @param sourceLocator TODO (Fliedner) add text for param sourceLocator
	* @return TODO (Fliedner) add text for returnValue
	*
	* @author Fliedner
	*/
	private static String getDigest(SourceLocator sourceLocator) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			EObjectOutputStream eObjectOutputStream = new BinaryResourceImpl.EObjectOutputStream(bytes, null);
			eObjectOutputStream.saveEObject((InternalEObject) sourceLocator,
					BinaryResourceImpl.EObjectOutputStream.Check.NOTHING);
			bytes.toByteArray();
			return XMLTypeFactory.eINSTANCE
					.convertBase64Binary(IOUtil.getSHA1(new ByteArrayInputStream(bytes.toByteArray())));
		} catch (IOException ex) {
			BuildshipImportPlugin.INSTANCE.log(ex);
		} catch (NoSuchAlgorithmException ex) {
			BuildshipImportPlugin.INSTANCE.log(ex);
		}

		return null;
	}

}
