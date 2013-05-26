package jd.ide.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.NonCancelableSection;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.psi.ClsFileDecompiledPsiFileProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provider extension that builds a {@link com.intellij.psi.PsiFile} from the JD decompiled text, otherwise IDEA uses
 * the default decompiled text to build the PsiFile which results in wrong text sections to be highlighted and wrong
 * references.
 *
 * @see <a href="http://youtrack.jetbrains.com/issue/IDEA-100029">http://youtrack.jetbrains.com/issue/IDEA-100029</a>
 *
 */
public class JavaClassDecompiledPsiFileProvider implements ClsFileDecompiledPsiFileProvider {
    private final CachingJavaDecompilerService javaDecompilerService;

    public JavaClassDecompiledPsiFileProvider() {
        javaDecompilerService = ServiceManager.getService(CachingJavaDecompilerService.class);
    }



    @Nullable
    @Override
    public PsiFile getDecompiledPsiFile(@NotNull PsiJavaFile clsFile) {
            String decompiledText = javaDecompilerService.decompile(clsFile);

        PsiFile fileFromDecompiledText = PsiFileFactory.getInstance(clsFile.getProject()).createFileFromText(
                clsFile.getName(),
                clsFile.getLanguage(),
                decompiledText,
                false,
                false
        );
        fileFromDecompiledText.putUserData(PsiUtil.FILE_LANGUAGE_LEVEL_KEY, clsFile.getLanguageLevel());


        // code taken from ClsFileImpl#getMirror()
        TreeElement mirrorTreeElement = SourceTreeToPsiMap.psiToTreeNotNull(fileFromDecompiledText);

        if (clsFile instanceof ClsFileImpl) {
                NonCancelableSection section = ProgressIndicatorProvider.getInstance().startNonCancelableSection();
            try {
                ((ClsFileImpl) clsFile).setMirror(mirrorTreeElement);
            }
            finally {
                section.done();
            }
        }


        return fileFromDecompiledText;
    }
}
