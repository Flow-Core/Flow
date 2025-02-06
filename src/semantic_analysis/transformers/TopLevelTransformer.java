package semantic_analysis.transformers;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

public class TopLevelTransformer {
    public static ClassDeclarationNode transform(final FileWrapper file) {
        final ClassDeclarationNode topLevelClass = new ClassDeclarationNode(
            "", // TODO: class name as the name of the file
            List.of("public"),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            null
        );

        for (final ASTNode child : file.root().children) {
            if (child instanceof FunctionDeclarationNode functionDeclarationNode) {
                topLevelClass.methods.add(functionDeclarationNode);
            } else if (child instanceof FieldNode fieldNode) {
                topLevelClass.fields.add(fieldNode);
            }
        }

        return topLevelClass;
    }
}
