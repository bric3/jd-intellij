package jd.ide.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.function.BiFunction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaDecompilerServiceTest extends LightJavaCodeInsightFixtureTestCase4 {

    @Test
    public void very_simple_decompilation_using_the_caching_service_with_local_fs_class() {
        VirtualFile file = TestUtils.virtualFileOf(TestUtils.localFileSystemClassFileUrl(JavaDecompiler.class));

        CharSequence text = ServiceManager.getService(CachingJavaDecompilerService.class).decompile(file);
        assertNotNull(text);

        String decompiled = text.toString();
        System.out.println(decompiled);
        assertTrue(decompiled, decompiled.contains("public class JavaDecompiler"));
        assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
    }

    @Test
    public void very_simple_decompilation_using_the_caching_service_with_jdk_class() {
        VirtualFile file = TestUtils.virtualFileOf(TestUtils.jdkObjectClassUrlString(BiFunction.class));

        CharSequence text = ServiceManager.getService(CachingJavaDecompilerService.class).decompile(file);
        assertNotNull(text);

        String decompiled = text.toString();
        System.out.println(decompiled);
        assertTrue(decompiled, decompiled.contains("public interface BiFunction"));
        assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
    }

}
