package jd.ide.idea.config;

import javax.swing.*;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginConfiguration {
    protected static final String COMPONENT_NAME = "JDPluginConfiguration";
    private JCheckBox displayLineNumbersCheckBox;
    private JCheckBox displayMetadataCheckBox;
    private JCheckBox enableJavaDecompilerCheckBox;
    private JPanel contentPane;
    private JTextPane usingJDCore0TextPane;

    public void storeDataTo(JDPluginComponent jdPluginComponent) {
        jdPluginComponent.setPluginEnabled(enableJavaDecompilerCheckBox.isSelected());
        jdPluginComponent.setDisplayLineNumbersEnabled(displayLineNumbersCheckBox.isSelected());
        jdPluginComponent.setDisplayMetadataEnabled(displayMetadataCheckBox.isSelected());
    }

    public void readDataFrom(JDPluginComponent jdPluginComponent) {
        enableJavaDecompilerCheckBox.setSelected(jdPluginComponent.isPluginEnabled());
        displayLineNumbersCheckBox.setSelected(jdPluginComponent.isDisplayLineNumbersEnabled());
        displayMetadataCheckBox.setSelected(jdPluginComponent.isDisplayMetadataEnabled());
    }

    public boolean isModified(JDPluginComponent jdPluginComponent) {
        return  enableJavaDecompilerCheckBox.isSelected() != jdPluginComponent.isPluginEnabled()
                || displayLineNumbersCheckBox.isSelected() != jdPluginComponent.isDisplayLineNumbersEnabled()
                || displayMetadataCheckBox.isSelected() != jdPluginComponent.isDisplayMetadataEnabled();
    }

    public JPanel getRootPane() {
        return contentPane;
    }
}
