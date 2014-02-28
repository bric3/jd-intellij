package jd.ide.intellij.config;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.HyperlinkLabel;
import jd.ide.intellij.CachingJavaDecompilerService;
import jd.ide.intellij.JavaDecompilerRefreshSupportService;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginConfigurationPane {
    protected static final String COMPONENT_NAME = "JDPluginConfiguration";

    private JCheckBox escapeUnicodeCharactersCheckBox;
    private JCheckBox omitPrefixThisCheckBox;
    private JCheckBox realignLineNumbersCheckBox;
    private JCheckBox showLineNumbersCheckBox;
    private JCheckBox showMetadataCheckBox;
    private JPanel contentPane;
    private JLabel jdCoreVersionLabel;
    private HyperlinkLabel jd_hyperlinkLabel;

    public JDPluginConfigurationPane() {
        MouseListener itemListener = new OnMouseReleaseRefreshDecompiledFilesListener(this);
        escapeUnicodeCharactersCheckBox.addMouseListener(itemListener);
        omitPrefixThisCheckBox.addMouseListener(itemListener);
        realignLineNumbersCheckBox.addMouseListener(itemListener);
        showLineNumbersCheckBox.addMouseListener(itemListener);
        showMetadataCheckBox.addMouseListener(itemListener);
    }

    public void storeDataTo(JDPluginComponent jdPluginComponent) {
        jdPluginComponent.setEscapeUnicodeCharactersEnabled(escapeUnicodeCharactersCheckBox.isSelected());
        jdPluginComponent.setOmitPrefixThisEnabled(omitPrefixThisCheckBox.isSelected());
        jdPluginComponent.setRealignLineNumbersEnabled(realignLineNumbersCheckBox.isSelected());
        jdPluginComponent.setShowLineNumbersEnabled(showLineNumbersCheckBox.isSelected());
        jdPluginComponent.setShowMetadataEnabled(showMetadataCheckBox.isSelected());
    }

    public void readDataFrom(JDPluginComponent jdPluginComponent) {
        escapeUnicodeCharactersCheckBox.setSelected(jdPluginComponent.isEscapeUnicodeCharactersEnabled());
        omitPrefixThisCheckBox.setSelected(jdPluginComponent.isOmitPrefixThisEnabled());
        realignLineNumbersCheckBox.setSelected(jdPluginComponent.isRealignLineNumbersEnabled());
        showLineNumbersCheckBox.setSelected(jdPluginComponent.isShowLineNumbersEnabled());
        showMetadataCheckBox.setSelected(jdPluginComponent.isShowMetadataEnabled());
    }

    public boolean isModified(JDPluginComponent jdPluginComponent) {
        return escapeUnicodeCharactersCheckBox.isSelected() != jdPluginComponent.isEscapeUnicodeCharactersEnabled()
            || omitPrefixThisCheckBox.isSelected() != jdPluginComponent.isOmitPrefixThisEnabled()
            || realignLineNumbersCheckBox.isSelected() != jdPluginComponent.isRealignLineNumbersEnabled()
            || showLineNumbersCheckBox.isSelected() != jdPluginComponent.isShowLineNumbersEnabled()
                || showMetadataCheckBox.isSelected() != jdPluginComponent.isShowMetadataEnabled();
    }

    public JPanel getRootPane() {
        return contentPane;
    }

    private void createUIComponents() {
        CachingJavaDecompilerService javaDecompilerService =
                ServiceManager.getService(CachingJavaDecompilerService.class);


        jdCoreVersionLabel = new JLabel("JD-Core " + javaDecompilerService.getVersion());
        jd_hyperlinkLabel = createHyperLinkLabelWithURL("http://en.wikipedia.org/wiki/Java_Decompiler");
    }

    private HyperlinkLabel createHyperLinkLabelWithURL(String link) {
        HyperlinkLabel hyperlinkLabel = new HyperlinkLabel();
        hyperlinkLabel.setHyperlinkText(link);
        hyperlinkLabel.setHyperlinkTarget(link);
        return hyperlinkLabel;
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
