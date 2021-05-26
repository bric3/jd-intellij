package jd.ide.intellij;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.function.Function;
import java.util.regex.Pattern;

import static jd.ide.intellij.TestUtils.jdkObjectClassUrlString;
import static jd.ide.intellij.TestUtils.virtualFileOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaDecompilerTest extends LightJavaCodeInsightFixtureTestCase4 {

    @Test
    public void nominal_decompilation() throws Exception {
        final var decompiled = new JavaDecompiler().decompile(virtualFileOf(jdkObjectClassUrlString(Function.class)));

        assertEquals(decompiled.toString(),
                     "package java.util.function;\n" +
                     "\n" +
                     "import java.util.Objects;\n" +
                     "\n" +
                     "@FunctionalInterface\n" +
                     "public interface Function<T, R> {\n" +
                     "    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {\n" +
                     "        Objects.requireNonNull(before);\n" +
                     "        return v -> apply(before.apply(v));\n" +
                     "    }\n" +
                     "    \n" +
                     "    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {\n" +
                     "        Objects.requireNonNull(after);\n" +
                     "        return t -> after.apply(apply((T)t));\n" +
                     "    }\n" +
                     "    \n" +
                     "    static <T> Function<T, T> identity() {\n" +
                     "        return t -> t;\n" +
                     "    }\n" +
                     "    \n" +
                     "    R apply(T paramT);\n" +
                     "}\n" +
                     "");
    }

    @Test
    public void should_read_version() {
        assertTrue(Pattern.matches("\\d+(?:\\.\\d+)+", new JavaDecompiler().getVersion()));
    }
}
