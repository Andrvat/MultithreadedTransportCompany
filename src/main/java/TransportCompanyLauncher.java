import utilities.GoodsConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TransportCompanyLauncher {
    private static final Logger logger = Logger.getLogger(TransportCompanyLauncher.class.getName());

    private static final int INIT_CONSUMERS_NUMBER = 10;

    private final ArrayList<GoodsFactory> goodsFactories = new ArrayList<>();
    private final ArrayList<GoodsConsumer> goodsConsumers = new ArrayList<>();

    private final DepartureStation departureStation;
    private final ArrivalStation arrivalStation;

    private final DirectedRailwayTracksManager railwayTracksManager;

    private final ConfiguratorManager configuratorManager;
    private RailwayMainManager mainManager;

    public TransportCompanyLauncher() throws IOException {
        configuratorManager = new ConfiguratorManager();
        logger.log(Level.INFO, "Configurator manager was successfully created");

        departureStation = new DepartureStation(configuratorManager);
        arrivalStation = new ArrivalStation(configuratorManager);
        logger.log(Level.INFO, "All stations were successfully created");

        railwayTracksManager = new DirectedRailwayTracksManager(configuratorManager.getCompanyConfigurator());
        logger.log(Level.INFO, "Directed railway tracks manager was successfully created");

        initializeGoodsFactories();
        initializeGoodsConsumers();
        logger.log(Level.INFO, "Factories and consumers were successfully initialized");

    }

    private void initializeGoodsFactories() {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        for (String goodName : goodsList) {
            Properties goodData = configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName);
            for (int i = 0; i < Integer.parseInt(goodData.getProperty("factoriesNumber")); i++) {
                goodsFactories.add(GoodsFactory.builder()
                        .manufacturedGoodName(goodName)
                        .manufacturedGoodConfigs(goodData)
                        .associateStorage(departureStation.getAssociateGoodsStorageByName(goodName))
                        .factoryId(UUID.randomUUID().toString())
                        .build());
            }
        }
    }

    private void initializeGoodsConsumers() {
        for (int i = 0; i < INIT_CONSUMERS_NUMBER; i++) {
            goodsConsumers.add(GoodsConsumer.builder()
                    .arrivalStation(arrivalStation)
                    .goodsConfigurator(configuratorManager.getGoodsConfigurator())
                    .consumerId(UUID.randomUUID().toString())
                    .build());
        }
    }

    public void launch() {
        for (GoodsFactory factory : goodsFactories) {
            factory.start();
        }

        TrainInformationManifest informationManifest = TrainInformationManifest.builder()
                .arrivalStation(arrivalStation)
                .departureStation(departureStation)
                .railwayTracksManager(railwayTracksManager)
                .build();

        mainManager = RailwayMainManager.builder()
                .informationManifest(informationManifest)
                .build();
        mainManager.start();

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.start();
        }
    }

    public void terminate() {
        for (GoodsFactory factory : goodsFactories) {
            factory.interrupt();
        }

        mainManager.terminate();

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.interrupt();
        }
    }
}
