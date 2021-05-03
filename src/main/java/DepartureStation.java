import utilities.GoodsConfigurator;
import utilities.LoggerPrintAssistant;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartureStation {
    private static final Logger logger = Logger.getLogger(DepartureStation.class.getName());

    private final HashMap<String, GoodsStorage> storages = new LinkedHashMap<>();
    private final Queue<StationsRailwayTrack> freeRailwayTracks = new LinkedList<>();

    private final DepartureStationDepot depot;

    public DepartureStation(ConfiguratorManager configuratorManager) throws IOException {
        depot = new DepartureStationDepot(configuratorManager);
        logger.log(Level.INFO, "Departure station depot was successfully created");

        initializeGoodsStorages(configuratorManager);
        logger.log(Level.INFO, "Goods storages in departure station were successfully initialized");

        initializeStationsRailwayTracks(configuratorManager);
        logger.log(Level.INFO, "Stations railway tracks in departure station were successfully initialized");

    }

    private void initializeGoodsStorages(ConfiguratorManager configuratorManager) {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            storages.put(goodName, GoodsStorage.builder()
                    .storedGoodConfigs(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName))
                    .storageId(UUID.randomUUID().toString())
                    .storedGoodName(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName).getProperty("name"))
                    .build());
        }
    }

    private void initializeStationsRailwayTracks(ConfiguratorManager configuratorManager) throws IOException {
        int totalTracksNumber = configuratorManager.getCompanyConfigurator().getDepartureRailwayTracksNumber();
        for (int i = 0; i < totalTracksNumber; i++) {
            freeRailwayTracks.add(StationsRailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }
    }

    public GoodsStorage getAssociateGoodsStorageByName(String goodName) {
        return storages.get(goodName);
    }

    public void startRunningTrainsInUse(TrainInformationLog informationLog) {
        depot.startTrainProducing(informationLog);
    }

    public StationsRailwayTrack getFreeStationRailwayTrack() throws InterruptedException {
        synchronized (freeRailwayTracks) {
            while (freeRailwayTracks.isEmpty()) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "All tracks occupied. Waiting for free some one...");
                freeRailwayTracks.wait();
            }

            StationsRailwayTrack track = freeRailwayTracks.remove();
            track.occupyTrack();

            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Track " + track.getTrackId() + " was occupied");

            freeRailwayTracks.notifyAll();
            return track;
        }
    }

    public void freeOccupiedStationRailwayTrack(StationsRailwayTrack track) {
        synchronized (freeRailwayTracks) {
            freeRailwayTracks.add(track);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Track " + track.getTrackId() + " was returned to station");
            freeRailwayTracks.notifyAll();
        }
    }

    public void loadTrainWithGoodsByCapacities(Train train) throws InterruptedException {
        synchronized (storages) {
            ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
            for (String goodName : goodsList) {
                GoodsStorage storage = storages.get(goodName);
                long goodsNumberToLoad = Long.parseLong(train.getTrainProperties().getProperty(goodName + "s" + "Capacity"));
                for (int i = 0; i < goodsNumberToLoad; i++) {
                    train.loadGood(storage.unloadGood());
                }
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "Train " + train.getTrainProperties().getProperty("name") + " loaded all fitable " + goodName);
            }
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Train " + train.getTrainProperties().getProperty("name") + " was fully loaded");
            storages.notifyAll();
        }
    }
}
