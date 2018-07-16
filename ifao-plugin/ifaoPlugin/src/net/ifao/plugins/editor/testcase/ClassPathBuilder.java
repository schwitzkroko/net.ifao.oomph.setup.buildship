/**
 *
 */
package net.ifao.plugins.editor.testcase;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;


/**
 * ClassPathBuilder
 *
 * <p>
 * Copyright &copy; 2018, i:FAO Group GmbH.
 *
 * @author brod
 */
public class ClassPathBuilder
{
   public static List<File> getClassesFolders(IProject project)
   {
      System.out.println("---------------------");
      List<File> classesFolder = new ArrayList<>();
      try {
         IProject[] referencedProjects = project.getReferencedProjects();
         addProjectClasspath(project, classesFolder);
         for (IProject iProject : referencedProjects) {
            addProjectClasspath(iProject, classesFolder);
         }

      }
      catch (CoreException e2) {
         // TODO Auto-generated catch block
         e2.printStackTrace();
      }
      return classesFolder;
   }

   private static void addProjectClasspath(IProject iProject, List<File> classesFolder)
      throws CoreException, JavaModelException
   {
      IProjectNature adapter = iProject.getNature(JavaCore.NATURE_ID);
      if (adapter != null) {
         IJavaProject javaProject = JavaCore.create(iProject);
         System.out.println(iProject.getLocationURI());
         classesFolder.addAll(getClassesFolder(iProject.getLocation().toFile()));
         IClasspathEntry[] resolvedClasspath = javaProject.getResolvedClasspath(true);
         if (resolvedClasspath != null) {
            classesFolder.addAll(Arrays.stream(resolvedClasspath).filter(ic -> ic.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
                  .map(ic -> ic.getPath().toFile()).collect(Collectors.toList()));
         }

      }
   }

   private static List<File> getClassesFolder(File f)
   {
      try {
         f = f.getCanonicalFile();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      List<File> lst = new ArrayList<>();
      String[] types = { "bin", "classes" };
      for (String type : types) {
         File file = new File(f, type);

         if (file.exists()) {
            File file1 = new File(file, "main");
            File file2 = new File(file, "test");
            boolean f1 = file1.exists();
            boolean f2 = file2.exists();
            if (f2 || f1) {
               if (f1) {
                  lst.add(file1);
               }
               if (f2) {
                  lst.add(file2);
               }
            } else {
               lst.add(file);
            }
            break;
         }
      }

      return lst;
   }

}
