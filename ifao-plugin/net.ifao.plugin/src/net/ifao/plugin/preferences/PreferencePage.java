package net.ifao.plugin.preferences;


import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import ifaoplugin.Activator;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage
   extends FieldEditorPreferencePage
   implements IWorkbenchPreferencePage
{

   public PreferencePage()
   {
      super(GRID);
      setPreferenceStore(Activator.getDefault().getPreferenceStore());
      setDescription("Setup of Ifao Plugin properties");
   }

   /**
    * Creates the field editors. Field editors are abstractions of
    * the common GUI blocks needed to manipulate various types
    * of preferences. Each field editor knows how to save and
    * restore itself.
    */
   @Override
   public void createFieldEditors()
   {
      addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_ARCTIC, "&Arctic Directory:",
            getFieldEditorParent()));
      addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_ARCTICTOOLS,
            "&Arctic Tools Directory:", getFieldEditorParent()));
      addField(new StringFieldEditor(PreferenceConstants.P_PATH_ADDITIONAL_CLASSES,
            "Additional arctic &Classpath:", getFieldEditorParent()));
      addField(new BooleanFieldEditor(PreferenceConstants.P_FORMATTER,
            "&Format header, by using the headernames", getFieldEditorParent()));
      //      addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN,
      //            "&An example of a boolean preference", getFieldEditorParent()));
      //
      //      addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE,
      //            "An example of a multiple-choice preference", 1, new String[][]{
      //                  { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
      //      addField(new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:",
      //            getFieldEditorParent()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   @Override
   public void init(IWorkbench workbench)
   {
      // make nothing
   }

}
