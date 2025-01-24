package semantic_analysis;

import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;

import java.util.List;

public record SymbolTable(
    List<ClassDeclarationNode> classes,
    List<FunctionDeclarationNode> functions,
    List<FieldNode> fields
) { }
