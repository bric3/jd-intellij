package jd.ide.intellij.config;

import javax.swing.*;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginConfigurationPane {
    protected static final String COMPONENT_NAME = "JDPluginConfiguration";
    private JCheckBox displayLineNumbersCheckBox;
    private JCheckBox displayMetadataCheckBox;
    private JPanel contentPane;
    private JTextPane usingJDCoreTextPane;

    public void storeDataTo(JDPluginComponent jdPluginComponent) {
        jdPluginComponent.setDisplayLineNumbersEnabled(displayLineNumbersCheckBox.isSelected());
        jdPluginComponent.setDisplayMetadataEnabled(displayMetadataCheckBox.isSelected());
    }

    public void readDataFrom(JDPluginComponent jdPluginComponent) {
        displayLineNumbersCheckBox.setSelected(jdPluginComponent.isDisplayLineNumbersEnabled());
        displayMetadataCheckBox.setSelected(jdPluginComponent.isDisplayMetadataEnabled());
    }

    public boolean isModified(JDPluginComponent jdPluginComponent) {
        return displayLineNumbersCheckBox.isSelected() != jdPluginComponent.isDisplayLineNumbersEnabled()
                || displayMetadataCheckBox.isSelected() != jdPluginComponent.isDisplayMetadataEnabled();
    }

    public JPanel getRootPane() {
        return contentPane;
    }
}
