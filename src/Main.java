import compiler.packer.JarPacker;
import compiler.packer.Packer;
import compiler.packer.PackerFacade;
import driver.FlowCLI;
import logger.Logger;
import logger.LoggerFacade;
import logger.impl.ColoredLogger;

public class Main {
    public static void main(String[] args) {
        final Logger logger = new ColoredLogger();
        LoggerFacade.initLogger(logger);

        final Packer packer = new JarPacker();
        PackerFacade.initPacker(packer);

        try {
            FlowCLI.parseArguments(args);
        } catch (Exception e) {
            logger.log(Logger.Severity.ERROR, e.getMessage());
            System.exit(1);
        }
    }
}