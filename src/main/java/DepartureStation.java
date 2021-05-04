import utilities.GoodsConfigurator;
import utilities.LoggerPrintAssistant;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartureStation extends AbstractStation {
    private static final Logger logger = Logger.getLogger(DepartureStation.class.getName());

    private final DepartureStationDepot depot;

    public DepartureStation(ConfiguratorManager configuratorManager) {
        depot = new DepartureStationDepot(configuratorManager);
        logger.log(Level.INFO, "Departure station depot was successfully created");

        initializeGoodsStorages(configuratorManager);
        logger.log(Level.INFO, "Goods storages in departure station were successfully initialized");

        initializeStationsRailwayTracks(configuratorManager.getCompanyConfigurator().getDepartureRailwayTracksNumber());
        logger.log(Level.INFO, "Stations railway tracks in departure station were successfully initialized");

    }

    public void startRunningTrainsInUse(TrainInformationManifest informationManifest) {
        depot.startTrainProducing(informationManifest);
    }

    public void sendInfoToDepotForReplaceOldTrainToNewOne(Train oldTrain) {
        depot.replaceOldTrainToNewOne(oldTrain);
    }

    public void terminateDepot() {
        depot.stopDepotOperations();
    }

    public void loadTrainWithGoodsByCapacities(Train train) throws InterruptedException {
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " is ready to load with goods...");

        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            GoodsStorage storage = getAssociateGoodsStorageByName(goodName);
            long goodsNumberToLoad = Long.parseLong(train.getTrainProperties().getProperty(goodName + "s" + "Capacity"));
            for (int i = 0; i < goodsNumberToLoad; i++) {
                Good goodForLoading = storage.unloadGood();
                train.loadGood(goodForLoading);
            }

            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Train " + train.getTrainProperties().getProperty("name") + " loaded all fitable " + goodName);
        }

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " was fully loaded");
    }
}
