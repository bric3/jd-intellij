package jd.ide.intellij;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.compiled.ClsCustomNavigationPolicy;
import com.intellij.psi.impl.compiled.ClsFieldImpl;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom navigation policy
 */
public class JavaDecompilerClsCustomNavigationPolicy implements ClsCustomNavigationPolicy {
    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsClassImpl clsClass) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsMethodImpl clsMethod) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsFieldImpl clsField) {
        return null;
    }
}
