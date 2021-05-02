
import utilities.GoodsConfigurator;
import utilities.TransportCompanyConfigurator;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
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

    public TransportCompanyLauncher() throws IOException, InvalidNameException {
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

    public void initializeGoodsFactories() throws InvalidNameException {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
        int currentId = 0;
        for (String goodName : goodsList) {
            Properties goodData = configuratorManager.getGoodsConfigurator().getDataAboutGoodByName(goodName);
            String forGoodFactoriesNumber = goodData.getProperty("factoriesNumber");
            if (forGoodFactoriesNumber == null) {
                logger.log(Level.SEVERE, "There is no factoriesNumber field in the *.properties file");
                throw new InvalidNameException();
            }

            for (int i = 0; i < Integer.parseInt(forGoodFactoriesNumber); i++) {
                goodsFactories.add(GoodsFactory.builder()
                        .manufacturedGoodName(goodName)
                        .manufacturedGoodConfigs(goodData)
                        .associateStorage(departureStation.getAssociateGoodsStorageByName(goodName))
                        .factoryId(currentId)
                        .build());

                currentId++;
            }
        }
    }

    public void initializeGoodsConsumers() {
        for (int i = 0; i < INIT_CONSUMERS_NUMBER; i++) {
            goodsConsumers.add(GoodsConsumer.builder()
                    .arrivalStation(arrivalStation)
                    .goodsConfigurator(configuratorManager.getGoodsConfigurator())
                    .consumerId(i)
                    .build());
        }
    }

    public void launch() throws IOException {
        for (GoodsFactory factory : goodsFactories) {
            factory.start();
        }

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.start();
        }
    }

    public void terminate() {
        for (GoodsFactory factory : goodsFactories) {
            factory.interrupt();
        }

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.interrupt();
        }
    }
}
