package compiler.library_loader;

import compiler.code_generation.mappers.ModifierMapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import parser.nodes.FlowType;
import parser.nodes.classes.BaseClassNode;
import parser.nodes.classes.BaseInterfaceNode;
import parser.nodes.classes.ClassDeclarationNode;
import parser.nodes.classes.ConstructorNode;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static compiler.library_loader.TypeMapper.mapType;

public class LibLoader {
    public static LibOutput loadLibraries(String libFolderPath) throws Exception {
        File libFolder = new File(libFolderPath);
        if (!libFolder.exists() || !libFolder.isDirectory()) {
            throw new IllegalArgumentException("Library folder not found: " + libFolderPath);
        }

        Map<String, PackageWrapper> packages = new HashMap<>();

        File[] jarFiles = libFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return new LibOutput(
                new File[0],
                packages
            );
        }

        for (File jarFile : jarFiles) {
            loadClassesFromJar(jarFile, packages);
        }

        return new LibOutput(
            jarFiles,
            packages
        );
    }

    private static void loadClassesFromJar(File jarFile, Map<String, PackageWrapper> packages) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            final var it = jar.entries().asIterator();
            while (it.hasNext()) {
                final var entry = it.next();
                if (entry.getName().endsWith(".class")) {
                    PackageWrapper currentPackage = extractClass(jar, entry);
                    packages.computeIfAbsent(currentPackage.path(), key -> currentPackage)
                        .scope()
                        .symbols()
                        .recognizeSymbolTable(currentPackage.scope().symbols());
                }
            }
        }
    }

    private static PackageWrapper extractClass(JarFile jar, JarEntry entry) throws IOException {
        try (var inputStream = jar.getInputStream(entry)) {
            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            String packageName = extractPackageName(classNode);
            SymbolTable st = SymbolTable.getEmptySymbolTable();

            PackageWrapper packageWrapper = new PackageWrapper(
                packageName,
                new ArrayList<>(),
                new Scope(
                    null,
                    st,
                    null,
                    Scope.Type.TOP
                )
            );

            ClassDeclarationNode flowClass = convertToFlowClass(st, classNode);
            st.classes().add(flowClass);

            return packageWrapper;
        }
    }

    private static ClassDeclarationNode convertToFlowClass(SymbolTable symbolTable, ClassNode classNode) {
        int nameIndex = classNode.name.lastIndexOf("/");
        String className;

        if (nameIndex == -1) {
            className = classNode.name;
        } else {
            className = classNode.name.substring(nameIndex + 1);
        }

        List<parser.nodes.classes.FieldNode> fields = new ArrayList<>();
        for (FieldNode field : classNode.fields) {
            FlowType fieldType = TypeMapper.mapType(Type.getType(field.desc).getClassName());

            parser.nodes.classes.FieldNode fieldNode = new parser.nodes.classes.FieldNode(
                extractModifiers(field.access),
                new parser.nodes.variable.InitializedVariableNode(
                    new parser.nodes.variable.VariableDeclarationNode("var", fieldType, field.name),
                    null
                )
            );

            if (fieldNode.modifiers.remove("final")) {
                fieldNode.initialization.declaration.modifier = "val";
            }

            fields.add(fieldNode);
        }

        List<FunctionDeclarationNode> methods = new ArrayList<>();
        List<ConstructorNode> constructors = new ArrayList<>();

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<init>")) {
                constructors.add(convertToFlowConstructor(classNode, method));
            } else {
                methods.add(convertToFlowMethod(classNode, method));
            }
        }

        List<BaseClassNode> baseClasses = List.of(new BaseClassNode(classNode.superName.replace("/", "."), List.of()));

        List<BaseInterfaceNode> interfaces = classNode.interfaces.stream().map(
            baseInterface -> new BaseInterfaceNode(baseInterface.replace("/", "."))
        ).toList();

        final ClassDeclarationNode flowClass = new ClassDeclarationNode(
            className,
            extractModifiers(classNode.access),
            new ArrayList<>(),
            baseClasses,
            interfaces,
            fields,
            methods,
            constructors,
            null,
            null
        );

        symbolTable.bindingContext().put(flowClass, classNode.name.replace("/", "."));
        return flowClass;
    }

    private static FunctionDeclarationNode convertToFlowMethod(ClassNode classNode, MethodNode method) {
        FlowType returnType = mapType(Type.getReturnType(method.desc).getClassName());

        return new FunctionDeclarationNode(
            method.name,
            returnType,
            extractModifiers(method.access),
            parseParameters(classNode, method, false),
            null
        );
    }

    private static ConstructorNode convertToFlowConstructor(ClassNode classNode, MethodNode method) {
        final List<String> modifier = extractModifiers(method.access);
        return new ConstructorNode(
            modifier.isEmpty() ? "public" : modifier.get(0),
            parseParameters(classNode, method, true),
            new BodyNode(new BlockNode(new ArrayList<>()), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.FUNCTION))
        );
    }

    private static List<ParameterNode> parseParameters(ClassNode classNode, MethodNode methodNode, boolean isConstructor) {
        Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
        List<ParameterNode> parameters = new ArrayList<>();

        for (int i = 0; i < argumentTypes.length; i++) {
            FlowType type = mapType(argumentTypes[i].getClassName());
            String paramName = (methodNode.parameters != null && i < methodNode.parameters.size())
                ? methodNode.parameters.get(i).name
                : null;

            parameters.add(
                new ParameterNode(
                    type,
                    paramName,
                    null
                )
            );
        }

        if (!isConstructor && (methodNode.access & Opcodes.ACC_STATIC) == 0) {
            parameters.add(
                0,
                    new ParameterNode(new FlowType(classNode.name.replace("/", "."), true, false),
                    "this",
                    null
                )
            );
        }

        return parameters;
    }

    private static String extractPackageName(ClassNode classNode) {
        int lastSlashIndex = classNode.name.lastIndexOf('/');
        return (lastSlashIndex != -1) ? classNode.name.substring(0, lastSlashIndex).replace("/", ".") : "";
    }

    private static List<String> extractModifiers(int flags) {
        List<String> modifiers = new ArrayList<>();

        for (String key : ModifierMapper.MODIFIER_MAP.keySet()) {
            if ((flags & ModifierMapper.MODIFIER_MAP.get(key)) != 0) {
                modifiers.add(key);
            }
        }

        return modifiers;
    }

    public record LibOutput(
       File[] libFiles,
       Map<String, PackageWrapper> packages
    ) {}
}