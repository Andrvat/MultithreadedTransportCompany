import utilities.LoggerPrintAssistant;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ArrivalStation extends AbstractStation {
    private static final Logger logger = Logger.getLogger(ArrivalStation.class.getName());

    public ArrivalStation(ConfiguratorManager configuratorManager) {
        initializeGoodsStorages(configuratorManager);
        logger.log(Level.INFO, "Goods storages in arrival station were successfully initialized");

        initializeStationsRailwayTracks(configuratorManager.getCompanyConfigurator().getArrivalRailwayTracksNumber());
        logger.log(Level.INFO, "Stations railway tracks in arrival station were successfully initialized");

    }

    public void unloadTrainWithGoodsToAssociateStorages(Train train) throws InterruptedException {
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " is ready to unload goods...");

        while (train.areThereAnyUnloadedGoodsLeft()) {
            Good nextGood = train.unloadNextGood();
            GoodsStorage storageForLoad = getAssociateGoodsStorageByName(nextGood.getGoodName());
            storageForLoad.loadGood(nextGood);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Train " + train.getTrainProperties().getProperty("name") + " unloaded "
                            + nextGood.getGoodName() + " to storage " + storageForLoad.getStorageId());
        }

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " unloaded all goods");
    }
}
