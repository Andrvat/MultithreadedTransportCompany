import utilities.GoodsConfigurator;
import utilities.TrainsConfigurator;
import utilities.TransportCompanyConfigurator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfiguratorManager {
    private static final Logger logger = Logger.getLogger(ConfiguratorManager.class.getName());

    private final TransportCompanyConfigurator companyConfigurator;
    private final GoodsConfigurator goodsConfigurator;
    private final TrainsConfigurator trainsConfigurator;

    public ConfiguratorManager() throws IOException {
        companyConfigurator = new TransportCompanyConfigurator();
        goodsConfigurator = new GoodsConfigurator();
        trainsConfigurator = new TrainsConfigurator();
        logger.log(Level.INFO, "ConfiguratorManager constructor finished. All configs were successfully created");
    }

    public TransportCompanyConfigurator getCompanyConfigurator() {
        return companyConfigurator;
    }

    public GoodsConfigurator getGoodsConfigurator() {
        return goodsConfigurator;
    }

    public TrainsConfigurator getTrainsConfigurator() {
        return trainsConfigurator;
    }
}
