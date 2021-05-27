package jd.ide.intellij.config;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JDPluginConfigurable implements SearchableConfigurable {
    private JDPluginSettingsPane settingsPane;
    private final Project project;

    public JDPluginConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull
    @NonNls
    String getId() {
        return "jd.intellij.settings"; // see plugin.xml
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Java Decompiler";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return settingsPane().getPane();
    }

    @Override
    public boolean isModified() {
        return settingsPane().isModified(JDPluginSettings.getInstance());
    }

    @Override
    public void apply() {
        settingsPane().storeDataTo(JDPluginSettings.getInstance());
    }

    @Override
    public void reset() {
        settingsPane().readDataFrom(JDPluginSettings.getInstance());
    }

    @Override
    public void disposeUIResources() {
        settingsPane = null;
    }

    @NotNull
    public JDPluginSettingsPane settingsPane() {
        if (settingsPane == null) {
            settingsPane = new JDPluginSettingsPane(project);
        }
        return settingsPane;
    }
}
