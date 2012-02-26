package jd.ide.idea.config;

import com.intellij.openapi.application.ApplicationManager;

/**
 * Java Decompiler configuration Access.
 *
 * @see JDPluginComponent
 */
public class JavaDecompilerConfigurationAccess {

    private final JDPluginComponent jdPluginComponent;

    public JavaDecompilerConfigurationAccess() {
        jdPluginComponent = ApplicationManager.getApplication().getComponent(JDPluginComponent.class);
    }

    public boolean isDisplayLineNumbersEnabled() {
        return jdPluginComponent.isDisplayLineNumbersEnabled();
    }

    public boolean isDisplayMetadataEnabled() {
        return jdPluginComponent.isDisplayMetadataEnabled();
    }

}
