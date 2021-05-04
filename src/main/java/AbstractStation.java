import utilities.GoodsConfigurator;
import utilities.LoggerPrintAssistant;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStation {
    private static final Logger logger = Logger.getLogger(AbstractStation.class.getName());

    private final ConcurrentMap<String, GoodsStorage> storages = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<RailwayTrack> freeRailwayTracks = new ConcurrentLinkedQueue<>();

    protected void initializeGoodsStorages(ConfiguratorManager configuratorManager) {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            storages.putIfAbsent(goodName, GoodsStorage.builder()
                    .storedGoodConfigs(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName))
                    .storageId(UUID.randomUUID().toString())
                    .storedGoodName(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName).getProperty("name"))
                    .build());
        }
    }

    protected void initializeStationsRailwayTracks(int totalTracksNumber) {
        for (int i = 0; i < totalTracksNumber; i++) {
            freeRailwayTracks.add(RailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }
    }

    public GoodsStorage getAssociateGoodsStorageByName(String goodName) {
        return storages.get(goodName);
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

}

