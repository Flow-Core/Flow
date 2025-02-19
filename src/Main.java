import compiler.code_generation.build_system.BuildSystem;
import compiler.library_loader.LibLoader;
import compiler.packer.JarPacker;
import compiler.packer.Packer;
import compiler.packer.PackerFacade;
import lexer.Lexer;
import lexer.token.Token;
import parser.Parser;
import parser.nodes.components.BlockNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        LibLoader.LibOutput libOutput = null;
        try {
            libOutput = LibLoader.loadLibraries("./libs");
        } catch (Exception e) {
            System.err.println("Could not load libraries");
            e.printStackTrace();
        }

        final BuildSystem buildSystem = new BuildSystem(Path.of("./src"), libOutput);
        buildSystem.build();

        final Packer packer = new JarPacker();
        PackerFacade.initPacker(packer);

        try {
            PackerFacade.pack(
                    "output.jar",
                    "./build",
                    libOutput == null ? new ArrayList<>() : Arrays.asList(libOutput.libFiles()
                )
            );
        } catch (IOException e) {
            System.err.println("Couldn't pack jar file");
            e.printStackTrace();
        }
    }

    private static BlockNode getFileAST(final String file) {
        final Lexer lexer = new Lexer(file);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);

        return parser.parse();
    }
}