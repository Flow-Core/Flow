package compiler.library_loader;

import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import parser.nodes.classes.BaseClassNode;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LibLoader {
    public static LibOutput loadLibraries(String libFolderPath) throws Exception {
        SymbolTable symbolTable = SymbolTable.getEmptySymbolTable();

        File libFolder = new File(libFolderPath);
        if (!libFolder.exists() || !libFolder.isDirectory()) {
            throw new IllegalArgumentException("Library folder not found: " + libFolderPath);
        }

        File[] jarFiles = libFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return new LibOutput(
                new File[0],
                new Scope(null, symbolTable, null, Scope.Type.TOP)
            );
        }

        for (File jarFile : jarFiles) {
            loadClassesFromJar(jarFile, symbolTable);
        }

        return new LibOutput(
            jarFiles,
            new Scope(null, symbolTable, null, Scope.Type.TOP)
        );
    }

    private static void loadClassesFromJar(File jarFile, SymbolTable symbolTable) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            final var it = jar.entries().asIterator();
            while (it.hasNext()) {
                final var entry = it.next();
                if (entry.getName().endsWith(".class")) {
                    extractClass(jar, entry, symbolTable);
                }
            }
        }
    }

    private static void extractClass(JarFile jar, JarEntry entry, SymbolTable symbolTable) throws IOException {
        try (var inputStream = jar.getInputStream(entry)) {
            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            ClassDeclarationNode flowClass = convertToFlowClass(symbolTable, classNode);
            symbolTable.classes().add(flowClass);
        }
    }

    private static ClassDeclarationNode convertToFlowClass(SymbolTable symbolTable, ClassNode classNode) {
        int nameIndex = classNode.name.lastIndexOf("/");
        String className;

        if (nameIndex == -1) {
            className = classNode.name;
        } else {
            className = classNode.name.substring(nameIndex);
        }

        List<parser.nodes.classes.FieldNode> fields = new ArrayList<>();
        for (FieldNode field : classNode.fields) {
            String fieldType = Type.getType(field.desc).getClassName();

            parser.nodes.classes.FieldNode fieldNode = new parser.nodes.classes.FieldNode(extractModifiers(field.access), new parser.nodes.variable.InitializedVariableNode(
                new parser.nodes.variable.VariableDeclarationNode("var", fieldType, field.name, true), null));

            if (fieldNode.modifiers.remove("final")) {
                fieldNode.initialization.declaration.modifier = "val";
            }

            fields.add(fieldNode);
        }

        List<FunctionDeclarationNode> methods = new ArrayList<>();
        for (MethodNode method : classNode.methods) {
            methods.add(convertToFlowMethod(method));
        }

        List<BaseClassNode> baseClasses = List.of(new BaseClassNode(classNode.superName, List.of()));

        List<BaseInterfaceNode> interfaces = classNode.interfaces.stream().map(BaseInterfaceNode::new).toList();

        final ClassDeclarationNode flowClass = new ClassDeclarationNode(className, extractModifiers(classNode.access), new ArrayList<>(), baseClasses,
            interfaces, fields, methods, new ArrayList<>(), null, null);

        symbolTable.bindingContext().put(flowClass, classNode.name);
        return flowClass;
    }

    private static FunctionDeclarationNode convertToFlowMethod(MethodNode method) {
        String returnType = Type.getReturnType(method.desc).getClassName();
        boolean isNullable = true; // TODO: Add annotations or think about a better solution
        return new FunctionDeclarationNode(method.name, returnType, isNullable, extractModifiers(method.access), new ArrayList<>(), null);
    }

    private static List<String> extractModifiers(int flags) {
        List<String> modifiers = new ArrayList<>();

        for (String key : ModifierMapper.MODIFIER_MAP.keySet()) {
            if ((flags & ModifierMapper.MODIFIER_MAP.get(key)) > 0) {
                modifiers.add(key);
            }
        }

        return modifiers;
    }

    public record LibOutput(
       File[] libFiles,
       Scope libScope
    ) {}
}