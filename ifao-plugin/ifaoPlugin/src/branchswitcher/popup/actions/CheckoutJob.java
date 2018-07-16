/**
 *
 */
package branchswitcher.popup.actions;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.swt.widgets.Shell;


public class CheckoutJob
   implements IRunnableWithProgress
{
   private static final String RELEASE = "release/";

   GitAccess branchHandler = new GitAccess();
   private final java.io.File applicationFolder;
   private Shell shell;
   private IWorkspaceRoot root;

   private LoggerAction logger = new LoggerAction();

   CheckoutJob(Shell shell, File applicationFolder)
   {
      this.applicationFolder = applicationFolder;
      this.shell = shell;
      root = ResourcesPlugin.getWorkspace().getRoot();
   }


   @Override
   public void run(IProgressMonitor monitor)
      throws InvocationTargetException, InterruptedException
   {
      monitor.setTaskName("checkout branches:");

      SubMonitor progress = null;
      try {
         List<String> branches = branchHandler.getBranches(applicationFolder, logger);
         if (!branches.isEmpty()) {
            String branch = selectBranch(branches);

            if (branch != null && branch.length() > 0) {

               updateGradleInitDInEnvironment(branch);

               List<File> projectFiles = getEclipseProjectFiles(branch);

               progress = SubMonitor.convert(monitor, projectFiles.size() + 2);
               progress.setTaskName("checkout branches:");

               checkoutBranch(branch, progress, projectFiles);

            } else {
               return;
            }
         }
      }
      catch (Exception e) { // NOSONAR
         logger.warning("", e);
      }
      finally {
         if (progress != null) {
            progress.done();
         }
         if (logger.hasWarnings()) {
            shell.getDisplay().asyncExec(() -> MessageDialog.openInformation(shell, "BranchSwitcher", logger.toString()));
         }

      }

   }


   private void updateGradleInitDInEnvironment(String branch)
      throws IOException
   {
      File environment = new File(applicationFolder.getParentFile(), "environment");
      if (environment.exists()) {
         File globalPolarisProperties = new File(environment, "Gradle/globalPolarisProperties.gradle");
         File initD = new File(environment, "Gradle/init.d");
         File globalPolarisPropertiesInitD = new File(initD, "globalPolarisProperties.gradle");
         if (globalPolarisProperties.exists()) {
            if (!initD.exists() && initD.mkdirs()) {
               logger.println("> created folder " + initD.getAbsolutePath());
            }
            List<String> lines;
            if (globalPolarisPropertiesInitD.exists()) {
               lines = Files.readAllLines(globalPolarisPropertiesInitD.toPath());
            } else {
               lines = Files.readAllLines(globalPolarisProperties.toPath());
            }

            byte[] bytes = readLinesWithCorrectedParemeter(branch, lines);
            Files.write(globalPolarisPropertiesInitD.toPath(), bytes);
            logger.println("> update " + globalPolarisPropertiesInitD.getAbsolutePath());
         }
      }
   }

   private byte[] readLinesWithCorrectedParemeter(String branch, List<String> lines)
      throws UnsupportedEncodingException
   {
      Pattern compile = Pattern.compile("(.*useVersionFile\\W*=\\W*)(\\w+)(\\W.*)");
      String key = branch.startsWith("release/") ? "true" : "false";
      StringBuilder sb = new StringBuilder();
      lines.forEach(line -> {
         Matcher matcher = compile.matcher(line);
         if (matcher.find()) {
            line = matcher.group(1) + key + matcher.group(3);
            logger.println(">>> Set " + matcher.group(1) + key);
         }
         if (sb.length() > 0) {
            sb.append("\n");
         }
         sb.append(line);
      });

      byte[] bytes = sb.toString().getBytes("UTF-8");
      return bytes;
   }


   private String selectBranch(List<String> branches)
   {
      String[] branchItem = { "" };
      shell.getDisplay().syncExec(() -> branchItem[0] =
         new ComboDialog(shell).open("Select a branch\nof project " + applicationFolder.getName(), branches));
      return branchItem[0];
   }

   private void checkoutBranch(String branchId, SubMonitor progress, List<File> projectFiles)
      throws IOException, GitAPIException, CoreException
   {
      List<String> refreshProject = new ArrayList<>();

      // checkout the root branch
      String branch = checkoutRootBranch(branchId, progress, refreshProject);

      // get the version properties map
      Map<String, String> mapVersionProperties = getVersionProperties(branch);

      // checkout the sub projects
      projectFiles.forEach(sub -> {
         progress.subTask(sub.getName());
         progress.split(1);
         checkoutBranch(sub, branch, mapVersionProperties, refreshProject);
      });

      // finally refresh the projects
      progress.subTask("refresh projects");
      progress.split(1);
      refreshProject.forEach(this::refreshEclipseProject);

      if (logger.hasWarnings()) {
         logger.println("please Checkout manually");
      }
   }

   private String checkoutRootBranch(String branchId, SubMonitor progress, List<String> refreshProject)
      throws IOException, GitAPIException
   {
      progress.subTask(applicationFolder.getName());
      progress.split(1);
      String branch;
      if (!branchId.startsWith("*")) {
         // checkout branch
         branchHandler.selectBranch(applicationFolder, branchId, logger);
         refreshProject.add(applicationFolder.getName());
         if (branchId.startsWith("[")) {
            branch = branchId.substring(1);
         } else {
            branch = branchId;
         }
      } else {
         branch = branchId.substring(1);
      }
      return branch;
   }


   private void refreshEclipseProject(String name)

   {
      IProject project = root.getProject(name);
      if (project != null) {
         logger.println("-- refresh project " + project.getName());
         try {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);
         }
         catch (CoreException e) { // NOSONAR
            logger.println("WARNING: " + name + ": " + e.getLocalizedMessage());
         }
      }
   }


   private boolean isValidFolder(java.io.File file, Map<String, String> mapVersionProperties, List<String> projectNames)
   {
      return !file.equals(applicationFolder) && projectNames.contains(file.getName())
            && mapVersionProperties.get(file.getName()) != null;
   }

   private List<File> getEclipseProjectFiles(String branch)
      throws IOException
   {
      Map<String, String> mapVersionProperties = getVersionProperties(branch);

      List<String> projectNames = Arrays.stream(root.getProjects()).map(IProject::getName).collect(Collectors.toList());

      return Arrays.stream(applicationFolder.getParentFile().listFiles())
            .filter(file -> isValidFolder(file, mapVersionProperties, projectNames)).collect(Collectors.toList());
   }


   private Map<String, String> getVersionProperties(String branch)
      throws IOException
   {
      File versionFile = new File(applicationFolder, "version.properties");
      String groupKey = "(.*?)\\..*";
      Map<String, String> versionProperties;
      // for non releases, use always master
      if (branch.startsWith(RELEASE)) {
         // get the key
         String key = branch.substring(RELEASE.length()).split("-")[0];
         if (key.startsWith("_")) {
            versionProperties = getVersionProperties(versionFile, groupKey, number -> RELEASE + key + "-" + number);
         } else {
            versionProperties = getVersionProperties(versionFile, groupKey, number -> RELEASE + number);
         }
      } else { // no release project
         // map all default entries to master
         versionProperties = getVersionProperties(versionFile, groupKey, anyVersion -> "master");
         addBranchProperties(versionProperties, branch);
      }


      return versionProperties;
   }


   private void addBranchProperties(Map<String, String> versionProperties, String key)
      throws IOException
   {
      if (key.startsWith("_")) {
         String branchFile = "branch-" + key + ".properties";
         File brachFile = new File(applicationFolder, branchFile);
         if (brachFile.exists()) {
            Map<String, String> versionPropertiesBranch = getVersionProperties(brachFile, "(.*)", s -> s);
            versionProperties.putAll(versionPropertiesBranch);
         }
      }
   }

   private Map<String, String> getVersionProperties(File versionFile, String groupKey, Function<String, String> map)
      throws IOException
   {
      Pattern pattern = Pattern.compile("(.*)=" + groupKey);
      return Files.readAllLines(versionFile.toPath()).stream().filter(s -> pattern.matcher(s).matches())
            .collect(Collectors.toMap(s -> getGroup(s, pattern, 1), s -> map.apply(getGroup(s, pattern, 2))));
   }

   private void checkoutBranch(java.io.File sub, String branch, Map<String, String> mapVersionProperties,
                               List<String> refreshProject)
   {
      try {
         String version = mapVersionProperties.get(sub.getName());
         if (version == null) {
            version = branch;
         }

         branchHandler.selectBranch(sub, version, logger);
         refreshProject.add(sub.getName());
      }
      catch (Exception e) { // NOSONAR
         logger.warning("Project " + sub.getName() + ":", e);
      }
   }

   private String getGroup(String s, Pattern pattern, int group)
   {
      Matcher matcher = pattern.matcher(s);
      if (matcher.find()) {
         return matcher.group(group);
      }
      return "";
   }

}
