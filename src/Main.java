import compiler.build_system.BuildSystem;
import compiler.library_loader.LibLoader;
import compiler.packer.JarPacker;
import compiler.packer.Packer;
import compiler.packer.PackerFacade;
import logger.Logger;
import logger.LoggerFacade;
import logger.impl.ConsoleLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        final String projectPath = "E:/flow/test";

        LibLoader.LibOutput libOutput = null;
        try {
            libOutput = LibLoader.loadLibraries(projectPath + "/libs");
        } catch (Exception e) {
            System.err.println("Could not load libraries");
        }

        final Logger logger = new ConsoleLogger();
        LoggerFacade.initLogger(logger);

        final BuildSystem buildSystem = new BuildSystem("/src", libOutput, projectPath);
        if (!buildSystem.build()) {
            return;
        }

        final Packer packer = new JarPacker();
        PackerFacade.initPacker(packer);

        try {
            PackerFacade.pack(
                projectPath + "/output.jar",
                    projectPath + "/build",
                    libOutput == null ? new ArrayList<>() : Arrays.asList(libOutput.libFiles()
                )
            );
        } catch (IOException e) {
            System.err.println("Couldn't pack jar file");
        }
    }
}