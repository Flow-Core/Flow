package compiler.packer;

import java.io.IOException;

public interface Packer {
    void pack(String outputFileName, String buildPath, String mainClassFQName) throws IOException;
}
