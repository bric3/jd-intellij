package jd.ide.intellij;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import jd.commonide.IdeDecompiler;
import jd.commonide.preferences.IdePreferences;
import jd.core.CoreConstants;
import jd.ide.intellij.config.JDPluginComponent;

import java.io.File;

/**
 * Java Decompiler tool, use native libs to achieve decompilation.
 * <p/>
 * <p>Identify the native lib full path through IntelliJ helpers in {@link SystemInfo}.</p>
 */
public class JavaDecompiler {

    /**
     * Return a File representing the plugin path.
     * <p/>
     * Trusting IDEA to not fail there with NPE.
     *
     * @return Plugin path.
     */
    private static File pluginPath() {
        return PluginManager.getPlugin(PluginId.getId(JDPluginComponent.JD_INTELLIJ_ID)).getPath();
    }

    /**
     * Actual call to the native lib.
     *
     * @param basePath          Path to the root of the classpath, either a path to a directory or a path to a jar file.
     * @param internalTypeName  internal name of the type.
     * @return Decompiled class text.
     */
    public String decompile(String basePath, String internalTypeName) {
        // Load preferences
        JDPluginComponent jdPluginComponent = ApplicationManager.getApplication().getComponent(JDPluginComponent.class);

        boolean showDefaultConstructor = jdPluginComponent.isShowDefaultConstructorEnabled();
        boolean realignmentLineNumber = jdPluginComponent.isRealignLineNumbersEnabled();
        boolean showPrefixThis = !jdPluginComponent.isOmitPrefixThisEnabled();
        boolean mergeEmptyLines = false;
        boolean unicodeEscape = jdPluginComponent.isEscapeUnicodeCharactersEnabled();
        boolean showLineNumbers = jdPluginComponent.isShowLineNumbersEnabled();
        boolean showMetadata = jdPluginComponent.isShowMetadataEnabled();

        // Create preferences
        IdePreferences preferences = new IdePreferences(
            showDefaultConstructor, realignmentLineNumber, showPrefixThis,
            mergeEmptyLines, unicodeEscape, showLineNumbers, showMetadata);

        // Decompile
        return IdeDecompiler.decompile(preferences, basePath, internalTypeName);
    }

    /**
     * @return version of JD-Core
     * @since JD-Core 0.7.0
     */
    public String getVersion() {
        return CoreConstants.JD_CORE_VERSION;
    }
}
