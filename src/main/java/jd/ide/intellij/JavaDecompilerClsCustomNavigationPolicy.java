package jd.ide.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.*;
import com.intellij.psi.util.MethodSignatureUtil;

/**
 * Custom navigation policy
 */
public class JavaDecompilerClsCustomNavigationPolicy implements ClsCustomNavigationPolicy {
    private static Logger LOGGER = Logger.getInstance(JavaDecompilerClsCustomNavigationPolicy.class);
    private final JavaClassDecompiledPsiFileProvider javaClassDecompiledPsiFileProvider;

    public JavaDecompilerClsCustomNavigationPolicy() {
        JavaClassDecompiledPsiFileProvider test = ServiceManager.getService(JavaClassDecompiledPsiFileProvider.class);
        javaClassDecompiledPsiFileProvider = new JavaClassDecompiledPsiFileProvider();
    }


    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsClassImpl clsClass) {
        LOGGER.info("ClsClassImpl:'" + clsClass.getName() + "', instance:'" + System.identityHashCode(clsClass) + "'");
        LOGGER.info("Parent of ClsClassImpl " + clsClass.getParent().getClass().getSimpleName() + "'" + clsClass.getParent() + "', instance:'" + System.identityHashCode(clsClass.getParent()) + "'");

//        Project project = clsClass.getProject();
//        PsiElement parent = clsClass.getParent();
//
//        if(parent instanceof PsiJavaFile) {
//            return JavaPsiImplementationHelper.getInstance(project).getClsFileNavigationElement((PsiJavaFile) parent);
//        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsMethodImpl clsMethod) {

        PsiClass sourceClassMirror = findSourceClassMirror(clsMethod);

        if (sourceClassMirror == null) return null; // not found

        for (PsiMethod sourceMethod : sourceClassMirror.findMethodsByName(clsMethod.getName(), false)) {
            if (MethodSignatureUtil.areParametersErasureEqual(clsMethod, sourceMethod)) {
                return sourceMethod.getNavigationElement();  // not working unfortunately
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsFieldImpl clsField) {
        return null;
    }


    @Nullable
    public PsiClass findSourceClassMirror(@NotNull ClsElementImpl clsMember) { // note: in later version IntelliJ introduced a ClsMemberImpl, this ok here as it is only called for method and fields, but it is unsafe for everything else
        // original code, can use IntelliJ decompiled code
//        ((ClsClassImpl)clsMember.getParent()).getSourceMirrorClass();
//        String parentText = ((ClsClassImpl)clsMember.getParent()).getText();
//        PsiElement methodParent = ((ClsClassImpl)clsMember.getParent()).getParent();

        ClsClassImpl memberOwner = (ClsClassImpl) clsMember.getParent();
        PsiFile decompiledPsiFile = javaClassDecompiledPsiFileProvider.getDecompiledPsiFile((PsiJavaFile) memberOwner.getParent());
        PsiElement navigationElement = decompiledPsiFile.getNavigationElement();

        PsiClassOwner fileNavigationElement = (PsiClassOwner) navigationElement.getNavigationElement();
        for (PsiClass aClass : fileNavigationElement.getClasses()) {
            if (memberOwner.getName().equals(aClass.getName())) return aClass;
        }

        return null; // not found
    }
}
