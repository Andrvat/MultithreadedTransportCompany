import utilities.GoodsConfigurator;
import utilities.LoggerPrintAssistant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArrivalStation {
    private static final Logger logger = Logger.getLogger(ArrivalStation.class.getName());

    private final ConcurrentMap<String, GoodsStorage> storages = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<RailwayTrack> freeRailwayTracks = new ConcurrentLinkedQueue<>();

    public ArrivalStation(ConfiguratorManager configuratorManager) throws IOException {
        initializeGoodsStorages(configuratorManager);
        logger.log(Level.INFO, "Goods storages in arrival station were successfully initialized");

        initializeStationsRailwayTracks(configuratorManager);
        logger.log(Level.INFO, "Stations railway tracks in arrival station were successfully initialized");

    }

    private void initializeGoodsStorages(ConfiguratorManager configuratorManager) {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            storages.putIfAbsent(goodName, GoodsStorage.builder()
                    .storedGoodConfigs(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName))
                    .storageId(UUID.randomUUID().toString())
                    .storedGoodName(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName).getProperty("name"))
                    .build());
        }
    }

    private void initializeStationsRailwayTracks(ConfiguratorManager configuratorManager) throws IOException {
        int totalTracksNumber = configuratorManager.getCompanyConfigurator().getArrivalRailwayTracksNumber();
        for (int i = 0; i < totalTracksNumber; i++) {
            freeRailwayTracks.add(RailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }
    }

    public RailwayTrack getFreeStationRailwayTrack() throws InterruptedException {
        synchronized (freeRailwayTracks) {
            while (freeRailwayTracks.isEmpty()) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "All tracks occupied. Waiting for free some one...");
                freeRailwayTracks.wait();
            }

            RailwayTrack track = freeRailwayTracks.remove();
            track.occupyTrack();

            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Track " + track.getTrackId() + " was occupied");

            freeRailwayTracks.notifyAll();
            return track;
        }
    }

    public void freeOccupiedStationRailwayTrack(RailwayTrack track) {
        synchronized (freeRailwayTracks) {
            freeRailwayTracks.add(track);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Track " + track.getTrackId() + " was returned to station");
            freeRailwayTracks.notifyAll();
        }
    }

    public void unloadTrainWithGoodsToAssociateStorages(Train train) throws InterruptedException {
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " is ready to unload goods...");

        while (train.areThereAnyUnloadedGoodsLeft()) {
            Good nextGood = train.unloadNextGood();
            GoodsStorage storageForLoad = getStorageByStoredGoodName(nextGood.getGoodName());
            storageForLoad.loadGood(nextGood);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Train " + train.getTrainProperties().getProperty("name") + " unloaded "
                            + nextGood.getGoodName() + " to storage " + storageForLoad.getStorageId());
        }

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " unloaded all goods");
    }

    public GoodsStorage getStorageByStoredGoodName(String goodName) {
        return storages.get(goodName);
    }
}
