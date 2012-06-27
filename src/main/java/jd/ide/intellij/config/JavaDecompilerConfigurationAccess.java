package jd.ide.intellij.config;

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

    public boolean isShowLineNumbersEnabled() {
        return jdPluginComponent.isShowLineNumbersEnabled();
    }

    public boolean isShowMetadataEnabled() {
        return jdPluginComponent.isShowMetadataEnabled();
    }

}
