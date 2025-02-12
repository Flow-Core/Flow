package compiler.packer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarPacker implements Packer {
    private final Manifest manifest = new Manifest();

    @Override
    public void pack(String outputFileName, String buildPath, String mainClassFQName) throws IOException {
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClassFQName);

        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputFileName), manifest)) {
            File buildDir = new File(buildPath);
            if (!buildDir.exists() || !buildDir.isDirectory()) {
                throw new IllegalArgumentException("Invalid build path: " + buildPath);
            }

            addFilesToJar(jarOutputStream, buildDir, buildDir.getAbsolutePath().length() + 1);
        }
    }

    private void addFilesToJar(JarOutputStream jarOutputStream, File source, int basePathLength) throws IOException {
        if (source.isDirectory()) {
            for (File file : Objects.requireNonNull(source.listFiles())) {
                addFilesToJar(jarOutputStream, file, basePathLength);
            }
        } else {
            String entryName = source.getAbsolutePath().substring(basePathLength).replace("\\", "/");
            JarEntry jarEntry = new JarEntry(entryName);
            jarEntry.setTime(source.lastModified());

            jarOutputStream.putNextEntry(jarEntry);

            try (FileInputStream fis = new FileInputStream(source)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    jarOutputStream.write(buffer, 0, bytesRead);
                }
            }

            jarOutputStream.closeEntry();
        }
    }
}