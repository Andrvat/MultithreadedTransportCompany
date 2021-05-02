import handlers.QuitButtonClickHandler;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

final public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            InputStream loggerConfigFile = Main.class.getResourceAsStream("/loggerConfigs.properties");
            LogManager.getLogManager().readConfiguration(loggerConfigFile);
            logger.log(Level.INFO, "Logger was successfully redirected to XMLFormatter");
        } catch (IOException exception) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Could not load loggerConfigs.properties file", exception);
        }

        try {
            TransportCompanyLauncher companyLauncher = new TransportCompanyLauncher();
            companyLauncher.launch();
            QuitButtonClickHandler.handle();
            companyLauncher.terminate();
        } catch (IOException | InvalidNameException exception) {
            logger.log(Level.SEVERE, "Transport company could not work correctly. Emulator finished...", exception);
        }
    }
}
