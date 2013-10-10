package jd.ide.intellij;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import jd.ide.intellij.config.JDPluginComponent;

import java.io.File;

/**
 * Java Decompiler tool, use native libs to achieve decompilation.
 * <p/>
 * <p>Identify the native lib full path through IntelliJ helpers in {@link SystemInfo}.</p>
 */
public class JavaDecompiler {

    public static String JD_LIB_RELATIVE_PATH = "/nativelib/" + osIdentifier() + "/" + architecture() + "/" + libFileName();

    public JavaDecompiler() {
        File pluginPath = pluginPath();

        String devLibPath = pluginPath + "/classes" + JD_LIB_RELATIVE_PATH;
        if (new File(devLibPath).exists()) {
            loadLibrary(pluginPath, devLibPath);
        } else {
            String libPath = pluginPath + JD_LIB_RELATIVE_PATH;
            loadLibrary(pluginPath, libPath);
        }
    }

    private void loadLibrary(File pluginPath, String libPath) {
        try {
            System.load(libPath);
        } catch (Exception e) {
            throw new IllegalStateException("Something got wrong when loading the Java Decompiler native lib, " +
                    "\nlookup path : " + libPath +
                    "\nplugin path : " + pluginPath, e);
        }
    }

    /**
     * Return a File representing the plugin path.
     * <p/>
     * Trusting IDEA to to not fail there with NPE.
     *
     * @return Plugin path.
     */
    private File pluginPath() {
        return PluginManager.getPlugin(PluginId.getId(JDPluginComponent.JD_INTELLIJ_ID)).getPath();
    }

    /**
     * Library filename, depending on the OS identifier.
     *
     * @return lib filename.
     */
    private static String libFileName() {
        if (SystemInfo.isMac) {
            return "libjd-intellij.jnilib";
        } else if (SystemInfo.isWindows) {
            return "jd-intellij.dll";
        } else if (SystemInfo.isLinux) {
            return "libjd-intellij.so";
        }
        throw new IllegalStateException("OS not supported");
    }

    /**
     * Architecture, either 32bit or 64bit.
     *
     * @return x86 or x86_64 for respectively 32bit 64bit architecture.
     */
    private static String architecture() {
        if (SystemInfo.is32Bit) {
            return "x86";
        } else if (SystemInfo.is64Bit) {
            return "x86_64";
        }
        throw new IllegalStateException("Unsupported architecture, only x86 and x86_64 architectures are supported.");
    }

    /**
     * Identify the OS.
     *
     * @return Either macosx, win32, linux
     */
    private static String osIdentifier() {
        if (SystemInfo.isMac) {
            return "macosx";
        } else if (SystemInfo.isWindows) {
            return "win32";
        } else if (SystemInfo.isLinux) {
            return "linux";
        }
        throw new IllegalStateException("Unsupported OS, only windows, linux and mac OSes are supported.");
    }

    /**
     * Actual call to the native lib.
     *
     * @param basePath          Path to the root of the classpath, either a path to a directory or a path to a jar file.
     * @param internalClassName internal name of the class.
     * @return Decompiled class text.
     */
    public native String decompile(String basePath, String internalClassName);
}
