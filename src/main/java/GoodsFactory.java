import lombok.Builder;
import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class GoodsFactory extends Thread {
    private static final Logger logger = Logger.getLogger(GoodsFactory.class.getName());

    private final String factoryId;

    private final String manufacturedGoodName;

    private final Properties manufacturedGoodConfigs;

    private final GoodsStorage associateStorage;

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "factory " + factoryId + " started to produce " + manufacturedGoodName);
                Thread.sleep(TimeUtilities.convertSecsToMillis(Long.parseLong(manufacturedGoodConfigs.getProperty("createTime"))));

                Good readyMadeGood = Good.builder().goodName(manufacturedGoodName).build();
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "factory " + factoryId + " finished the producing of " + manufacturedGoodName);

                associateStorage.loadGood(readyMadeGood);
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        manufacturedGoodName + " was loaded to the associate storage from factory " + factoryId);
            } catch (InterruptedException exception) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                        "Factory " + factoryId + " stopped by interrupt", exception);
                return;
            }
        }
    }
}
