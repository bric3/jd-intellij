package jd.ide.intellij;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.compiled.ClassFileDecompilers;

import java.util.Optional;

public class IdeaDecompilerAccess {
    public static final ExtensionPointName<ClassFileDecompilers.Decompiler> EP_NAME = new ExtensionPointName<>("com.intellij.psi.classFileDecompiler");

    public static Optional<ClassFileDecompilers.Light> tryGetIdeaDecompiler() {
        return EP_NAME.getExtensionList().stream()
                      .filter(decompiler -> "IdeaDecompiler".equals(decompiler.getClass().getSimpleName())
                                            && decompiler instanceof ClassFileDecompilers.Light)
                      .map(ClassFileDecompilers.Light.class::cast)
                      .findFirst();

    }

}