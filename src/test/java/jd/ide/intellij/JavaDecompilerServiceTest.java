package jd.ide.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.SystemProperties;
import com.intellij.util.lang.JavaVersion;
import org.junit.Ignore;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaDecompilerServiceTest extends LightJavaCodeInsightFixtureTestCase {

    public void testVerySimpleDecompilationUsingTheCachingServiceWithLocalFSClass() throws URISyntaxException {
        VirtualFile file = virtualFileOf(localFileSystemClassFileUrl(JavaDecompiler.class));

        CharSequence text = ServiceManager.getService(CachingJavaDecompilerService.class).decompile(file);
        assertNotNull(text);

        String decompiled = text.toString();
        assertTrue(decompiled, decompiled.contains("public class JavaDecompiler"));
        assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
    }

//    public void testVerySimpleDecompilationUsingTheCachingServiceWithJDKCLass() throws URISyntaxException {
//        VirtualFile file = virtualFileOf(jdkObjectClassUrlString());
//
//        CharSequence text = ServiceManager.getService(CachingJavaDecompilerService.class).decompile(file);
//        assertNotNull(text);
//
//        String decompiled = text.toString();
//        assertTrue(decompiled, decompiled.contains("public class Object"));
//        assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
//    }

    private VirtualFile virtualFileOf(String url) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        assertNotNull("should have found the file : " + url, file);
        return file;
    }

    private String jdkObjectClassUrlString() {
        // if the JDK has modules then use
        // "jrt:/java.base/java/lang/Object.class"
        // otherwise build the full URI to the Object class
        // "jar:file:/path-to-jre/lib/rt.jar!/java/lang/Object.class"

        String objectClass = "java/lang/Object.class";
        return JavaVersion.current().feature >= 9 ?
//                Object.class.getResource("Object.class").toString() : // not handled by JrtFileSystem at this time
                "jrt://" + SystemProperties.getJavaHome() + "!/java.base/" + objectClass :
                "jar://" + SystemProperties.getJavaHome() + "" + (SystemInfo.isOracleJvm ? "/lib/rt.jar" : "/../Classes/classes.jar") + "!/" + objectClass;
    }

    private String localFileSystemClassFileUrl(Class<?> aClass) {
        return (aClass.getProtectionDomain().getCodeSource().getLocation().toString()
                + aClass.getPackage().getName().replaceAll("\\.", "/") + "/"
                + aClass.getSimpleName() + ".class").replace("file:", "file://");
    }

    public File nativeUrlPath() throws URISyntaxException {
        URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
        for (URL url : urls) {
            if (url.getPath().contains("native")) return new File(url.toURI());
        }
        throw new IllegalStateException("Cannot find the native folder, where the native libs are located");
    }

}
