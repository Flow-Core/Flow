package compiler.code_generation.generators;

import compiler.code_generation.manager.VariableManager;
import compiler.code_generation.mappers.BoxMapper;
import compiler.code_generation.mappers.FQNameMapper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import parser.nodes.FlowType;
import parser.nodes.classes.*;
import parser.nodes.components.ArgumentNode;
import parser.nodes.components.ParameterNode;
import parser.nodes.expressions.ExpressionNode;
import parser.nodes.expressions.UnaryOperatorNode;
import parser.nodes.functions.FunctionCallNode;
import parser.nodes.functions.FunctionDeclarationNode;
import parser.nodes.literals.*;
import parser.nodes.variable.FieldReferenceNode;
import parser.nodes.variable.VariableReferenceNode;
import semantic_analysis.files.FileWrapper;
import semantic_analysis.scopes.Scope;
import semantic_analysis.scopes.TypeRecognize;
import semantic_analysis.visitors.ParameterTraverse;

import java.util.List;

public class ExpressionGenerator {
    public static FlowType generate(ExpressionNode expression, MethodVisitor mv, VariableManager vm, FileWrapper file, FlowType expectedType) {
        final StackTracker tracker = new StackTracker(mv);
        FlowType result = null;

        if (expression instanceof VariableReferenceNode referenceNode) {
            result = generateVarReference(referenceNode, vm, tracker, expectedType);
        } else if (expression instanceof FunctionCallNode functionCallNode) {
            result = generateFuncCall(functionCallNode, file.scope(), file, vm, mv, tracker, expectedType);
        } else if (expression instanceof ObjectNode objectNode) {
            result = generateObjectInstantiation(objectNode, file.scope(), file, vm, mv, tracker);
        } else if (expression instanceof FieldReferenceNode fieldReferenceNode) {
            result = generateFieldReference(fieldReferenceNode, file.scope(), file, mv, vm, tracker, expectedType);
        } else if (expression instanceof UnaryOperatorNode unaryExpression) {
            result = generateUnary(unaryExpression, file.scope(), file, mv, vm, tracker, expectedType);
        } else if (expression instanceof LiteralNode literalNode) {
            result = generateLiteral(literalNode, mv, tracker, expectedType);
        } else if (expression instanceof NullLiteral) {
            mv.visitInsn(Opcodes.ACONST_NULL);
            tracker.hang(1);
        } else {
            throw new UnsupportedOperationException("Unknown expression type: " + (expression != null ? expression.getClass().getSimpleName() : "null"));
        }

        if (expectedType == null) tracker.cleanStack();

        return result;
    }

    private static FlowType generateVarReference(VariableReferenceNode refNode, VariableManager vm, StackTracker tracker, FlowType expectedType) {
        return tracker.hang(vm.loadVariable(refNode.variable, expectedType));
    }

    private static FlowType generateFuncCall(FunctionCallNode funcCallNode, Scope scope, FileWrapper file, VariableManager vm, MethodVisitor mv, StackTracker tracker, FlowType expectedType) {
        if (funcCallNode.callerType == null) {
            final FunctionDeclarationNode declaration = ParameterTraverse.findMethodByArguments(
                scope,
                funcCallNode.name,
                funcCallNode.arguments
            );
            if (declaration == null) {
                throw new IllegalArgumentException("Function " + funcCallNode.name + " was not found in the scope");
            }

            final String fqTopLevelName = FQNameMapper.getFQName(declaration, scope);
            final String descriptor = FunctionGenerator.getDescriptor(declaration, scope);

            processFunctionArguments(
                declaration.parameters,
                funcCallNode.arguments,
                mv,
                vm,
                file
            );

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, fqTopLevelName, funcCallNode.name, descriptor, false);
            BoxMapper.boxIfNeeded(declaration.returnType, expectedType, mv);

            return tracker.hang(declaration.returnType);
        } else {
            final TypeDeclarationNode caller = TypeRecognize.getTypeDeclaration(funcCallNode.callerType, scope);
            if (caller == null) {
                throw new RuntimeException("Caller not found in scope: " + funcCallNode.callerType);
            }

            final List<FunctionDeclarationNode> methods = caller.getAllMethods(scope);

            final boolean isInterface = caller instanceof InterfaceNode;

            final FunctionDeclarationNode declaration = ParameterTraverse.findMethodByArguments(
                scope,
                methods,
                funcCallNode.name,
                funcCallNode.arguments
            );
            final String descriptor = FunctionGenerator.getDescriptor(declaration, scope);
            final boolean isStatic = declaration.modifiers.contains("static");

            if (!isStatic) {
                ExpressionGenerator.generate(
                    funcCallNode.caller,
                    mv,
                    vm,
                    file,
                    new FlowType(funcCallNode.callerType, false, false)
                );
            }

            processFunctionArguments(
                declaration.parameters,
                funcCallNode.arguments,
                mv,
                vm,
                file
            );

            final int callType = isInterface ? Opcodes.INVOKEINTERFACE
                : isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
            final String fqCallerName = FQNameMapper.getFQName(funcCallNode.callerType, scope);

            mv.visitMethodInsn(callType, fqCallerName, funcCallNode.name, descriptor, isInterface);

            BoxMapper.boxIfNeeded(declaration.returnType, expectedType, mv);

            return tracker.hang(declaration.returnType);
        }
    }

    private static FlowType generateFieldReference(FieldReferenceNode refNode, Scope scope, FileWrapper file, MethodVisitor mv, VariableManager vm, StackTracker tracker, FlowType expectedType) {
        final String holderFQName = FQNameMapper.getFQName(refNode.holderType.name, scope);
        final String descriptor = FQNameMapper.getJVMName(refNode.type, scope);

        if (refNode.type.shouldBePrimitive()) refNode.type.isPrimitive = true;

        if (refNode.isStatic)
            mv.visitFieldInsn(Opcodes.GETSTATIC, holderFQName, refNode.name, descriptor);
        else {
            generate(refNode.holder, mv, vm, file, refNode.holderType);

            mv.visitFieldInsn(Opcodes.GETFIELD, holderFQName, refNode.name, descriptor);
        }

        BoxMapper.boxIfNeeded(refNode.type, expectedType, mv);

        return tracker.hang(refNode.type);
    }

    public static FlowType generateConstructorCall(ObjectNode objNode, Scope scope, FileWrapper file, VariableManager vm, MethodVisitor mv) {
        if (objNode.className.equals("Void")) {
            return new FlowType("Void", false, true);
        }

        final String fqObjectName = FQNameMapper.getFQName(objNode.className, scope);

        final ClassDeclarationNode objClass = TypeRecognize.getClass(objNode.className, scope);
        if (objClass == null) {
            throw new RuntimeException("Class " + objNode.className + " was not found");
        }

        ConstructorNode constructorNode = null;
        for (final ConstructorNode currentConstructor : objClass.constructors) {
            if (ParameterTraverse.compareParametersWithArguments(scope, currentConstructor.parameters, objNode.arguments)) {
                constructorNode = currentConstructor;
            }
        }

        if (constructorNode == null) {
            throw new RuntimeException("Constructor " + objNode.className + " was not found");
        }

        final String constructorDescriptor = FunctionGenerator.getDescriptor(
            constructorNode.parameters,
            new FlowType("Void", false, true),
            scope
        );

        for (ArgumentNode arg : objNode.arguments) {
            generate(arg.value.expression, mv, vm, file, arg.type);
        }

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, fqObjectName, "<init>", constructorDescriptor, false);

        return new FlowType(objNode.className, false, false);
    }

    private static FlowType generateObjectInstantiation(ObjectNode objNode, Scope scope, FileWrapper file, VariableManager vm, MethodVisitor mv, StackTracker tracker) {
        final String fqObjectName = FQNameMapper.getFQName(objNode.className, scope);
        mv.visitTypeInsn(Opcodes.NEW, fqObjectName);
        mv.visitInsn(Opcodes.DUP);

        return tracker.hang(generateConstructorCall(objNode, scope, file, vm, mv));
    }

    private static FlowType generateUnary(UnaryOperatorNode unaryExpression, Scope scope, FileWrapper file, MethodVisitor mv, VariableManager vm, StackTracker tracker, FlowType expectedType) {
        if (unaryExpression.operator.equals("!!"))
            return generateDoubleBang(unaryExpression, file, mv, vm, tracker, expectedType);

        final FlowType actualType = generate(unaryExpression.operand, mv, vm, file, null);
        if (actualType == null) {
            throw new IllegalArgumentException("Could not determine type");
        }

        boolean isPostfix = unaryExpression.isPostfix;
        boolean isIncrement = unaryExpression.operator.equals("++");

        if (unaryExpression.operand instanceof VariableReferenceNode variableReferenceNode) {
            int varIndex = vm.getVariableIndex(variableReferenceNode.variable);

            switch (actualType.toString()) {
                case "int", "short", "byte" -> {
                    int delta = isIncrement ? 1 : -1;

                    if (isPostfix) {
                        mv.visitVarInsn(Opcodes.ILOAD, varIndex);
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitIincInsn(varIndex, delta);
                    } else {
                        mv.visitIincInsn(varIndex, delta);
                        mv.visitVarInsn(Opcodes.ILOAD, varIndex);
                    }
                }
                case "long" -> {
                    if (isPostfix) {
                        mv.visitVarInsn(Opcodes.LLOAD, varIndex);
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitInsn(Opcodes.LCONST_1);
                        mv.visitInsn(isIncrement ? Opcodes.LADD : Opcodes.LSUB);
                        mv.visitVarInsn(Opcodes.LSTORE, varIndex);
                    } else {
                        mv.visitInsn(Opcodes.LCONST_1);
                        mv.visitInsn(isIncrement ? Opcodes.LADD : Opcodes.LSUB);
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitVarInsn(Opcodes.LSTORE, varIndex);
                    }
                }
                case "float" -> {
                    if (isPostfix) {
                        mv.visitVarInsn(Opcodes.FLOAD, varIndex);
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitLdcInsn(1.0f);
                        mv.visitInsn(isIncrement ? Opcodes.FADD : Opcodes.FSUB);
                        mv.visitVarInsn(Opcodes.FSTORE, varIndex);
                    } else {
                        mv.visitLdcInsn(1.0f);
                        mv.visitInsn(isIncrement ? Opcodes.FADD : Opcodes.FSUB);
                        mv.visitInsn(Opcodes.DUP);
                        mv.visitVarInsn(Opcodes.FSTORE, varIndex);
                    }
                }
                case "double" -> {
                    if (isPostfix) {
                        mv.visitVarInsn(Opcodes.DLOAD, varIndex);
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitLdcInsn(1.0);
                        mv.visitInsn(isIncrement ? Opcodes.DADD : Opcodes.DSUB);
                        mv.visitVarInsn(Opcodes.DSTORE, varIndex);
                    } else {
                        mv.visitLdcInsn(1.0);
                        mv.visitInsn(isIncrement ? Opcodes.DADD : Opcodes.DSUB);
                        mv.visitInsn(Opcodes.DUP2);
                        mv.visitVarInsn(Opcodes.DSTORE, varIndex);
                    }
                }
            }
        } else if (unaryExpression.operand instanceof FieldReferenceNode fieldReferenceNode) {
            boolean isStatic = fieldReferenceNode.holder == null;
            final String fieldOwner = FQNameMapper.getFQName(fieldReferenceNode.holderType.name, file.scope());
            final String fieldDescriptor = FQNameMapper.getJVMName(fieldReferenceNode.type, file.scope());

            if (!isStatic) {
                generate(fieldReferenceNode.holder, mv, vm, file, expectedType);
            }

            mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, fieldOwner, fieldReferenceNode.name, fieldDescriptor);

            switch (actualType.toString()) {
                case "int" -> {
                    if (isPostfix) {
                        mv.visitInsn(Opcodes.DUP);
                    }
                    mv.visitInsn(Opcodes.ICONST_1);
                    mv.visitInsn(isIncrement ? Opcodes.IADD : Opcodes.ISUB);

                    if (!isPostfix) {
                        mv.visitInsn(Opcodes.DUP);
                    }
                }
                case "long" -> {
                    if (isPostfix) {
                        mv.visitInsn(Opcodes.DUP2);
                    }
                    mv.visitInsn(Opcodes.LCONST_1);
                    mv.visitInsn(isIncrement ? Opcodes.LADD : Opcodes.LSUB);

                    if (!isPostfix) {
                        mv.visitInsn(Opcodes.DUP2);
                    }
                }
                case "float" -> {
                    if (isPostfix) {
                        mv.visitInsn(Opcodes.DUP);
                    }
                    mv.visitLdcInsn(1.0f);
                    mv.visitInsn(isIncrement ? Opcodes.FADD : Opcodes.FSUB);

                    if (!isPostfix) {
                        mv.visitInsn(Opcodes.DUP);
                    }
                }
                case "double" -> {
                    if (isPostfix) {
                        mv.visitInsn(Opcodes.DUP2);
                    }
                    mv.visitLdcInsn(1.0);
                    mv.visitInsn(isIncrement ? Opcodes.DADD : Opcodes.DSUB);

                    if (!isPostfix) {
                        mv.visitInsn(Opcodes.DUP2);
                    }
                }
            }

            if (!isStatic) {
                mv.visitInsn(Opcodes.SWAP);
            }

            mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, fieldOwner, fieldReferenceNode.name, fieldDescriptor);
        }

        BoxMapper.boxIfNeeded(actualType, expectedType, mv);

        return tracker.hang(actualType);
    }

    private static FlowType generateDoubleBang(UnaryOperatorNode unaryExpression, FileWrapper file, MethodVisitor mv, VariableManager vm, StackTracker tracker, FlowType expectedType) {
        FlowType type = generate(unaryExpression.operand, mv, vm, file, expectedType);

        if (expectedType.isPrimitive) return type;

        // TODO: if null throw

        return type;
    }

    private static FlowType generateLiteral(LiteralNode literalNode, MethodVisitor mv, StackTracker tracker, FlowType expectedType) {
        return tracker.hang(generateLiteral(literalNode, mv, expectedType));
    }

    public static FlowType generateLiteral(LiteralNode literalNode, MethodVisitor mv, FlowType expectedType) {
        if (literalNode instanceof VoidLiteralNode) {
            return new FlowType("Void", false, true);
        } else if (literalNode instanceof BooleanLiteralNode booleanLiteralNode) {
            mv.visitInsn(booleanLiteralNode.value ? Opcodes.ICONST_1 : Opcodes.ICONST_0);

            return boxLiteralIfNeeded("Bool", expectedType, mv);
        } else if (literalNode instanceof IntegerLiteralNode intLiteralNode) {
            int value = intLiteralNode.value;
            if (value >= -1 && value <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + value);
            } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.SIPUSH, value);
            } else {
                mv.visitLdcInsn(value);
            }

            return boxLiteralIfNeeded("Int", expectedType, mv);
        } else if (literalNode instanceof FloatLiteralNode floatLiteralNode) {
            float value = floatLiteralNode.value;
            if (value == 0.0f) {
                mv.visitInsn(Opcodes.FCONST_0);
            } else if (value == 1.0f) {
                mv.visitInsn(Opcodes.FCONST_1);
            } else if (value == 2.0f) {
                mv.visitInsn(Opcodes.FCONST_2);
            } else {
                mv.visitLdcInsn(value);
            }

            return boxLiteralIfNeeded("Float", expectedType, mv);
        } else if (literalNode instanceof DoubleLiteralNode doubleLiteralNode) {
            double value = doubleLiteralNode.value;
            if (value == 0.0) {
                mv.visitInsn(Opcodes.DCONST_0);
            } else if (value == 1.0) {
                mv.visitInsn(Opcodes.DCONST_1);
            } else {
                mv.visitLdcInsn(value);
            }

            return boxLiteralIfNeeded("Double", expectedType, mv);
        } else if (literalNode instanceof LongLiteralNode longLiteralNode) {
            long value = longLiteralNode.value;
            if (value == 0L) {
                mv.visitInsn(Opcodes.LCONST_0);
            } else if (value == 1L) {
                mv.visitInsn(Opcodes.LCONST_1);
            } else {
                mv.visitLdcInsn(value);
            }

            return boxLiteralIfNeeded("Long", expectedType, mv);
        } else if (literalNode instanceof CharLiteralNode charLiteralNode) {
            char value = charLiteralNode.value;
            if (value <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + value);
            } else if (value <= Byte.MAX_VALUE) {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            } else {
                mv.visitLdcInsn((int) value);
            }

            return boxLiteralIfNeeded("Char", expectedType, mv);
        } else if (literalNode instanceof StringLiteralNode stringLiteralNode) {
            mv.visitTypeInsn(Opcodes.NEW, "flow/String");
            mv.visitInsn(Opcodes.DUP);

            mv.visitLdcInsn(stringLiteralNode.getValue());

            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "flow/String", "<init>", "(Ljava/lang/String;)V", false);

            return new FlowType("String", false, false);
        }

        mv.visitLdcInsn(literalNode.getValue());
        return null;
    }

    private static FlowType boxLiteralIfNeeded(String name, FlowType expectedType, MethodVisitor mv) {
        final FlowType type = new FlowType(name, false, true);
        if (BoxMapper.needBoxing(type, expectedType)) {
            BoxMapper.box(type, mv);
        }

        return type;
    }

    private static void processFunctionArguments(
        List<ParameterNode> expectedParams,
        List<ArgumentNode> providedArgs,
        MethodVisitor mv,
        VariableManager vm,
        FileWrapper file
    ) {
        for (int i = 0; i < expectedParams.size(); i++) {
            ParameterNode param = expectedParams.get(i);
            FlowType expectedArgType = param.type;

            if (i < providedArgs.size()) {
                ArgumentNode arg = providedArgs.get(i);
                ExpressionGenerator.generate(arg.value.expression, mv, vm, file, expectedArgType);
            } else if (param.defaultValue != null) {
                ExpressionGenerator.generate(param.defaultValue.expression, mv, vm, file, expectedArgType);
            }
        }
    }

    private static class StackTracker {
        private int hangingValues;
        private final MethodVisitor mv;

        public StackTracker(MethodVisitor mv) {
            hangingValues = 0;

            this.mv = mv;
        }

        public FlowType hang(FlowType type) {
            hang(getTypeSize(type));
            return type;
        }

        public void hang(int amount) {
            hangingValues += amount;
        }

        public void cleanStack() {
            for (int i = 0; i < hangingValues; i++) {
                mv.visitInsn(Opcodes.POP);
            }
        }

        private static int getTypeSize(FlowType type) {
            if (type == null) return 0;
            if (!type.isPrimitive) return 1;

            return switch (type.toString()) {
                case "long", "double" -> 2;
                case "void" -> 0;
                default -> 1;
            };
        }
    }
}
