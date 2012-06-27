package jd.ide.intellij.config;

import com.intellij.openapi.application.ApplicationManager;
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
    private JCheckBox showLineNumbersCheckBox;
    private JCheckBox showMetadataCheckBox;
    private JPanel contentPane;
    private JTextPane usingJDCoreTextPane;

    public JDPluginConfigurationPane() {
        MouseListener itemListener = new OnMouseReleaseRefreshDecompiledFilesListener(this);
        showLineNumbersCheckBox.addMouseListener(itemListener);
        showMetadataCheckBox.addMouseListener(itemListener);
    }

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

    private static class OnMouseReleaseRefreshDecompiledFilesListener implements MouseListener {

        private final JDPluginConfigurationPane jdPluginConfigurationPane;

        public OnMouseReleaseRefreshDecompiledFilesListener(JDPluginConfigurationPane jdPluginConfigurationPane) {
            this.jdPluginConfigurationPane = jdPluginConfigurationPane;
        }

        @Override public void mouseReleased(MouseEvent e) {
            updateJDComponentConfiguration();
            refreshDecompiledFiles();
        }

        private void updateJDComponentConfiguration() {
            JDPluginComponent jdPluginComponent = ApplicationManager.getApplication().getComponent(JDPluginComponent.class);
            jdPluginConfigurationPane.storeDataTo(jdPluginComponent);
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
