package jd.ide.intellij;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.jetbrains.org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Java Decompiler tool.
 */
public class JavaDecompiler {
    private static final ClassFileToJavaSourceDecompiler JD = new ClassFileToJavaSourceDecompiler();
    private static Logger LOGGER = Logger.getInstance(JavaDecompiler.class);

    /**
     * Actual call to the native lib.
     *
     * @param file Path to the root of the classpath, either a path to a directory or a path to a jar file.
     * @return Decompiled class text.
     */
    public CharSequence decompile(VirtualFile file) throws Exception {
        final String clazzInternalName = inferInternalClassName(file);
        Loader ijLoader = new VirtualFileLoader(file, clazzInternalName);

        SimpleDecompiledSourcePrinter printer = new SimpleDecompiledSourcePrinter();

        JD.decompile(ijLoader, printer, clazzInternalName);

        return printer.getStringBuilder();
    }

    private String inferInternalClassName(VirtualFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return new ClassReader(inputStream).getClassName();
        }
    }


//    public String decompile(String basePath, String internalTypeName) {
//        // Load preferences
//        JDPluginComponent jdPluginComponent = ApplicationManager.getApplication().getComponent(JDPluginComponent.class);
//
//        boolean showDefaultConstructor = jdPluginComponent.isShowDefaultConstructorEnabled();
//        boolean realignmentLineNumber = jdPluginComponent.isRealignLineNumbersEnabled();
//        boolean showPrefixThis = !jdPluginComponent.isOmitPrefixThisEnabled();
//        boolean mergeEmptyLines = false;
//        boolean unicodeEscape = jdPluginComponent.isEscapeUnicodeCharactersEnabled();
//        boolean showLineNumbers = jdPluginComponent.isShowLineNumbersEnabled();
//        boolean showMetadata = jdPluginComponent.isShowMetadataEnabled();
//
//        // Create preferences
//        IdePreferences preferences = new IdePreferences(
//            showDefaultConstructor, realignmentLineNumber, showPrefixThis,
//            mergeEmptyLines, unicodeEscape, showLineNumbers, showMetadata);
//
//        // Decompile
//        return IdeDecompiler.decompile(preferences, basePath, internalTypeName);
//    }

    /**
     * TODO manifest reader
     * @return version of JD-Core
     * @since JD-Core 0.7.0
     */
    public String getVersion() {
        return "1.1.3";
    }

    private static class VirtualFileLoader implements Loader {
        private VirtualFile root;

        public VirtualFileLoader(VirtualFile file, String clazzInternalName) {
            long levels = clazzInternalName.chars()
                                      .filter(c -> c == '/')
                                      .count();

            root = Stream.iterate(file.getParent(), VirtualFile::getParent)
                         .skip(levels)
                         .limit(1)
                         .findFirst()
                         .orElseThrow(() -> new IllegalStateException("impossible to find the parent of this class"));

            LOGGER.debug("Using root ", root, " for ", file);
        }

        @Override
        public boolean canLoad(String internalName) {
            // useful for inner or anonymous classes
            return root.findFileByRelativePath(internalName + ".class") != null;
        }

        @Override
        public byte[] load(String internalName) throws LoaderException {
            try {
                LOGGER.debug("Loading for decompilation : ", root, "/", internalName);
                return root.findFileByRelativePath(internalName + ".class")
                           .contentsToByteArray(true);
            } catch (IOException e) {
                throw new LoaderException(e);
            }
        }
    }

    private static class SimpleDecompiledSourcePrinter implements Printer {
        protected static final String TAB = "  ";
        protected static final String NEWLINE = "\n";

        protected int indentationCount = 0;
        protected StringBuilder sb = new StringBuilder();

        public StringBuilder getStringBuilder() {
            return sb;
        }

        @Override public String toString() { return sb.toString(); }

        @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}

        @Override public void end() {}

        @Override public void printText(String text) { sb.append(text); }

        @Override public void printNumericConstant(String constant) { sb.append(constant); }

        @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }

        @Override public void printKeyword(String keyword) { sb.append(keyword); }

        @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }

        @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

        @Override public void indent() { this.indentationCount++; }

        @Override public void unindent() { this.indentationCount--; }

        @Override public void startLine(int lineNumber) { for (int i=0; i<indentationCount; i++) sb.append(TAB); }

        @Override public void endLine() { sb.append(NEWLINE); }

        @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

        @Override public void startMarker(int type) {}

        @Override public void endMarker(int type) {}
    }
}
