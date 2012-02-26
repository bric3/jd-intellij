package jd.ide.eclipse;

import com.intellij.openapi.application.ApplicationManager;
import jd.ide.idea.config.JDPluginComponent;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Eclipse artifact, as the native libs only works with those specified classes.
 *
 * <p>Need to access the preference store.</p>
 *
 * @see IPreferenceStore
 * @see JDPluginComponent
 */
public class JavaDecompilerPlugin {
    private static JavaDecompilerPlugin plugin = new JavaDecompilerPlugin();
    private IPreferenceStore iPreferenceStore;

    public JavaDecompilerPlugin() {
        JDPluginComponent jdPluginComponent = ApplicationManager.getApplication().getComponent(JDPluginComponent.class);
        iPreferenceStore = new PreferenceStoreAdapter(jdPluginComponent);
    }

    public static JavaDecompilerPlugin getDefault() {
        return plugin;
    }

    public IPreferenceStore getPreferenceStore() {
        return iPreferenceStore;
    }

    public void savePluginPreferences() {
        // nop
    }

    /**
     * Eclipse artifact, as the native libs only works with those specified classes.
     *
     * <p>
     *     Needed to access preferences. Because of this coupling, this class is created, no care is needed
     * </p>
     */
    public static class PreferenceStoreAdapter implements IPreferenceStore {

        private final JDPluginComponent jdPluginComponent;

        public PreferenceStoreAdapter(JDPluginComponent jdPluginComponent) {
            this.jdPluginComponent = jdPluginComponent;
        }

        /**
         * Read the preferences from the intellij Java Decompiler plugin component.
         *
         * <p>Known preferences to be read :
         * <ul>
         *     <li><i>jd.ide.eclipse.prefs.DisplayLineNumbers</i> read from {@link jd.ide.idea.config.JDPluginComponent#isDisplayLineNumbersEnabled()}</li>
         *     <li><i>jd.ide.eclipse.prefs.DisplayMetadata</i> read from {@link jd.ide.idea.config.JDPluginComponent#isDisplayMetadataEnabled()}</li>
         * </ul>
         * </p>
         *
         * @param preferenceName Preference name
         * @return Value stored in the JDPluginComponent
         * @throws IllegalArgumentException Raised when a asked for a unknown property.
         * @see jd.ide.idea.config.JDPluginComponent
         */
        @Override public boolean getBoolean(String preferenceName) {
            if ("jd.ide.eclipse.prefs.DisplayLineNumbers".equals(preferenceName)) {
                return jdPluginComponent.isDisplayLineNumbersEnabled();
            }
            if ("jd.ide.eclipse.prefs.DisplayMetadata".equals(preferenceName)) {
                return jdPluginComponent.isDisplayMetadataEnabled();
            }
            throw new IllegalArgumentException("Preference is unknown : " + preferenceName);
        }

        /**
         * Called by the native lib to save some unneeded preferences, this call will do nothing.
         *
         * <p>Know preferences to be saved :
         * <ul>
         *     <li>jd.ide.eclipse.prefs.LastClass.baseName</li>
         *     <li>jd.ide.eclipse.prefs.LastClass.qualifiedName</li>
         * </ul>
         * </p>
         *
         * @param preferenceName Preference name
         * @param value Value of the preference
         */
        @Override public void setValue(String preferenceName, String value) {
            // nop
        }
    }
}
