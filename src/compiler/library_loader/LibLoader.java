package compiler.library_loader;

import compiler.code_generation.mappers.ModifierMapper;
import logger.LoggerFacade;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.components.BlockNode;
import parser.nodes.components.BodyNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.generics.TypeArgument;
import parser.nodes.generics.TypeParameterNode;
import semantic_analysis.files.PackageWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static compiler.library_loader.TypeMapper.mapType;

public class LibLoader {
    public static LibOutput loadLibraries(String libFolderPath) throws Exception {
        File libFolder = new File(libFolderPath);
        Map<String, PackageWrapper> packages = new HashMap<>();

        loadJavaStdLib(packages);

        if (!libFolder.exists() || !libFolder.isDirectory()) {
            return new LibOutput(
                new File[0],
                packages
            );
        }

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

            convertToFlowType(st, classNode);

            return packageWrapper;
        }
    }

    private static void convertToFlowType(SymbolTable symbolTable, ClassNode classNode) {
        if ((classNode.access & Opcodes.ACC_INTERFACE) != 0) {
            convertToFlowInterface(symbolTable, classNode);
        } else {
            convertToFlowClass(symbolTable, classNode);
        }
    }

    private static void convertToFlowClass(SymbolTable symbolTable, ClassNode classNode) {
        String className = classNode.name.replace("/", ".");
        className = trimPackageName(className);

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
                constructors.add(convertToFlowConstructor(method));
            } else {
                methods.add(convertToFlowMethod(method));
            }
        }

        List<BaseClassNode> baseClasses = (classNode.superName == null)
            ? List.of()
            : List.of(new BaseClassNode(new FlowType(classNode.superName.replace("/", "."), false, false), List.of()));

        List<BaseInterfaceNode> interfaces = classNode.interfaces.stream()
            .map(baseInterface -> new BaseInterfaceNode(new FlowType(baseInterface.replace("/", "."), false, false)))
            .toList();

        List<TypeParameterNode> typeParams = extractTypeParameters(classNode.signature);
        final ClassDeclarationNode flowClass = new ClassDeclarationNode(
            className,
            extractModifiers(classNode.access),
            typeParams,
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
        for (TypeParameterNode typeParam : typeParams) {
            symbolTable.typeParameters().add(typeParam);
        }

        symbolTable.classes().add(flowClass);
        symbolTable.bindingContext().put(flowClass, classNode.name.replace("/", "."));
    }

    private static void convertToFlowInterface(SymbolTable symbolTable, ClassNode classNode) {
        String interfaceName = trimPackageName(classNode.name.replace("/", "."));

        List<BaseInterfaceNode> implementedInterfaces = classNode.interfaces.stream()
            .map(baseInterface -> new BaseInterfaceNode(new FlowType(trimPackageName(baseInterface.replace("/", ".")), false, false)))
            .toList();

        List<FunctionDeclarationNode> methods = new ArrayList<>();
        for (MethodNode method : classNode.methods) {
            methods.add(convertToFlowMethod(method));
        }

        List<TypeParameterNode> typeParams = extractTypeParameters(classNode.signature);
        InterfaceNode flowInterface = new InterfaceNode(
            interfaceName,
            extractModifiers(classNode.access),
            extractTypeParameters(classNode.signature),
            implementedInterfaces,
            methods,
            new BlockNode(new ArrayList<>())
        );

        symbolTable.bindingContext().put(flowInterface, classNode.name.replace("/", "."));
        for (TypeParameterNode typeParam : typeParams) {
            symbolTable.typeParameters().add(typeParam);
        }

        symbolTable.interfaces().add(flowInterface);
        symbolTable.bindingContext().put(flowInterface, classNode.name.replace("/", "."));
    }

    private static List<TypeParameterNode> extractTypeParameters(String signature) {
        if (signature == null) return List.of();

        List<TypeParameterNode> typeParameters = new ArrayList<>();
        SignatureReader reader = new SignatureReader(signature);

        reader.accept(new SignatureVisitor(Opcodes.ASM9) {
            String currentName = null;
            final List<FlowType> currentBounds = new ArrayList<>();

            @Override
            public void visitFormalTypeParameter(String name) {
                if (currentName != null) {
                    FlowType effectiveBound = currentBounds.isEmpty()
                        ? new FlowType("java.lang.Object", false, false)
                        : currentBounds.get(0);
                    typeParameters.add(new TypeParameterNode(currentName, effectiveBound));
                    currentBounds.clear();
                }
                currentName = name;
            }

            @Override
            public SignatureVisitor visitClassBound() {
                return createFlowTypeVisitor(currentBounds::add);
            }

            @Override
            public SignatureVisitor visitInterfaceBound() {
                return createFlowTypeVisitor(currentBounds::add);
            }

            @Override
            public void visitEnd() {
                if (currentName != null) {
                    FlowType effectiveBound = currentBounds.isEmpty()
                        ? new FlowType("java.lang.Object", false, false)
                        : currentBounds.get(0);
                    typeParameters.add(new TypeParameterNode(currentName, effectiveBound));
                }
            }
        });

        return typeParameters;
    }

    private static SignatureVisitor createFlowTypeVisitor(Consumer<FlowType> onComplete) {
        return new SignatureVisitor(Opcodes.ASM9) {
            String name;
            final List<TypeArgument> typeArguments = new ArrayList<>();

            @Override
            public void visitClassType(String name) {
                this.name = name.replace('/', '.');
            }

            @Override
            public SignatureVisitor visitTypeArgument(char wildcard) {
                return createFlowTypeVisitor(type -> typeArguments.add(new TypeArgument(type)));
            }

            @Override
            public void visitEnd() {
                if (name != null) {
                    FlowType flowType = new FlowType(trimPackageName(name), false, false, typeArguments);
                    onComplete.accept(flowType);
                }
            }
        };
    }

    private static FunctionDeclarationNode convertToFlowMethod(MethodNode method) {
        FlowType returnType = mapType(Type.getReturnType(method.desc).getClassName());

        return new FunctionDeclarationNode(
            method.name,
            returnType,
            extractModifiers(method.access),
            parseParameters(method),
            extractTypeParameters(method.signature),
            null
        );
    }

    private static ConstructorNode convertToFlowConstructor(MethodNode method) {
        final List<String> modifier = extractModifiers(method.access);
        return new ConstructorNode(
            modifier.isEmpty() ? "public" : modifier.get(0),
            parseParameters(method),
            new BodyNode(
                new BlockNode(
                    new ArrayList<>()
                ),
                new Scope(
                    null,
                    SymbolTable.getEmptySymbolTable(),
                    null,
                    Scope.Type.FUNCTION
                )
            )
        );
    }

    private static List<ParameterNode> parseParameters(MethodNode methodNode) {
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

    private static void loadJavaStdLib(Map<String, PackageWrapper> packages) {
        try (FileSystem jrtFS = FileSystems.newFileSystem(URI.create("jrt:/"), Map.of())) {
            Path modulesPath = jrtFS.getPath("modules/java.base");

            try (Stream<Path> paths = Files.walk(modulesPath)) {
                paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .forEach(file -> {
                        try {
                            registerJavaClass(file, packages);
                        } catch (IOException e) {
                            LoggerFacade.error("Failed to load Java standard class: " + file);
                        }
                    });
            }

        } catch (IOException e) {
            LoggerFacade.error("Failed to load Java standard library: " + e.getMessage());
        }
    }

    private static void registerJavaClass(Path classFile, Map<String, PackageWrapper> packages) throws IOException {
        try (var inputStream = Files.newInputStream(classFile)) {
            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            String packageName = extractPackageName(classNode);

            PackageWrapper packageWrapper = packages.computeIfAbsent(packageName, k -> new PackageWrapper(
                packageName, new ArrayList<>(), new Scope(null, SymbolTable.getEmptySymbolTable(), null, Scope.Type.TOP)
            ));
            SymbolTable symbolTable = packageWrapper.scope().symbols();

            convertToFlowType(symbolTable, classNode);
        }
    }

    private static String trimPackageName(String fullName) {
        int lastDotIndex = fullName.lastIndexOf(".");
        return (lastDotIndex != -1) ? fullName.substring(lastDotIndex + 1) : fullName;
    }

    public record LibOutput(
        File[] libFiles,
        Map<String, PackageWrapper> packages
    ) {}
}