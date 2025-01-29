package semantic_analysis;

import parser.nodes.components.BlockNode;
import semantic_analysis.scopes.Scope;

public record FileWrapper (
    BlockNode root,
    Scope scope
) { }
