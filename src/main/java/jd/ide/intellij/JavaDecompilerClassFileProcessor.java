package jd.ide.intellij;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.ContentBasedClassFileProcessor;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import jd.ide.intellij.config.JDPluginComponent;


@SuppressWarnings("unused") // For compatibility reason
public class JavaDecompilerClassFileProcessor implements ContentBasedClassFileProcessor {

    private CachingJavaDecompilerService javaDecompilerService = null;
    private JDPluginComponent jdPluginComponent = null;

    @Override public boolean isApplicable(Project project, VirtualFile virtualFile) { return false; }
    @Override public SyntaxHighlighter createHighlighter(Project project, VirtualFile vFile) { return null; }
    @Override public String obtainFileText(Project project, VirtualFile virtualFile) { return null; }
    @Override public Language obtainLanguageForFile(VirtualFile virtualFile) { return null; }
}
