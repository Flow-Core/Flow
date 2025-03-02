package compiler.build_system;

import compiler.code_generation.CodeGeneration;
import compiler.library_loader.LibLoader;
import lexer.Lexer;
import lexer.token.Token;
import logger.LoggerFacade;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.FileWrapper;
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
    public static final List<String> flowExtensions = List.of(".fl", ".flow");
    public final String buildPath;

    private final Path dirPath;
    private final LibLoader.LibOutput libOutput;

    private final List<String> fileNames;
    private final List<BlockNode> fileRoots;

    public BuildSystem(final String srcPath, final LibLoader.LibOutput libOutput, final String projectPath) {
        this.dirPath = Path.of(projectPath + srcPath);

        this.libOutput = libOutput;
        buildPath = projectPath + "build";

        this.fileNames = new ArrayList<>();
        this.fileRoots = new ArrayList<>();
    }

    public boolean build() {
        walk();

        final Map<String, PackageWrapper> files = PackageMapper.map(fileRoots, fileNames);
        if (files.isEmpty()) {
            throw LoggerFacade.getLogger().panic("Couldn't find src directory");
        }

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(
            files,
            libOutput != null ? libOutput.packages() : new HashMap<>()
        );
        final Map<String, PackageWrapper> packages = semanticAnalysis.analyze();

        LoggerFacade.getLogger().dump();
        if (LoggerFacade.getLogger().hasErrors()) {
            return false;
        }

        for (final var packageWrapper : packages.entrySet()) {
            for (final FileWrapper file : packageWrapper.getValue().files()) {
                final CodeGeneration codeGeneration = new CodeGeneration(file);
                final List<CodeGeneration.ClassFile> bytes = codeGeneration.generate();

                File buildDir = new File(buildPath + "/" + packageWrapper.getKey().replace(".", "/"));
                if (!buildDir.exists()) {
                    if (!buildDir.mkdirs()) {
                        throw LoggerFacade.getLogger().panic("Couldn't make build dir");
                    }
                }

                for (final CodeGeneration.ClassFile classFile : bytes) {
                    File outputFile = new File(buildDir, classFile.name());
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        fos.write(classFile.content());
                    } catch (IOException e) {
                        throw LoggerFacade.getLogger().panic("Failed to write class file");
                    }
                }
            }
        }

        return true;
    }

    private void walk() {
        try (Stream<Path> stream = Files.walk(dirPath)) {
            stream.forEach(path -> {
                if (Files.isRegularFile(path) &&
                    flowExtensions.stream().anyMatch(extension -> path.getFileName().toString().endsWith(extension))
                ) {
                    buildFile(path);
                }
            });
        } catch (IOException e) {
            throw LoggerFacade.getLogger().panic("src dir not found: " + dirPath);
        }
    }

    private void buildFile(Path path) {
        final String fileName = removeExtension(path.getFileName().toString());

        final String fileContent;

        try {
            fileContent = Files.readString(path);
        } catch (IOException e) {
            throw LoggerFacade.getLogger().panic("Could not build file: " + e.getMessage());
        }

        final BlockNode root = getFileAST(fileContent, fileName);

        fileNames.add(fileName);
        fileRoots.add(root);
    }

    private static BlockNode getFileAST(final String file, final String fileName) {
        final Lexer lexer = new Lexer(file, fileName);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens, fileName);

        return parser.parse();
    }

    private static String removeExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
    }
}
