package jd.ide.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import jd.ide.intellij.config.JDPluginSettings;
import jd.ide.intellij.decompiler.JavaDecompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

/**
 * Java Decompiler Service.
 */
public class JavaDecompilerService {
    private static final Pattern CLASS_DECLARATION = Pattern.compile("(?sm)class\\s*\\{\\s*}.*");
    private static final Logger LOGGER = Logger.getInstance(JavaDecompilerService.class);

    private final JavaDecompiler javaDecompiler;

    public JavaDecompilerService() {
        this(new JavaDecompiler());
    }

    JavaDecompilerService(JavaDecompiler javaDecompiler) {
        this.javaDecompiler = javaDecompiler;
    }

    public CharSequence decompile(VirtualFile file) {
        // For class file in a JAR archive, filePath=absolute/path/to/file.jar!package1/package2/.../file.class
        // For other class file, filePath=absolute/path/to/file.class
        LOGGER.debug("[JD] Start decompiling of : ", file);
        if(JDPluginSettings.getInstance().isUseIdeaDecompiler()) {
            return IdeaDecompilerAccess.tryGetIdeaDecompiler()
                                       .filter(decompiler -> decompiler.accepts(file))
                                       .map(decompiler -> decompiler.getText(file))
                                       .orElseThrow(() -> new IllegalStateException("Decompilation issue when using Idea Decompiler"));
        }

        try {
            var jdDecompiled = javaDecompiler.decompile(file);

//            dumpDecompiledContent_for_InvalidMirrorException(file, jdDecompiled);

            if (validContent(jdDecompiled)) {
                return jdDecompiled;
            }
        } catch (Exception ex) {
            LOGGER.warn("[JD] Failed to decompile " + file, ex);
        }

        // fallback
        return ClsFileImpl.decompile(file);
    }

    @SuppressWarnings("unused")
    private void dumpDecompiledContent_for_InvalidMirrorException(VirtualFile file, CharSequence jdDecompiled) throws IOException {
        Files.writeString(Path.of("/Users/brice/opensource/jd-intellij", file.getNameWithoutExtension() + ".jd.java"),
                          jdDecompiled,
                          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(Path.of("/Users/brice/opensource/jd-intellij", file.getNameWithoutExtension() + ".cls.java"),
                          ClsFileImpl.decompile(file),
                          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String getVersion() {
        return JavaDecompiler.readVersion();
    }

    private boolean validContent(CharSequence decompiled) {
        // note when java decompiler encounter an internal error on some very rare occasion, then it can output null
        // strings.
        return decompiled != null && !CLASS_DECLARATION.matcher(decompiled).matches();
    }
}