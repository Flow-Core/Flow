import lexer.Lexer;
import lexer.token.Token;
import logger.LoggerFacade;
import logger.impl.ConsoleLogger;
import parser.Parser;
import parser.nodes.components.BlockNode;
import semantic_analysis.SemanticAnalysis;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.loaders.PackageMapper;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final String file1 = """
            package main.first
            \s
            func main(): A? {
                val x = 2
                return null
            }
            
            class A {}
           \s""";

        final String file2 = """
        """;

        final String file3 = """
        """;

        LoggerFacade.initLogger(new ConsoleLogger());

        final BlockNode file1Root = getFileAST(file1, "File1.fl");
        final BlockNode file2Root = getFileAST(file2, "File2.fl");
        final BlockNode file3Root = getFileAST(file3, "File3.fl");
        final Map<String, PackageWrapper> files = PackageMapper.map(List.of(file1Root, file2Root, file3Root));

        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis(files);
        semanticAnalysis.analyze();

        if (LoggerFacade.getLogger().hasErrors()) {
            LoggerFacade.getLogger().dump();
        }
    }

    private static BlockNode getFileAST(final String fileContent, final String fileName) {
        final Lexer lexer = new Lexer(fileContent, fileName);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens, fileName);
        final BlockNode root = parser.parse();
        Parser.printTree(root);

        return root;
    }
}