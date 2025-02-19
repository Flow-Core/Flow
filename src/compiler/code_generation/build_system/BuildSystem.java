package compiler.code_generation.build_system;

import compiler.code_generation.CodeGeneration;
import compiler.library_loader.LibLoader;
import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.PackageMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BuildSystem {
    public static final String flowExtension = ".fl";
    public static final String buildPath = "build";

    private final Path dirPath;
    private final LibLoader.LibOutput libOutput;

    private final List<String> fileNames;
    private final List<BlockNode> fileRoots;

    public BuildSystem(final Path srcPath, final LibLoader.LibOutput libOutput) {
        this.dirPath = srcPath;
        this.libOutput = libOutput;

        this.fileNames = new ArrayList<>();
        this.fileRoots = new ArrayList<>();
    }

    public void build() {
        walk();

        final Map<String, PackageWrapper> files = PackageMapper.map(fileRoots, fileNames);

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(
            files,
            libOutput != null ? libOutput.packages() : new HashMap<>()
        );
        final Map<String, PackageWrapper> packages = semanticAnalysis.analyze();

        final CodeGeneration codeGeneration = new CodeGeneration(packages.get("").files().get(0));
        final List<CodeGeneration.ClassFile> bytes = codeGeneration.generate();

        File buildDir = new File(buildPath);
        if (!buildDir.exists()) {
            if (!buildDir.mkdir()) {
                System.err.println("Couldn't make build dir");
            }
        }

        for (final CodeGeneration.ClassFile classFile : bytes) {
            File outputFile = new File(buildDir, classFile.name());
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(classFile.content());
            } catch (IOException e) {
                System.out.println("Failed to write class file");
            }
        }
    }

    private void walk() {
        try (Stream<Path> stream = Files.walk(dirPath)) {
            stream.forEach(path -> {
                if (Files.isRegularFile(path) &&
                    path.getFileName().toString().endsWith(flowExtension)
                ) {
                    buildFile(path);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildFile(Path path) {
        final String fileName = removeExtension(path.getFileName().toString());

        final String fileContent;

        try {
            fileContent = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final BlockNode root = getFileAST(fileContent);

        fileNames.add(fileName);
        fileRoots.add(root);
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);

        return parser.parse();
    }

    private static String removeExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
    }
}
