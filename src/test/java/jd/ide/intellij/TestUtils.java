package jd.ide.intellij;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.SystemProperties;
import com.intellij.util.lang.JavaVersion;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertNotNull;

public class TestUtils {
    public static String localFileSystemClassFileUrl(Class<?> aClass) {
        return (aClass.getProtectionDomain().getCodeSource().getLocation().toString()
                + aClass.getPackage().getName().replaceAll("\\.", "/") + "/"
                + aClass.getSimpleName() + ".class").replace("file:", "file://");
    }

    public static String jdkObjectClassUrlString(Class<?> jdkType) {
        // if the JDK has modules then use
        // "jrt:/java.base/java/lang/Object.class"
        // otherwise build the full URI to the Object class
        // "jar:file:/path-to-jre/lib/rt.jar!/java/lang/Object.class"


        String jdkInternalName = jdkType.getName().replace('.', '/');
        return JavaVersion.current().feature >= 9 ?
//                Object.class.getResource("Object.class").toString() : // not handled by JrtFileSystem at this time
               "jrt://" + SystemProperties.getJavaHome() + "!/java.base/" + jdkInternalName + ".class" :
               "jar://" + SystemProperties.getJavaHome() + "" + (SystemInfo.isOracleJvm ? "/lib/rt.jar" : "/../Classes/classes.jar") + "!/" + jdkInternalName + ".class";
    }

    public static File nativeUrlPath() throws URISyntaxException {
        URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
        for (URL url : urls) {
            if (url.getPath().contains("native")) return new File(url.toURI());
        }
        throw new IllegalStateException("Cannot find the native folder, where the native libs are located");
    }

    public static VirtualFile virtualFileOf(String url) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        assertNotNull("should have found the file : " + url, file);
        return file;
    }
}
