package compiler.packer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class PackerFacade {
    private static Packer packer;
    private static String mainClassFQName;

    private PackerFacade() {}

    public static void initPacker(Packer packer) {
        if (PackerFacade.packer != null) {
            throw new RuntimeException("Packer was already initialized");
        }

        PackerFacade.packer = packer;
    }

    public static void setMainClassFQName(String mainClassFQName) {
        PackerFacade.mainClassFQName = mainClassFQName;
    }

    public static void pack(String outputFileName, String buildPath, List<File> libJars) throws IOException {
        packer.pack(outputFileName, buildPath, mainClassFQName, libJars);
    }
}
