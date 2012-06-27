package jd.ide.intellij.config;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Configuration component for Java Decompiler.
 * <p/>
 * Holds the configuration of the plugin application wise.
 */
@State(
        name = JDPluginConfigurationPane.COMPONENT_NAME,
        storages = {@Storage(id = "other", file = "$APP_CONFIG$/java.decompiler.xml")}
)
public class JDPluginComponent implements ApplicationComponent, Configurable, PersistentStateComponent<Element> {

    public static final String SHOW_METADATA_ATTRIBUTE = "displayMetadata";
    public static final String SHOW_LINE_NUMBERS_ATTRIBUTE = "displayLineNumbers";
    public static final String JD_CONFIGURATION_CONFIG_ELEMENT = "jd-configuration";
    public static final String JD_INTELLIJ_ID = "jd-intellij";

    private JDPluginConfigurationPane configPane;
    private boolean showLineNumbersEnabled;
    private boolean showMetadataEnabled;

    @Override
    public void initComponent() {
    } // nop

    @Override
    public void disposeComponent() {
    } // nop

    @NotNull
    @Override
    public String getComponentName() {
        return "Java Decompiler plugin";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Java Decompiler";
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("main/resources/icons/jd_64.png");
    }

    @Override
    public String getHelpTopic() {
        return null;
    } // nop

    @Override
    public Element getState() {
        Element jdConfiguration = new Element(JD_CONFIGURATION_CONFIG_ELEMENT);
        jdConfiguration.setAttribute(SHOW_LINE_NUMBERS_ATTRIBUTE, String.valueOf(showLineNumbersEnabled));
        jdConfiguration.setAttribute(SHOW_METADATA_ATTRIBUTE, String.valueOf(showMetadataEnabled));
        return jdConfiguration;
    }

    @Override
    public void loadState(Element jdConfiguration) {
        String showLineNumbersStr = jdConfiguration.getAttributeValue(SHOW_LINE_NUMBERS_ATTRIBUTE);
        if (StringUtils.isNotBlank(showLineNumbersStr)) {
            showLineNumbersEnabled = Boolean.valueOf(showLineNumbersStr);
        }
        String showMetadataStr = jdConfiguration.getAttributeValue(SHOW_METADATA_ATTRIBUTE);
        if (StringUtils.isNotBlank(showMetadataStr)) {
            showMetadataEnabled = Boolean.valueOf(showMetadataStr);
        }
    }

    @Override
    public JComponent createComponent() {
        if (configPane == null) {
            configPane = new JDPluginConfigurationPane();
        }
        return configPane.getRootPane();
    }

    @Override
    public boolean isModified() {
        return configPane != null && configPane.isModified(this);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (configPane != null) {
            configPane.storeDataTo(this);
        }
    }

    @Override
    public void reset() {
        if (configPane != null) {
            configPane.readDataFrom(this);
        }
    }

    @Override
    public void disposeUIResources() {
        configPane = null;
    }

    public boolean isShowLineNumbersEnabled() {
        return showLineNumbersEnabled;
    }

    public void setShowLineNumbersEnabled(boolean showLineNumbersEnabled) {
        this.showLineNumbersEnabled = showLineNumbersEnabled;
    }

    public boolean isShowMetadataEnabled() {
        return showMetadataEnabled;
    }

    public void setShowMetadataEnabled(boolean showMetadataEnabled) {
        this.showMetadataEnabled = showMetadataEnabled;
    }

}
