package semantic_analysis.transformers;

import parser.nodes.classes.ObjectNode;
import parser.nodes.components.ArgumentNode;
import parser.nodes.expressions.ExpressionBaseNode;
import parser.nodes.literals.LiteralNode;

import java.util.List;

public class LiteralTransformer {
    public static ObjectNode transform(final LiteralNode literalNode) {
        return new ObjectNode(
            literalNode.getClassName(),
            List.of(
                new ArgumentNode(
                    null,
                    new ExpressionBaseNode(
                        literalNode
                    )
                )
            )
        );
    }
}