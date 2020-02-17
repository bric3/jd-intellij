package jd.ide.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.compiled.ClassFileDecompilers;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.vfs.VirtualFile;

public class JavaLightClassDecompiler extends ClassFileDecompilers.Light {
    private final CachingJavaDecompilerService javaDecompilerService;

    public JavaLightClassDecompiler() {
        this.javaDecompilerService = ServiceManager.getService(CachingJavaDecompilerService.class);
    }

    @Override
    public boolean accepts(@NotNull VirtualFile file) {
        return true;
    }

    @NotNull
    @Override
    public CharSequence getText(@NotNull VirtualFile file) {
        return javaDecompilerService.decompile(file);
    }
}