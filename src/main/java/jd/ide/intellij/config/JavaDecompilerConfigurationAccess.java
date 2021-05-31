package jd.ide.intellij.config;

import com.intellij.openapi.application.ApplicationManager;

/**
 * Java Decompiler configuration Access.
 *
 * @see JDPluginSettings
 */
public class JavaDecompilerConfigurationAccess {

    private final JDPluginSettings jdPluginSettings;

    public JavaDecompilerConfigurationAccess() {
        jdPluginSettings = ApplicationManager.getApplication().getComponent(JDPluginSettings.class);
    }

    public boolean isShowMetadataEnabled() {
        return jdPluginSettings.isShowMetadata();
    }
}                        
