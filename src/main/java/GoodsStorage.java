import lombok.Builder;
import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class GoodsStorage {
    private static final Logger logger = Logger.getLogger(GoodsStorage.class.getName());

    private final String storageId;

    private final String storedGoodName;

    private final Properties storedGoodConfigs;

    private final Queue<Good> readyGoods = new LinkedList<>();

    public synchronized void loadGood(Good good) throws InterruptedException {
        int storageCapacity = Integer.parseInt(storedGoodConfigs.getProperty("departureStorageCapacity"));
        while (readyGoods.size() >= storageCapacity) {
            wait();
        }

        Thread.sleep(TimeUtilities.convertSecsToMillis(Long.parseLong(storedGoodConfigs.getProperty("loadTime"))));
        readyGoods.add(good);

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                storedGoodName + " was loaded to the storage " + storageId + ". " +
                        "Current storage's fullness = " + readyGoods.size());
        notifyAll();
    }

    public synchronized Good unloadGood() throws InterruptedException {
        while (readyGoods.isEmpty()) {
            wait();
        }

        Thread.sleep(TimeUtilities.convertSecsToMillis(Long.parseLong(storedGoodConfigs.getProperty("unloadTime"))));
        Good goodToSend = readyGoods.remove();

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                storedGoodName + " was unloaded from the storage " + storageId + ". " +
                        "Current storage's fullness = " + readyGoods.size());
        notifyAll();
        return goodToSend;
    }

    public String getStoredGoodName() {
        return storedGoodName;
    }
}
