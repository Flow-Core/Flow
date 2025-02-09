package compiler.code_generation;

import compiler.code_generation.generators.ClassGenerator;
import compiler.code_generation.generators.InterfaceGenerator;
import parser.nodes.ASTNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.InterfaceNode;
import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

public class CodeGeneration {
    final FileWrapper file;

    public CodeGeneration(FileWrapper file) {
        this.file = file;
    }

    public List<byte[]> generate() {
        List<byte[]> generated = new ArrayList<>();

        for (final ASTNode node : file.root().children) {
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                generated.add(ClassGenerator.generate(classDeclarationNode, file.scope()).toByteArray());
            } else if (node instanceof InterfaceNode interfaceNode) {
                generated.add(InterfaceGenerator.generate(interfaceNode, file.scope()).toByteArray());
            }
        }

        return generated;
    }
}
