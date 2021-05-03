import lombok.Builder;
import utilities.GoodsConfigurator;
import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class GoodsConsumer extends Thread {
    private static final Logger logger = Logger.getLogger(GoodsConsumer.class.getName());

    private final ArrivalStation arrivalStation;

    private final String consumerId;

    private final GoodsConfigurator goodsConfigurator;

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                String goodName = generateRandomGoodNameFromExisting();

                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "Consumer " + consumerId + " wants to get " + goodName);

                GoodsStorage storageToVisit = arrivalStation.getStorageByStoredGoodName(goodName);
                Good consumerGood = storageToVisit.unloadGood();
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "Consumer " + consumerId + " is using " + goodName + "...");

                Thread.sleep(TimeUtilities.convertSecsToMillis(Long.parseLong(
                        goodsConfigurator.getDataAboutGoodByName(goodName).getProperty("consumeTime"))));
            } catch (InterruptedException exception) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                        "Consumer " + consumerId + " stopped by interrupt", exception);
                return;
            }
        }
    }

    private String generateRandomGoodNameFromExisting() {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        return goodsList.get(new Random().nextInt(goodsList.size()));
    }
}
