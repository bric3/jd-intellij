package jd.ide.intellij.config;

import com.intellij.openapi.components.ServiceManager;
import jd.ide.intellij.JavaDecompilerRefreshSupportService;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginConfigurationPane {
    protected static final String COMPONENT_NAME = "JDPluginConfiguration";
    private JCheckBox displayLineNumbersCheckBox;
    private JCheckBox displayMetadataCheckBox;
    private JPanel contentPane;
    private JTextPane usingJDCoreTextPane;

    public JDPluginConfigurationPane() {
        MouseListener itemListener = new OnMouseReleaseRefreshDecompiledFilesListener();
        displayLineNumbersCheckBox.addMouseListener(itemListener);
        displayMetadataCheckBox.addMouseListener(itemListener);
    }

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

    private static class OnMouseReleaseRefreshDecompiledFilesListener implements MouseListener {

        @Override public void mouseReleased(MouseEvent e) {
            refreshDecompiledFiles();
        }

        private void refreshDecompiledFiles() {
            ServiceManager.getService(JavaDecompilerRefreshSupportService.class).refreshDecompiledFiles();
        }

        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mousePressed(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }
    }
}
