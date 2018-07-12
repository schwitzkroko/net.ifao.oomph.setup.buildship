package de.hkneissel.oomph.buildshipimport.util;

import de.hkneissel.oomph.buildshipimport.BuildshipImportPackage;
import de.hkneissel.oomph.buildshipimport.BuildshipImportTask;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.oomph.base.ModelElement;
import org.eclipse.oomph.setup.SetupTask;

public class BuildshipImportSwitch<T>
   extends Switch<T>
 {
   protected static BuildshipImportPackage modelPackage;

   public BuildshipImportSwitch()
   {
     if (modelPackage == null)
     {
       modelPackage = BuildshipImportPackage.eINSTANCE;
     }
   }










   @Override
   protected boolean isSwitchFor(EPackage ePackage)
   {
     return ePackage == modelPackage;
   }









   @Override
   protected T doSwitch(int classifierID, EObject theEObject)
   {
     switch (classifierID)
     {

     case 0:
       BuildshipImportTask buildshipImportTask = (BuildshipImportTask)theEObject;
       T result = caseBuildshipImportTask(buildshipImportTask);
       if (result == null) {
         result = caseSetupTask(buildshipImportTask);
      }
       if (result == null) {
         result = caseModelElement(buildshipImportTask);
      }
       if (result == null) {
         result = defaultCase(theEObject);
      }
       return result; }

     return (T)defaultCase(theEObject);
   }













   public T caseBuildshipImportTask(BuildshipImportTask object)
   {
     return null;
   }












   public T caseModelElement(ModelElement object)
   {
     return null;
   }












   public T caseSetupTask(SetupTask object)
   {
     return null;
   }













   @Override
   public T defaultCase(EObject object)
   {
     return null;
   }
 }


/* Location:              C:\Users\fliedner\Desktop\buildship-import-4-oomph-8.0.0\plugins\de.hkneissel.oomph.buildshipimport_0.7.0.201604211538.jar!\de\hkneissel\oomph\buildshipimport\util\BuildshipImportSwitch.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
