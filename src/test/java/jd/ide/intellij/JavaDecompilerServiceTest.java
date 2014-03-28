package jd.ide.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaDecompilerServiceTest extends LightCodeInsightFixtureTestCase {

    public void testVerySimpleDecompilationUsingTheCachingService() throws URISyntaxException {
        VirtualFile file = objectClassVirtualFile();

        CharSequence text = ServiceManager.getService(CachingJavaDecompilerService.class).decompile(file);
        assertNotNull(text);

        String decompiled = text.toString();
        assertTrue(decompiled, decompiled.contains("public class Object"));
        assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
    }

    private VirtualFile objectClassVirtualFile() {
        String url = getString();
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        assertNotNull("should have found the file : " + url, file);
        return file;
    }

    private String getString() {
        return "jar://" + System.getProperty("java.home") + "" + (SystemInfo.isOracleJvm ? "/lib/rt.jar" : "/../Classes/classes.jar") + "!/java/lang/Object.class";
    }

    public File nativeUrlPath() throws URISyntaxException {
        URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
        for (URL url : urls) {
            if(url.getPath().contains("native")) return new File(url.toURI());
        }
        throw new IllegalStateException("Cannot find the native folder, where the native libs are located");
    }

}
