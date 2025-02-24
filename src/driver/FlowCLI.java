package driver;

import compiler.build_system.BuildSystem;
import compiler.library_loader.LibLoader;
import compiler.packer.PackerFacade;
import logger.Logger;
import logger.LoggerFacade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FlowCLI {
    private static final Logger logger = LoggerFacade.getLogger();
    private static String projectPath = "./";
    private static boolean verbose = false;

    public static void parseArguments(String[] args) throws CLIException, IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        Deque<String> argQueue = new ArrayDeque<>(Arrays.asList(args));
        Deque<String> flagQueue = new ArrayDeque<>();
        String command = Objects.requireNonNull(argQueue.poll());

        String outputFileName = "";
        boolean shouldRebuild = false;
        while (!argQueue.isEmpty()) {
            String arg = argQueue.poll();
            switch (arg) {
                case "-v", "--verbose" -> verbose = true;
                case "-p", "--project" -> {
                    StringBuilder path = new StringBuilder(requireValue("project (-p)", argQueue));
                    if (!path.toString().endsWith("/")) {
                        path.append("/");
                    }
                    projectPath = path.toString();
                }
                case "-o" -> {
                    if (!command.equals("pack")) {
                        throw new CLIException("Can't use the flag: '-o' here");
                    }
                    outputFileName = arg;
                } case "-c" -> {
                    if (!command.equals("run")) {
                        throw new CLIException("Can't use the flag: '-c' here");
                    }
                    shouldRebuild = true;
                }
                default -> flagQueue.add(arg);
            }
        }

        switch (command) {
            case "build" -> build();
            case "rebuild" -> rebuild();
            case "clean" -> clean();
            case "run" -> run(flagQueue, shouldRebuild);
            case "pack" -> pack(outputFileName);
            case "--help" -> printUsage();
            case "--version" -> printVersion();
            default -> throw new CLIException("Unknown command/option: " + command);
        }
    }

    private static void build() throws CLIException {
        long start = System.currentTimeMillis();

        BuildSystem buildSystem = new BuildSystem(
            "src",
            loadLibraries(),
            projectPath
        );

        boolean success = buildSystem.build();
        if (!success) {
            throw new CLIException("Build failed");
        }

        long end = System.currentTimeMillis();

        if (verbose) {
            logger.log(Logger.Severity.INFO, "Build successful! (Time: " + (end - start) + "ms)");
        } else {
            logger.log(Logger.Severity.INFO, "Build successful!");
        }
    }


    private static void rebuild() throws CLIException, IOException {
        clean();
        build();
    }

    private static void run(Deque<String> args, boolean shouldRebuild) throws CLIException, IOException {
        String fileName = args.isEmpty() ? "output.jar" : args.poll();

        if (shouldRebuild) {
            rebuild();
            pack(fileName);
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", projectPath + fileName);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LoggerFacade.error("Error running project: " + e.getMessage());
        }
    }

    private static void pack(String outputJar) throws CLIException {
        if (outputJar.isEmpty()) {
            outputJar = "output.jar";
        } else if (!outputJar.endsWith(".jar")) {
            outputJar += ".jar";
        }

        try {
            PackerFacade.pack(outputJar, projectPath + "build", List.of(loadLibraries().libFiles()));
            logger.log(Logger.Severity.INFO, "Project packed into: " + outputJar);
        } catch (IOException e) {
            throw new CLIException("Packaging failed: " + e.getMessage());
        }
    }

    private static void clean() throws IOException {
        Path buildDir = Path.of(projectPath, "build");

        if (!Files.exists(buildDir)) {
            return;
        }

        try (Stream<Path> pathStream = Files.walk(buildDir)) {
            pathStream
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        if (verbose) {
                            logger.log(Logger.Severity.INFO, "Deleted: " + path);
                        }
                    } catch (IOException e) {
                        logger.log(Logger.Severity.WARNING, "Failed to delete: " + path);
                    }
                });
        }
    }

    private static LibLoader.LibOutput loadLibraries() throws CLIException {
        try {
            return LibLoader.loadLibraries(projectPath + "libs");
        } catch (Exception e) {
            throw new CLIException("Library loading failed: " + e.getMessage());
        }
    }

    private static String requireValue(String name, Deque<String> args) throws CLIException {
        if (args.isEmpty()) {
            throw new CLIException("Missing value for " + name);
        }

        return args.poll();
    }

    public static void printUsage() {
        logger.log(Logger.Severity.INFO,
        """
            Flow Language Compiler
            Usage: flow [COMMAND] [OPTIONS]
            
            Commands:
              build      Compile source files
              run        Build and execute program
              pack       Package into JAR file
              test       Run tests
              clean      Remove build artifacts
            
            Options:
              -p, --project PATH  Set project directory
              -v, --verbose       Show detailed output
              -c                  Clean build before running
              --help              Show full help
              --version           Show compiler version
            """);
    }

    private static void printVersion() {
        logger.log(Logger.Severity.INFO, "Flow v0.1.0");
    }
}