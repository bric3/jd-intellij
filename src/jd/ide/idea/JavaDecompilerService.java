package jd.ide.idea;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import jd.ide.eclipse.editors.JDSourceMapper;

import java.util.Iterator;

/**
 * Java Decompiler Service
 */
public class JavaDecompilerService {

    private JDSourceMapper javaDecompiler;

    public JavaDecompilerService() {
        javaDecompiler = new JDSourceMapper();
    }

    public String decompile(Project project, VirtualFile virtualFile) {
        // for jars only
        String filePath = virtualFile.getPath();
        VirtualFile classRootForFile = ProjectRootManager.getInstance(project).getFileIndex().getClassRootForFile(virtualFile);

        if (classRootForFile != null) {
            String baseName = classRootForFile.getPresentableUrl();
            String qualifiedName = filePath.substring(filePath.indexOf('!') + 2, filePath.length());
            return javaDecompiler.decompile(baseName, qualifiedName);
        }

        // for other files if possible
        for(DecompilerPathArgs decompilerPathArgs : new DecompilerPathArgsFinder(virtualFile)) {
            String decompiled = javaDecompiler.decompile(
                    decompilerPathArgs.baseName(),
                    decompilerPathArgs.qualifiedPathName()
            );
            if (decompiled != null) {
                return decompiled;
            }
        }

        // fallback
        return ClsFileImpl.decompile(PsiManager.getInstance(project), virtualFile);
    }

    private class DecompilerPathArgsFinder implements Iterable<DecompilerPathArgs> {
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
                    String qualifiedPathName = VfsUtil.getRelativePath(virtualFile, classPathRoot, '/');
                    return new DecompilerPathArgs(classPathRoot.getPresentableUrl(), qualifiedPathName);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("why the heck would you want to do that!");
                }
            };
        }
    }

    private class DecompilerPathArgs {
        private final String baseName;
        private final String qualifiedPathName;

        public DecompilerPathArgs(String baseName, String qualifiedPathName) {
            this.baseName = baseName;
            this.qualifiedPathName = qualifiedPathName;
        }

        public String baseName() {
            return baseName;
        }

        public String qualifiedPathName() {
            return qualifiedPathName;
        }
    }


}
