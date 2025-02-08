package compiler.code_generation;

import semantic_analysis.files.FileWrapper;

import java.util.ArrayList;
import java.util.List;

public class CodeGeneration {
    final FileWrapper file;

    public CodeGeneration(FileWrapper file) {
        this.file = file;
    }

    public List<Byte[]> generate() {
        return new ArrayList<>();
    }
}
