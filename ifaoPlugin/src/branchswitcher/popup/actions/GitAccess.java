/**
 *
 */
package branchswitcher.popup.actions;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryBuilder;


public class GitAccess
{


   List<String> getBranches(java.io.File folder, LoggerAction logger)
      throws IOException, InterruptedException, GitAPIException
   {
      // open the git access object
      try (Git git = getGit(folder)) {
         // get the current branch
         String currentBranch = git.getRepository().getBranch();
         logger.println("--- branch for " + folder.getName() + " --- " + currentBranch);

         // create a list with all known (local) branches

         Set<String> listOfBranches = new HashSet<>();
         listOfBranches.addAll(git.branchList().call().stream()
               // map the branch references to string
               .map(ref -> getName(ref, currentBranch))
               // filter only valid, sort and return as list
               .filter(name -> name.length() > 0).collect(Collectors.toList()));

         listOfBranches.addAll(git.branchList().setListMode(ListMode.REMOTE).call().stream()
               // map the branch references to string
               .map(ref -> getName(ref, currentBranch))
               // filter only valid, sort and return as list
               .filter(name -> name.length() > 0).collect(Collectors.toList()));

         // return them
         return listOfBranches.stream().filter(name -> !name.startsWith("[") || originExists(listOfBranches, name.substring(1)))
               .sorted(this::sortBranches).collect(Collectors.toList());
      }
   }


   private boolean originExists(Set<String> listOfBranches, String name)
   {
      return !(listOfBranches.contains(name) || listOfBranches.contains("*" + name));
   }

   private int sortBranches(String a, String b)
   {
      String prefix = "*";
      if (a.startsWith(prefix) && !b.startsWith(prefix)) {
         return -1;
      }
      if (!a.startsWith(prefix) && b.startsWith(prefix)) {
         return 1;
      }
      if (a.startsWith("[") && !b.startsWith("[")) {
         return 1;
      }
      if (!a.startsWith("[") && b.startsWith("[")) {
         return -1;
      }
      int diff = compareNumber(a, b);
      if (diff != 0) {
         return diff;
      }
      return a.compareTo(b);
   }

   private int compareNumber(String a, String b)
   {
      Pattern number = Pattern.compile("(\\d+).(\\d+).(\\d+)");
      Matcher na = number.matcher(a);
      Matcher nb = number.matcher(b);
      if (na.find()) {
         if (nb.find()) {
            return getNumber(nb) - getNumber(na);
         }
         return -1;

      } else if (nb.find()) {
         return 1;
      }
      return 0;
   }

   private int getNumber(Matcher na)
   {
      return Integer.parseInt(na.group(3)) + Integer.parseInt(na.group(2)) * 1000 + Integer.parseInt(na.group(1)) * 1000 * 1000;
   }


   void selectBranch(java.io.File folder, String branch, LoggerAction logger)
      throws IOException, GitAPIException
   {

      // open the git access object
      try (Git git = getGit(folder)) {
         String localBranch = branch.startsWith("[") ? branch.substring(1) : branch;
         logger.println("--- select branch " + localBranch + " for " + folder.getName() + " ---");
         // if the branch does not match
         if (!localBranch.equals(git.getRepository().getBranch())) {
            // check if branch exists
            if (!git.branchList().call().stream().map(ref -> getName(ref, "")).filter(name -> name.equals(localBranch)).findAny()
                  .isPresent()) {
               logger.println("- create a new local from from origin/" + localBranch);
               // if not preset ... create a local branch
               git.branchCreate().setForce(true).setName(localBranch).setStartPoint("origin/" + localBranch).call();
            }
            // checkout the branch
            logger.println("- checkout branch " + localBranch);
            git.checkout().setName(localBranch).call();
         } else {
            logger.println("- branch is already up to date");
         }
      }
   }

   private Git getGit(java.io.File folder)
      throws IOException
   {
      return new Git(new RepositoryBuilder().setGitDir(new File(folder, ".git")).readEnvironment().findGitDir().build());
   }


   private String getName(Ref ref, String branch)
   {
      String name = ref.getName().replaceFirst(".*/origin/", "[").replaceAll("refs/heads/", "");
      if (name.equals(branch)) {
         name = "*" + name;
      }
      return name;
   }


}
