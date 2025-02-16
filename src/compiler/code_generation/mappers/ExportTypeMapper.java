package compiler.code_generation.mappers;

public class ExportTypeMapper {
    public static String map(String className) {
        return switch (className) {
            case "flow.String" -> "java.lang.String";
            case "flow.Int" -> "java.lang.Integer";
            case "flow.Bool" -> "java.lang.Boolean";
            case "flow.Float" -> "java.lang.Float";
            case "flow.Double" -> "java.lang.Double";
            case "flow.Long" -> "java.lang.Long";
            case "flow.Byte" -> "java.lang.Byte";
            case "flow.Char" -> "java.lang.Character";
            case "flow.Short" -> "java.lang.Short";

            default -> className.replace(".", "/");
        };
    }
}
