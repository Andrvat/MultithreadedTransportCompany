import utilities.GoodsConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArrivalStation {
    private static final Logger logger = Logger.getLogger(ArrivalStation.class.getName());

    private final ArrayList<GoodsStorage> storages = new ArrayList<>();
    private final ArrayList<StationsRailwayTrack> railwayTracks = new ArrayList<>();

    private final ArrivalStationDepot depot;

    public ArrivalStation(ConfiguratorManager configuratorManager) throws IOException {
        depot = new ArrivalStationDepot(configuratorManager);
        logger.log(Level.INFO, "Arrival station depot was successfully created");

        initializeGoodsStorages(configuratorManager);
        logger.log(Level.INFO, "Goods storages in arrival station were successfully initialized");

        initializeStationsRailwayTracks(configuratorManager);
        logger.log(Level.INFO, "Stations railway tracks in arrival station were successfully initialized");

    }

    private void initializeGoodsStorages(ConfiguratorManager configuratorManager) {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            storages.add(GoodsStorage.builder()
                    .storedGoodConfigs(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName))
                    .storageId(UUID.randomUUID().toString())
                    .storedGoodName(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName).getProperty("name"))
                    .build());
        }
    }

    private void initializeStationsRailwayTracks(ConfiguratorManager configuratorManager) throws IOException {
        int totalTracksNumber = configuratorManager.getCompanyConfigurator().getArrivalRailwayTracksNumber();
        for (int i = 0; i < totalTracksNumber; i++) {
            railwayTracks.add(StationsRailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }
    }

    public GoodsStorage getStorageByStoredGoodName(String goodName) {
        for (GoodsStorage storage : storages) {
            if (storage.getStoredGoodName().equals(goodName)) {
                return storage;
            }
        }
        logger.log(Level.SEVERE, "Storage with " + goodName + " was not found. Null value returned");
        return null;
    }
}
