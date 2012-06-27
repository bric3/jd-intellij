package jd.ide.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.compiled.ClsFileImpl;

import java.util.Iterator;

/**
 * Java Decompiler Service.
 */
public class JavaDecompilerService {

    private JavaDecompiler javaDecompiler;

    public JavaDecompilerService() {
        javaDecompiler = new JavaDecompiler();
    }

    public String decompile(Project project, VirtualFile virtualFile) {
        // for jars only
        String filePath = virtualFile.getPath();
        VirtualFile classRootForFile =
                ProjectRootManager.getInstance(project).getFileIndex().getClassRootForFile(virtualFile);

        if (classRootForFile != null) {
            String basePath = classRootForFile.getPresentableUrl();
            String internalClassName = filePath.substring(filePath.indexOf('!') + 2, filePath.length());
            String decompiled = javaDecompiler.decompile(basePath, internalClassName);
            if (validContent(decompiled)) {
                // All text strings passed to document modification methods (setText, insertString, replaceString) must
                // use only \n as line separators.
                //   http://confluence.jetbrains.net/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
                return StringUtil.convertLineSeparators(decompiled);
            }
        }

        // for other files if possible
        for (DecompilerPathArgs decompilerPathArgs : new DecompilerPathArgsFinder(virtualFile)) {
            String decompiled = javaDecompiler.decompile(
                    decompilerPathArgs.getBasePath(),
                    decompilerPathArgs.getInternalClassName()
            );
            if (validContent(decompiled)) {
                // Idem
                return StringUtil.convertLineSeparators(decompiled);
            }
        }

        // fallback
        return ClsFileImpl.decompile(PsiManager.getInstance(project), virtualFile);
    }

    private boolean validContent(String decompiled) {
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
                    return classPathRoot != null;
                }

                @Override
                public DecompilerPathArgs next() {
                    classPathRoot = classPathRoot.getParent();
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
