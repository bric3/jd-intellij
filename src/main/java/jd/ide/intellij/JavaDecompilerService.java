package jd.ide.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.util.io.URLUtil;

import java.util.Iterator;

/**
 * Java Decompiler Service.
 */
public class JavaDecompilerService {
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

        Pair<String, String> jarPaths = URLUtil.splitJarUrl("file:" + file.getPath());
        if (jarPaths != null) {
            String decompiled = javaDecompiler.decompile(jarPaths.first, jarPaths.second);

            if (validContent(decompiled)) {
                // All text strings passed to document modification methods (setText, insertString, replaceString) must
                // use only \n as line separators.
                //   http://confluence.jetbrains.net/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
                return StringUtil.convertLineSeparators(decompiled);
            }
        } else {
            // discover root and path for other files if possible
            for (DecompilerPathArgs decompilerPathArgs : new DecompilerPathArgsFinder(file)) {
                String decompiled = javaDecompiler.decompile(
                        decompilerPathArgs.getBasePath(),
                        decompilerPathArgs.getInternalClassName()
                );
                if (validContent(decompiled)) {
                    // Idem
                    return StringUtil.convertLineSeparators(decompiled);
                }
            }
        }

        // fallback
        return ClsFileImpl.decompile(file);
    }

    public String getVersion() {
        return javaDecompiler.getVersion();
    }

    private boolean validContent(String decompiled) {
        // note when java decompiler encounter an internal error on some very rare occasion, then it can output null
        // strings.
        return decompiled != null && !decompiled.matches("(?sm)class\\s*\\{\\s*\\}.*");
    }

    /**
     * Simple utility class to iterate on possible path arguments
     * for class files not in standard location inside the project.
     * <p/>
     * Produces {@link DecompilerPathArgs} types.
     */
    private static class DecompilerPathArgsFinder implements Iterable<DecompilerPathArgs> {
        private final VirtualFile virtualFile;

        public DecompilerPathArgsFinder(VirtualFile virtualFile) {
            this.virtualFile = virtualFile;
        }

        @Override
        public Iterator<DecompilerPathArgs> iterator() {
            return new Iterator<DecompilerPathArgs>() {
                private VirtualFile classPathRoot = virtualFile.getParent();

                @Override
                public boolean hasNext() {
                    return classPathRootIsNotRootDirectory();
                }

                private boolean classPathRootIsNotRootDirectory() {
                    return classPathRoot != null
                            && classPathRoot.getParent() != null;
                }

                @Override
                public DecompilerPathArgs next() {
                    classPathRoot = classPathRoot.getParent();
                    assert classPathRoot != null : "classPathRoot cannot be null";

                    String internalClassName = VfsUtil.getRelativePath(virtualFile, classPathRoot, '/');
                    return new DecompilerPathArgs(classPathRoot.getPresentableUrl(), internalClassName);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("why the heck would you want to do that!");
                }
            };
        }
    }

    /**
     * Java Decompiler path arguments.
     * <p/>
     * Composed of the <em>base name</em> and the <em>qualified name</em>.
     */
    private static class DecompilerPathArgs {
        private final String basePath;
        private final String internalClassName;

        public DecompilerPathArgs(String basePath, String internalClassName) {
            this.basePath = basePath;
            this.internalClassName = internalClassName;
        }

        public String getBasePath() {
            return basePath;
        }

        public String getInternalClassName() {
            return internalClassName;
        }
    }
}
