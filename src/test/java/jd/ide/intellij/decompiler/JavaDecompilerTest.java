package jd.ide.intellij.decompiler;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.function.Function;
import java.util.regex.Pattern;

import static jd.ide.intellij.TestUtils.jdkObjectClassUrlString;
import static jd.ide.intellij.TestUtils.virtualFileOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class JavaDecompilerTest extends LightJavaCodeInsightFixtureTestCase4 {

    @Test
    public void nominal_decompilation() throws Exception {
        final var decompiled = new JavaDecompiler().decompile(virtualFileOf(jdkObjectClassUrlString(Function.class)));

        assertThat(decompiled)
                .startsWith("/* Location: ")
                .containsPattern("Location: .*java/util/function/Function\\.class")
                .containsSubsequence(
                        " * Java language version: 11\n" +
                        " * Class File: 55.0\n" +
                        " * JD-Core Version: 1.1.3\n" +
                        " */",

                        "package java.util.function;\n" +
                        "\n" +
                        "import java.util.Objects;\n",

                        "@FunctionalInterface\n" +
                        "public interface Function<T, R>\n" +
                        "{",

                        "    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {\n" +
                        "        Objects.requireNonNull(before);\n" +
                        "        return v -> apply(before.apply(v));\n" +
                        "    }\n",

                        "    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {\n" +
                        "        Objects.requireNonNull(after);\n" +
                        "        return t -> after.apply(apply((T)t));\n" +
                        "    }",

                        "    static <T> Function<T, T> identity() {\n" +
                        "        return t -> t;\n" +
                        "    }\n",
                        "    R apply(T paramT);\n",

                        "}\n");
    }

    @Test
    public void should_read_version() {
        assertTrue(Pattern.matches("\\d+(?:\\.\\d+)+", JavaDecompiler.readVersion()));
    }
}
