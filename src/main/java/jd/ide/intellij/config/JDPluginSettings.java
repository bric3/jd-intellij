package jd.ide.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration component for Java Decompiler.
 * <p/>
 * Holds the configuration of the plugin application wise.
 *
 * @see <a href="https://plugins.jetbrains.com/docs/intellij/settings.html">Settings documentation</a>
 * @see <a href="https://plugins.jetbrains.com/docs/intellij/settings-tutorial.html#creating-the-appsettingstate-implementation">Settings tutorila</a>
 */
@State(
        name = "jd.intellij.settings",
        storages = {@Storage(value = "java-decompiler.xml")}
)
public class JDPluginSettings implements PersistentStateComponent<JDPluginSettings> {
    private boolean showMetadata = true;
    private int tabSize = 4;
    private boolean useIdeaDecompiler = false;

    @NotNull
    public static JDPluginSettings getInstance() {
        return ServiceManager.getService(JDPluginSettings.class);
    }

    @Override
    public JDPluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JDPluginSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setShowMetadata(boolean showMetadata) {
        this.showMetadata = showMetadata;
    }
    public boolean isShowMetadata() {
        return showMetadata;
    }

    public int getTabSize() {
        return tabSize;
    }
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public void setUseIdeaDecompiler(boolean useIdeaDecompiler) {
        this.useIdeaDecompiler = useIdeaDecompiler;
    }
    public boolean isUseIdeaDecompiler() {
        return useIdeaDecompiler;
    }

}