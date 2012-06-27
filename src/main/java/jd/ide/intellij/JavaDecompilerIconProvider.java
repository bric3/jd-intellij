package jd.ide.intellij;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Provides a custom icon for *.class files.
 */
public class JavaDecompilerIconProvider extends IconProvider {

    private static final String JD_ICON_URL = "/main/resources/icons/jd_16.png";

    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile != null && containingFile.getName().endsWith(".class")) {
            return IconLoader.getIcon(JD_ICON_URL);
        }
        return null;
    }
}
