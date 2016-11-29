package net.ifao.plugins.action;


import java.io.*;
import java.util.*;

import ifaoplugin.Util;
import net.ifao.util.Execute;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;


public class UpdateH
   extends AbstractHandler
{

   @Override
   public Object execute(ExecutionEvent arg0)
      throws ExecutionException
   {
      new Thread()
      {

         @Override
         public void run()
         {
            IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

            Hashtable<String, IFile> ht = new Hashtable<>();
            for (IProject iProject : projects) {
               IFile file = iProject.getFile("/lib/build/UpdateFromH.bat");

               if (file != null && file.exists()) {
                  IFile fileCvs = iProject.getFile("/CVS/Entries");
                  if (fileCvs != null && fileCvs.exists()) {
                     String sName = iProject.getName();
                     try {
                        InputStream contents = fileCvs.getContents();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(contents));
                        String[] sFirstLine = reader.readLine().split("/");
                        if (sFirstLine.length > 0) {
                           String sTag = sFirstLine[sFirstLine.length - 1];
                           if (sTag.startsWith("T")) {
                              sTag = sTag.substring(1);
                           } else {
                              sTag = "Mainline";
                           }
                           sName += " (" + sTag + ")";
                        }
                        reader.close();
                        ht.put(sName, file);
                     }
                     catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     }
                  }
               }
            }

            if (ht.size() > 0) {
               String[] array = ht.keySet().toArray(new String[0]);
               Arrays.sort(array);
               String select = Util.select("Please select the arctic project :", array);
               if (select != null) {
                  IFile file = ht.get(select);
                  if (file != null) {
                     try {
                        Execute.executeBatch(new File(file.getRawLocation().toOSString()),
                              "Update H:", false);
                        IProject project = file.getProject();
                        IFolder providerdataJar = project.getFolder("/lib/providerdataJar");
                        if (!providerdataJar.exists()) {
                           providerdataJar = project.getFolder("/lib");
                        }
                        if (providerdataJar.exists()) {
                           providerdataJar.refreshLocal(IResource.DEPTH_INFINITE, null);
                        }
                     }
                     catch (Exception ex) {
                        Util.showException(ex);
                     }
                  }
               }
            } else {
               Util.showException("No 'UpdateFromH.bat' files found in your projects");
            }

         }
      }.start();
      return null;
   }
}
