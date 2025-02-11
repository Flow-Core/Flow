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

    public List<ClassFile> generate() {
        List<ClassFile> generated = new ArrayList<>();

        for (final ASTNode node : file.root().children) {
            System.out.println(node);
            if (node instanceof ClassDeclarationNode classDeclarationNode) {
                generated.addAll(
                    ClassGenerator.generate(
                        classDeclarationNode,
                        file
                    )
                );
            } else if (node instanceof InterfaceNode interfaceNode) {
                generated.addAll(
                    InterfaceGenerator.generate(
                        interfaceNode,
                        file
                    )
                );
            }
        }

        return generated;
    }

    public record ClassFile(
       String name,
       byte[] content
    ) {}
}