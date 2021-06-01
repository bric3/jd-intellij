package jd.ide.intellij.config;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.Language;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.ui.HyperlinkLabel;
import jd.ide.intellij.CachingJavaDecompilerService;
import jd.ide.intellij.IdeaDecompilerAccess;
import jd.ide.intellij.JavaDecompilerRefreshSupportService;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Configuration Form for Java Decompiler plugin
 */
public class JDPluginSettingsPane {
    private final Project project;
    private JPanel pane;
    private JLabel jdCoreVersionLabel;
    private HyperlinkLabel jd_hyperlinkLabel;
    private JCheckBox showMetadataCheckBox;
    private JFormattedTextField tabSizeTextField;
    private JButton copyFromProjectCodeStyle;
    private JCheckBox useIdeaDecompiler;

    public JDPluginSettingsPane(Project project) {
        this.project = project;
        var itemListener = new SettingChangeRefreshDecompiledFilesListener(this);
        showMetadataCheckBox.addMouseListener(itemListener);
        tabSizeTextField.addPropertyChangeListener("value", itemListener);
        copyFromProjectCodeStyle.addActionListener(e -> {
            final CommonCodeStyleSettings javaStyle = CodeStyle.getSettings(project).getCommonSettings(Language.findLanguageByID("JAVA"));
            //noinspection ConstantConditions
            final int indent_size = javaStyle.getIndentOptions().INDENT_SIZE;
            tabSizeTextField.setValue(indent_size);
        });

        IdeaDecompilerAccess.tryGetIdeaDecompiler()
                            .ifPresentOrElse(
                                    d -> {
                                        useIdeaDecompiler.addMouseListener(itemListener);
                                    }, () -> {
                                        useIdeaDecompiler.setSelected(false);
                                        useIdeaDecompiler.setEnabled(false);
                                    });
    }

    public void storeDataTo(JDPluginSettings jdPluginSettings) {
        jdPluginSettings.setUseIdeaDecompiler(useIdeaDecompiler.isSelected());
        jdPluginSettings.setShowMetadata(showMetadataCheckBox.isSelected());
        final var tabSizeValue = tabSizeTextField.getValue();
        if (Integer.class == tabSizeValue.getClass()) {
            jdPluginSettings.setTabSize((Integer) tabSizeValue);
        } else if (Long.class == tabSizeValue.getClass()) {
            jdPluginSettings.setTabSize(((Long) tabSizeValue).intValue());
        } else {
            throw new IllegalStateException("tab size value should be a number, current value " + tabSizeValue + " (" + tabSizeValue.getClass() + ")");
        }
    }

    public void readDataFrom(JDPluginSettings jdPluginSettings) {
        useIdeaDecompiler.setSelected(jdPluginSettings.isUseIdeaDecompiler());
        showMetadataCheckBox.setSelected(jdPluginSettings.isShowMetadata());
        tabSizeTextField.setValue(jdPluginSettings.getTabSize());
    }

    public boolean isModified(JDPluginSettings jdPluginSettings) {
        return showMetadataCheckBox.isSelected() != jdPluginSettings.isShowMetadata()
               || Objects.equals(tabSizeTextField.getValue(), jdPluginSettings.getTabSize())
                ;
    }

    public JPanel getPane() {
        return pane;
    }

    private void createUIComponents() {
        tabSizeTextField = new JFormattedTextField(new NumberFormatter(NumberFormat.getIntegerInstance(Locale.ROOT)));

        CachingJavaDecompilerService javaDecompilerService =
                ServiceManager.getService(CachingJavaDecompilerService.class);

        jdCoreVersionLabel = new JLabel("JD-Core " + javaDecompilerService.getVersion());
        jd_hyperlinkLabel = createHyperLinkLabelWithURL("https://en.wikipedia.org/wiki/Java_Decompiler");
    }

    private HyperlinkLabel createHyperLinkLabelWithURL(@SuppressWarnings("SameParameterValue") String link) {
        HyperlinkLabel hyperlinkLabel = new HyperlinkLabel();
        hyperlinkLabel.setHyperlinkText(link);
        hyperlinkLabel.setHyperlinkTarget(link);
        return hyperlinkLabel;
    }


    private static class SettingChangeRefreshDecompiledFilesListener implements MouseListener, PropertyChangeListener {
        private final JDPluginSettingsPane jdPluginSettingsPane;

        public SettingChangeRefreshDecompiledFilesListener(JDPluginSettingsPane jdPluginSettingsPane) {
            this.jdPluginSettingsPane = jdPluginSettingsPane;
        }

        private void updateJDComponentConfiguration() {
            jdPluginSettingsPane.storeDataTo(JDPluginSettings.getInstance());
        }

        private void refreshDecompiledFiles() {
            ServiceManager.getService(JavaDecompilerRefreshSupportService.class).refreshDecompiledFiles();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateJDComponentConfiguration();
            refreshDecompiledFiles();
        }


        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!"value".equals(evt.getPropertyName()) || evt.getOldValue() == null) {
                return;
            }

            updateJDComponentConfiguration();
            refreshDecompiledFiles();
        }
    }
}