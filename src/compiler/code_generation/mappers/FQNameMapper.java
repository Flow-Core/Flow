package compiler.code_generation.mappers;

public class FQNameMapper {
    public static String map(String name) {
        return name.replace('.', '/');
    }
}
