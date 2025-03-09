package semantic_analysis.transformers;

import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.FieldNode;
import parser.nodes.components.BlockNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

import static semantic_analysis.scopes.SymbolTable.joinPath;

public class TopLevelTransformer {
    public static void transform(final FileWrapper file, final String packageName) {
        final ClassDeclarationNode topLevelClass = new ClassDeclarationNode(
            file.name() + "Fl",
            List.of("public"),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            new BlockNode(new ArrayList<>())
        );

        for (final ASTNode child : file.root().children) {
            if (child instanceof FunctionDeclarationNode functionDeclarationNode) {
                topLevelClass.methods.add(functionDeclarationNode);
            } else if (child instanceof FieldNode fieldNode) {
                topLevelClass.fields.add(fieldNode);
            }
        }

        file.root().children.add(topLevelClass);

        file.scope().symbols().classes().add(topLevelClass);
        file.scope().symbols().bindingContext().put(topLevelClass, joinPath(packageName, topLevelClass.name));
    }
}