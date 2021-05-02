import utilities.GoodsConfigurator;
import utilities.TransportCompanyConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartureStation {
    private static final Logger logger = Logger.getLogger(DepartureStation.class.getName());

    private final HashMap<String, GoodsStorage> storages = new LinkedHashMap<>();
    private final ArrayList<StationsRailwayTracks> railwayTracks = new ArrayList<>();

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
        int currentId = 0;
        for (String goodName : goodsList) {
            storages.put(goodName, GoodsStorage.builder()
                    .storedGoodConfigs(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName))
                    .storageId(currentId)
                    .storedGoodName(configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName).getProperty("name"))
                    .build());
            currentId++;
        }
    }

    private void initializeStationsRailwayTracks(ConfiguratorManager configuratorManager) throws IOException {
        int totalTracksNumber = configuratorManager.getCompanyConfigurator().getDepartureRailwayTracksNumber();
        for (int i = 0; i < totalTracksNumber; i++) {
            railwayTracks.add(new StationsRailwayTracks());
        }
    }

    public GoodsStorage getAssociateGoodsStorageByName(String goodName) {
        return storages.get(goodName);
    }
}
