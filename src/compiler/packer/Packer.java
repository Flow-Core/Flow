package compiler.packer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Packer {
    void pack(String outputFileName, String buildPath, String mainClassFQName, List<File> libJars) throws IOException;
}
