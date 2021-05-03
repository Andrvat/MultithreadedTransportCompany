import utilities.GoodsConfigurator;

import javax.naming.InvalidNameException;
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

    private void initializeGoodsFactories() throws InvalidNameException {
        ArrayList<String> goodsList = GoodsConfigurator.getGoodsList();
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

    public ArrayList<GoodsFactory> getGoodsFactories() {
        return goodsFactories;
    }

    public ArrayList<GoodsConsumer> getGoodsConsumers() {
        return goodsConsumers;
    }

    public DepartureStation getDepartureStation() {
        return departureStation;
    }

    public ArrivalStation getArrivalStation() {
        return arrivalStation;
    }

    public DirectedRailwayTracksManager getRailwayTracksManager() {
        return railwayTracksManager;
    }

    public ConfiguratorManager getConfiguratorManager() {
        return configuratorManager;
    }

    public void launch() throws IOException {
        for (GoodsFactory factory : goodsFactories) {
            factory.start();
        }

        TrainInformationLog informationLog = TrainInformationLog.builder()
                .arrivalStation(arrivalStation)
                .departureStation(departureStation)
                .railwayTracksManager(railwayTracksManager)
                .build();

        mainManager = RailwayMainManager.builder()
                .companyLauncher(this)
                .informationLog(informationLog)
                .build();
        mainManager.start();

        /**
         * TODO: запустить в отдельном потоке RailwayMainManager, который раскидает дальнейшние задачи
         * - заставить депо города-отрпавителя создавать поезда
         * - заставить поезда занимать пути на станции
         * - заставить поезда загружаться
         * - заставить поезда ехать
         * - и т.д.. В обратную сторону симметрично...
         */

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.start();
        }
    }

    public void terminate() {
        for (GoodsFactory factory : goodsFactories) {
            factory.interrupt();
        }

        mainManager.interrupt();

        for (GoodsConsumer consumer : goodsConsumers) {
            consumer.interrupt();
        }
    }
}
