package jd.ide.intellij.decompiler;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import jd.ide.intellij.config.JDPluginSettings;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.jetbrains.org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;

/**
 * Java Decompiler tool.
 */
public class JavaDecompiler {
    private static final ClassFileToJavaSourceDecompiler JD = new ClassFileToJavaSourceDecompiler();
    private static final Logger LOGGER = Logger.getInstance(JavaDecompiler.class);

    public static String version = readVersion();

    /**
     * Actual call to the native lib.
     *
     * @param file Path to the root of the classpath, either a path to a directory or a path to a jar file.
     * @return Decompiled class text.
     */
    public CharSequence decompile(VirtualFile file) throws Exception {
        final String clazzInternalName = inferInternalClassName(file);
        Loader ijLoader = new VirtualFileLoader(file, clazzInternalName);

        final var instance = JDPluginSettings.getInstance();

//        FileDocumentManager.getInstance().getDocument()
//        ProjectManager.getInstance().
//        CodeStyleSettingsManager.getInstance(project).getCurrentSettings();

        SimpleDecompiledSourcePrinter printer = new SimpleDecompiledSourcePrinter(
                file,
                JDPluginSettings.getInstance().isShowMetadata(),
                JDPluginSettings.getInstance().getTabSize()
        );

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("realignLineNumbers", true);

        JD.decompile(ijLoader, printer, clazzInternalName, configuration);

        return printer.toString();
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
     * Reads the JD-Core version
     *
     * @return version of JD-Core
     * @since JD-Core 0.7.0
     */
    public static String readVersion() {
        String className = ClassFileToJavaSourceDecompiler.class.getSimpleName() + ".class";
        String classPath = ClassFileToJavaSourceDecompiler.class.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return "unknown";
        }

        try {
            URL url = new URL(classPath);
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            Manifest manifest = jarConnection.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            final String value = attributes.getValue(new Attributes.Name("JD-Core-Version"));

            return value != null ? value : "unknown";
        } catch (IOException e) {
            LOGGER.warn("[JD] Can't read manifest file or entry of JD-Core, looking for JD-Core-Version", e);
            return "unknown";
        }
    }

    private static class VirtualFileLoader implements Loader {
        private final VirtualFile root;

        public VirtualFileLoader(VirtualFile file, String clazzInternalName) {
            long levels = clazzInternalName.chars()
                                           .filter(c -> c == '/')
                                           .count();

            root = Stream.iterate(file.getParent(), VirtualFile::getParent)
                         .skip(levels)
                         .limit(1)
                         .findFirst()
                         .orElseThrow(() -> new IllegalStateException("impossible to find the parent of this class"));

            LOGGER.debug("[JD] Using root ", root, " for ", file);
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
        protected final String tab;
        protected static final String NEWLINE = "\n";

        protected int indentationCount = 0;
        protected StringBuilder sb = new StringBuilder();
        private final boolean printClassMetadata;
        private final VirtualFile clazzFile;

        public SimpleDecompiledSourcePrinter(VirtualFile clazzFile, boolean printClassMetadata, int tabSize) {
            this.clazzFile = clazzFile;
            this.printClassMetadata = printClassMetadata;
            if (tabSize < 0) {
                throw new IllegalArgumentException("tabSize is negative: " + tabSize);
            }
            this.tab = " ".repeat(tabSize);
        }
        @Override
        public String toString() {
            return sb.toString();
        }

        @Override
        public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            if (!printClassMetadata) {
                return;
            }

            sb.append("/* Location: ").append(clazzFile);

            /* Infer Java language level. (e.g using --release change the class file version as well)
             *
             * The following table is not exhaustive (misses Java 1.2 to 1.4) but gives a good overview.
             * https://docs.oracle.com/javase/specs/jvms/se16/html/jvms-4.html#jvms-4.7-310
             *
             * Table 4.7-B. Predefined class file attributes (by class file format)
             *
             * Attribute                            class file  Java SE   Section
             *
             * ConstantValue                              45.3    1.0.2    §4.7.2
             * Code                                       45.3    1.0.2    §4.7.3
             * Exceptions                                 45.3    1.0.2    §4.7.5
             * SourceFile                                 45.3    1.0.2   §4.7.10
             * LineNumberTable                            45.3    1.0.2   §4.7.12
             * LocalVariableTable                         45.3    1.0.2   §4.7.13
             * InnerClasses                               45.3      1.1    §4.7.6
             * Synthetic                                  45.3      1.1    §4.7.8
             * Deprecated                                 45.3      1.1   §4.7.15
             * EnclosingMethod                            49.0      5.0    §4.7.7
             * Signature                                  49.0      5.0    §4.7.9
             * SourceDebugExtension                       49.0      5.0   §4.7.11
             * LocalVariableTypeTable                     49.0      5.0   §4.7.14
             * RuntimeVisibleAnnotations                  49.0      5.0   §4.7.16
             * RuntimeInvisibleAnnotations                49.0      5.0   §4.7.17
             * RuntimeVisibleParameterAnnotations         49.0      5.0   §4.7.18
             * RuntimeInvisibleParameterAnnotations       49.0      5.0   §4.7.19
             * AnnotationDefault                          49.0      5.0   §4.7.22
             * StackMapTable                              50.0        6    §4.7.4
             * BootstrapMethods                           51.0        7   §4.7.23
             * RuntimeVisibleTypeAnnotations              52.0        8   §4.7.20
             * RuntimeInvisibleTypeAnnotations            52.0        8   §4.7.21
             * MethodParameters                           52.0        8   §4.7.24
             * Module                                     53.0        9   §4.7.25
             * ModulePackages                             53.0        9   §4.7.26
             * ModuleMainClass                            53.0        9   §4.7.27
             * NestHost                                   55.0        11  §4.7.28
             * NestMembers                                55.0        11  §4.7.29
             * Record                                     60.0        16  §4.7.30
             */
            if (majorVersion >= 45) {
                sb.append("\n * Java language version: ");
                switch (majorVersion) {
                    case 45:
                        sb.append("1.0 - 1.1");
                        break;
                    case 46:
                        sb.append("1.2");
                        break;
                    case 47:
                        sb.append("1.3");
                        break;
                    case 48:
                        sb.append("1.4");
                        break;
                    case 49:
                    default:
                        sb.append(majorVersion - (44));
                }
            }
            
            sb.append("\n * Class File: ")
              .append(majorVersion)
              .append('.')
              .append(minorVersion);

            sb.append("\n * JD-Core Version: ").append(JavaDecompiler.version).append("\n */\n\n");
        }

        @Override
        public void end() {
        }

        @Override
        public void printText(String text) {
            sb.append(text);
        }

        @Override
        public void printNumericConstant(String constant) {
            sb.append(constant);
        }

        @Override
        public void printStringConstant(String constant, String ownerInternalName) {
            sb.append(constant);
        }

        @Override
        public void printKeyword(String keyword) {
            sb.append(keyword);
        }

        @Override
        public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
            sb.append(name);
        }

        @Override
        public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
            sb.append(name);
        }

        @Override
        public void indent() {
            this.indentationCount++;
        }

        @Override
        public void unindent() {
            this.indentationCount--;
        }

        @Override
        public void startLine(int lineNumber) {
            for (int i = 0; i < indentationCount; i++) sb.append(tab);
        }

        @Override
        public void endLine() {
            sb.append(NEWLINE);
        }

        @Override
        public void extraLine(int realignmentLines) {
            while (realignmentLines-- > 0) sb.append(NEWLINE);
        }

        @Override
        public void startMarker(int type) {
        }

        @Override
        public void endMarker(int type) {
        }
    }
}