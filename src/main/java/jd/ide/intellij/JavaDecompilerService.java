package jd.ide.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;

import java.util.regex.Pattern;

/**
 * Java Decompiler Service.
 */
public class JavaDecompilerService {
    private static final Pattern CLASS_DECLARATION = Pattern.compile("(?sm)class\\s*\\{\\s*\\}.*");
    private static Logger LOGGER = Logger.getInstance(JavaDecompilerService.class);

    private JavaDecompiler javaDecompiler;

    public JavaDecompilerService() {
        this(new JavaDecompiler());
    }

    JavaDecompilerService(JavaDecompiler javaDecompiler) {
        this.javaDecompiler = javaDecompiler;
    }

    public CharSequence decompile(VirtualFile file) {
        // For class file in a JAR archive, filePath=absolute/path/to/file.jar!package1/package2/.../file.class
        // For other class file, filePath=absolute/path/to/file.class
        LOGGER.debug("Start decompiling of : ", file);

        try {
            CharSequence jdDecompiled = javaDecompiler.decompile(file);
            if (validContent(jdDecompiled)) {
                return jdDecompiled;
            }
        } catch (Exception ex) {
            LOGGER.info("Failed to decompile " + file, ex);
        }

        // fallback
        // TODO find IJ fernflower decompiler ?
        return ClsFileImpl.decompile(file);
    }

    public String getVersion() {
        return javaDecompiler.getVersion();
    }

    private boolean validContent(CharSequence decompiled) {
        // note when java decompiler encounter an internal error on some very rare occasion, then it can output null
        // strings.
        return decompiled != null && !CLASS_DECLARATION.matcher(decompiled).matches();
    }
}
