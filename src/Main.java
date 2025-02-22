import compiler.packer.JarPacker;
import compiler.packer.Packer;
import compiler.packer.PackerFacade;
import driver.CLIException;
import driver.FlowCLI;
import logger.Logger;
import logger.LoggerFacade;
import logger.impl.ColoredLogger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final Logger logger = new ColoredLogger();
        LoggerFacade.initLogger(logger);

        final Packer packer = new JarPacker();
        PackerFacade.initPacker(packer);

        try {
            FlowCLI.parseArguments(args);
        } catch (CLIException | IOException e) {
            logger.log(Logger.Severity.ERROR, e.getMessage());
            FlowCLI.printUsage();
            System.exit(1);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}