package jd.ide.intellij;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.ClsFileDecompiledPsiFileProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * Provider extension that builds a PsiFile from the JD decompiled text, other wise IDEA uses
 * the default decompiled text to build the PsiFile which results in wrong references.
 *
 * @see <a href="http://youtrack.jetbrains.com/issue/IDEA-100029">http://youtrack.jetbrains.com/issue/IDEA-100029</a>
 *
 */
public class JavaClassDecompiledPsiFileProvider implements ClsFileDecompiledPsiFileProvider {
    private final JavaDecompilerService javaDecompilerService;
    private final Cache<PsiJavaFile, String> decompiledCache;


    public JavaClassDecompiledPsiFileProvider() {
        javaDecompilerService = ServiceManager.getService(JavaDecompilerService.class);


        decompiledCache = CacheBuilder.newBuilder().build(new CacheLoader<PsiJavaFile, String>() {
            @Override
            public String load(PsiJavaFile clsFile) throws Exception {
                return javaDecompilerService.decompile(clsFile.getProject(), clsFile.getVirtualFile());
            }
        });
    }



    @Nullable
    @Override
    public PsiFile getDecompiledPsiFile(@NotNull PsiJavaFile clsFile) {
        String decompiledText = accessToDecompiledText(clsFile);

        PsiFile fileFromDecompiledText = PsiFileFactory.getInstance(clsFile.getProject()).createFileFromText(
                clsFile.getName(),
                clsFile.getLanguage(),
                decompiledText,
                false,
                true
        );
        return fileFromDecompiledText;
    }

    private String accessToDecompiledText(PsiJavaFile clsFile) {
        try {
            return decompiledCache.get(clsFile);
        } catch (ExecutionException e) {
            Throwables.propagate(e);
            return null;
        }
    }
    // com.intellij.psi.impl.PsiFileFactoryImpl#createFileFromText




}
