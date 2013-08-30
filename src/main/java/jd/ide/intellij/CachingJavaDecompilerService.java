package jd.ide.intellij;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static jd.ide.intellij.JavaDecompilerRefreshSupportService.JavaDecompilerRefreshListener;

/**
 * Caching decorator for the decompilation service.
 *
 * <p>
 *     Note this service provides two method for decompilation that are consumed by two different extension points :
 *     <ul>
 *         <li>{@link JavaDecompilerClassFileProcessor} which gives the actual decompiled text to be displayed.</li>
 *         <li>{@link JavaClassDecompiledPsiFileProvider} which gives IDEA the PsiFile needed to build the AST, for
 *             references, etc.</li>
 *     </ul>
 * </p>
 *
 * <p>
 *     As both these services need to use the same decompiled text, the decompilation is cached in this class. Aside of
 *     that the decompiled text can change in regard of the activated options of the plugin. For this reason
 *     the cache need to listen to refresh requests.
 * </p>
 *
 * @see #decompile(Project, VirtualFile)
 * @see #decompile(PsiJavaFile)
 */
public class CachingJavaDecompilerService {
    private static Logger LOGGER = Logger.getInstance(CachingJavaDecompilerService.class);

    private final JavaDecompilerService javaDecompilerService;
    private final LoadingCache<DecompiledFileKey, String> decompiledCache;


    public CachingJavaDecompilerService() {
        javaDecompilerService = new JavaDecompilerService();
        decompiledCache = makeDecompiledCache();
        ServiceManager.getService(JavaDecompilerRefreshSupportService.class).registerRefreshListener(
                new JavaDecompilerRefreshListener() {
                    public void onRefreshDecompiledFiles() {
                        decompiledCache.invalidateAll();
                    }
                }
        );
    }


    @NotNull
    public String decompile(Project project, VirtualFile virtualFile) throws RuntimeException{
        return accessToDecompiledText(project, virtualFile);
    }


    @NotNull
    public String decompile(PsiJavaFile clsFile) throws RuntimeException{
        return decompile(clsFile.getProject(), clsFile.getVirtualFile());
    }

    public String getVersion() {
        return javaDecompilerService.getVersion();
    }


    private String accessToDecompiledText(Project project, VirtualFile virtualFile) {
        try {
            return decompiledCache.get(new DecompiledFileKey(project, virtualFile));
        } catch (ExecutionException e) {
            Throwables.propagate(e);
            return null;
        }
    }


    private LoadingCache<DecompiledFileKey, String> makeDecompiledCache() {
        return CacheBuilder.newBuilder()
//                .concurrencyLevel(4)
                .expireAfterAccess(20, TimeUnit.MINUTES)
                .build(new CacheLoader<DecompiledFileKey, String>() {
                    @Override
                    public String load(DecompiledFileKey decompiledFileKey) throws Exception {
                        ServiceManager.getService(JavaDecompilerRefreshSupportService.class).markDecompiled(decompiledFileKey.virtualFile);
                        return javaDecompilerService.decompile(decompiledFileKey.project, decompiledFileKey.virtualFile);
                    }
                });
    }


    private static class DecompiledFileKey {
        Project project;
        VirtualFile virtualFile;

        private DecompiledFileKey(Project project, VirtualFile virtualFile) {
            this.project = project;
            this.virtualFile = virtualFile;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(project, virtualFile);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final DecompiledFileKey other = (DecompiledFileKey) obj;
            return Objects.equal(this.project, other.project) && Objects.equal(this.virtualFile, other.virtualFile);
        }
    }
}
