package parser.analyzers.top;

import lexer.token.TokenType;
import logger.LoggerFacade;
import parser.Parser;
import parser.analyzers.TopAnalyzer;
import parser.exceptions.PARSE_WrongAnalyzer;
import parser.nodes.ASTNode;
import parser.nodes.components.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyzer {
    public static BlockNode parse(
        final Parser parser,
        final List<TopAnalyzer> analyzers,
        final TokenType... blockTerminators
    ) {
        final boolean isSingleLine = blockTerminators.length == 0;
        final List<ASTNode> nodes = new ArrayList<>();

        TopAnalyzer.AnalyzerResult result = null;
        do {
            if (parser.peek().isLineTerminator()) {
                parser.advance();
            }
            if (parser.check(blockTerminators)) {
                break;
            }

            for (final TopAnalyzer analyzer : analyzers) {
                parser.checkpoint();
                result = null;
                try {
                    result = analyzer.parse(parser);
                    if (result == null || result.node() == null) {
                        parser.rollback();
                        continue;
                    }
                } catch (PARSE_WrongAnalyzer exception) {
                    parser.rollback();
                    continue;
                }

                break;
            }

            if (result == null || result.node() == null) {
                throw LoggerFacade.getLogger().panic("Invalid statement", parser.peek().line(), parser.file);
            }
            if (result.terminationStatus() == TopAnalyzer.TerminationStatus.WAS_TERMINATED) {
                if (!isSingleLine) {
                    parser.advance();
                }
            } else if (result.terminationStatus() == TopAnalyzer.TerminationStatus.NOT_TERMINATED && !parser.check(blockTerminators)) {
                throw LoggerFacade.getLogger().panic("Required newline or ';' after statement", parser.peek().line(), parser.file);
            }
            nodes.add(result.node());
        } while (!isSingleLine && !parser.check(blockTerminators));

        return new BlockNode(nodes);
    }
}
