package jd.ide.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.NonCancelableSection;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Key;
import com.intellij.psi.ClsFileDecompiledPsiFileProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.util.PsiUtil;

/**
 * Provider extension that builds a {@link com.intellij.psi.PsiFile} from the JD decompiled text, otherwise IDEA uses
 * the default decompiled text to build the PsiFile which results in wrong text sections to be highlighted and wrong
 * references.
 *
 * @see <a href="http://youtrack.jetbrains.com/issue/IDEA-100029">http://youtrack.jetbrains.com/issue/IDEA-100029</a>
 */
public class JavaClassDecompiledPsiFileProvider implements ClsFileDecompiledPsiFileProvider {
    private static Logger LOGGER = Logger.getInstance(JavaClassDecompiledPsiFileProvider.class);


    private final CachingJavaDecompilerService javaDecompilerService;

    public JavaClassDecompiledPsiFileProvider() {
        javaDecompilerService = ServiceManager.getService(CachingJavaDecompilerService.class);
    }


    @Nullable
    @Override
    public PsiFile getDecompiledPsiFile(@NotNull PsiJavaFile clsFile) {
        String decompiledText = javaDecompilerService.decompile(clsFile);

        // investigate caching, called a lot
        PsiFile fileFromDecompiledText = PsiFileFactory.getInstance(clsFile.getProject()).createFileFromText(
                clsFile.getName(),
                clsFile.getLanguage(),
                decompiledText,
                false,
                false
        );
        fileFromDecompiledText.putUserData(PsiUtil.FILE_LANGUAGE_LEVEL_KEY, clsFile.getLanguageLevel());

        LOGGER.info("Decompiled PsiFile of PsiJavaFile:'" + clsFile.getName() + "', instance:'" + System.identityHashCode(clsFile) + "'");
        LOGGER.info("Decompiled PsiFile:'" + fileFromDecompiledText.getName() + "', instance:'" + System.identityHashCode(fileFromDecompiledText) + "'");

        // code taken from ClsFileImpl#getMirror()
        {
            TreeElement mirrorTreeElement = SourceTreeToPsiMap.psiToTreeNotNull(fileFromDecompiledText);

            if (clsFile instanceof ClsFileImpl) {
                NonCancelableSection section = ProgressIndicatorProvider.getInstance().startNonCancelableSection();
                try {
                    // TODO this code should be removed after 11.1 release. It is left just in case.
                    // Document is not actually used, maybe it is stored in mirror just to avoid garbage collecting
                    Document document = FileDocumentManager.getInstance().getDocument(clsFile.getVirtualFile());
                    mirrorTreeElement.putUserData(Key.create("DOCUMENT_IN_MIRROR_KEY"), document);

                    ((ClsFileImpl) clsFile).setMirror(mirrorTreeElement);
                } finally {
                    section.done();
                }
            }
        }

        return fileFromDecompiledText;
    }
}
