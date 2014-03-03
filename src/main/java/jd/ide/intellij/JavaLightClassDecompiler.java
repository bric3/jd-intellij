package jd.ide.intellij;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.compiled.ClassFileDecompilers;

public class JavaLightClassDecompiler extends ClassFileDecompilers.Light {
    private final CachingJavaDecompilerService javaDecompilerService;

    public JavaLightClassDecompiler(CachingJavaDecompilerService javaDecompilerService) {
        this.javaDecompilerService = javaDecompilerService;
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