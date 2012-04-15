package jd.ide.intellij.config;

import javax.swing.*;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginConfigurationPane {
    protected static final String COMPONENT_NAME = "JDPluginConfiguration";
    private JCheckBox showLineNumbersCheckBox;
    private JCheckBox showMetadataCheckBox;
    private JPanel contentPane;
    private JTextPane usingJDCoreTextPane;

    public void storeDataTo(JDPluginComponent jdPluginComponent) {
        jdPluginComponent.setShowLineNumbersEnabled(showLineNumbersCheckBox.isSelected());
        jdPluginComponent.setShowMetadataEnabled(showMetadataCheckBox.isSelected());
    }

    public void readDataFrom(JDPluginComponent jdPluginComponent) {
        showLineNumbersCheckBox.setSelected(jdPluginComponent.isShowLineNumbersEnabled());
        showMetadataCheckBox.setSelected(jdPluginComponent.isShowMetadataEnabled());
    }

    public boolean isModified(JDPluginComponent jdPluginComponent) {
        return showLineNumbersCheckBox.isSelected() != jdPluginComponent.isShowLineNumbersEnabled()
                || showMetadataCheckBox.isSelected() != jdPluginComponent.isShowMetadataEnabled();
    }

    public JPanel getRootPane() {
        return contentPane;
    }
}
