/**
 *
 */
package net.ifao.oomph.buildshipimport.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.EObjectOutputStream;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.oomph.resources.SourceLocator;
import org.eclipse.oomph.util.IOUtil;
import org.eclipse.oomph.util.PropertyFile;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ifao.oomph.buildshipimport.BuildshipImportPlugin;


/**
 * util to help remember existing project imports
 *
 * <p>
 * Copyright &copy; 2019, i:FAO Group GmbH.
 */
public final class HistoryUtil
{
   private static final Logger log = LoggerFactory.getLogger(HistoryUtil.class);


   private HistoryUtil()
   {
      // util
   }


   public static void setProjects(final PropertyFile historyRef, final SourceLocator sourceLocator,
                                  final Set<EclipseProject> buildProjects)
   {
      final Set<String> acceptNames = buildProjects.stream().map(EclipseProject::getName).collect(Collectors.toSet());
      log.debug("filter against build projects, accept names: {}", acceptNames);

      final IProject[] projects = Arrays.stream(ResourcesPlugin.getWorkspace().getRoot().getProjects())
            .filter(iproj -> acceptNames.contains(iproj.getName())).toArray(IProject[]::new);

      log.debug("for loc '{}', remember projects: {}", sourceLocator, Arrays.toString(projects));
      setProjects(historyRef, sourceLocator, projects);
   }

   /**
    * TODO (Jochen Fliedner) comment the Method setProjects
    *
    * @param historyRef
    * @param sourceLocator
    * @param projects
    */
   private static void setProjects(final PropertyFile historyRef, final SourceLocator sourceLocator, final IProject[] projects)
   {
      String key = getDigest(sourceLocator);
      StringBuilder value = new StringBuilder();
      for (IProject project : projects) {
         if (value.length() != 0) {
            value.append(' ');
         }

         value.append(URI.encodeSegment(project.getName(), false));
      }

      historyRef.setProperty(key, value.toString());
   }

   /**
    * TODO (Jochen Fliedner) comment the Method getProjects
    *
    * @param historyRef
    * @param sourceLocator
    * @return
    */
   public static IProject[] getProjects(final PropertyFile historyRef, final SourceLocator sourceLocator)
   {
      final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

      final String key = getDigest(sourceLocator);
      final String value = historyRef.getProperty(key, null);

      final List<IProject> projects = new ArrayList<>();
      if (value != null) {
         for (final String element : XMLTypeFactory.eINSTANCE.createNMTOKENS(value)) {
            projects.add(root.getProject(URI.decode(element)));
         }
      }

      return projects.toArray(new IProject[projects.size()]);
   }

   /**
   * TODO (Jochen Fliedner) comment the Method getDigest
   *
   * @param sourceLocator
   * @return
   */
   public static String getDigest(final SourceLocator sourceLocator)
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


   public static Set<EclipseProject> getAllEclipseProjects(EclipseProject root)
   {
      return getAllEclipseProjects(root, new HashSet<>());
   }

   private static Set<EclipseProject> getAllEclipseProjects(EclipseProject project, HashSet<EclipseProject> result)
   {
      result.add(project);
      project.getChildren().forEach(p -> getAllEclipseProjects(p, result));
      return result;
   }
}
