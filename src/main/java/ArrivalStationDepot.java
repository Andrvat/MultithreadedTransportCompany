import utilities.TrainsConfigurator;

public class ArrivalStationDepot {
    private final TrainsConfigurator trainsConfigurator;

    public ArrivalStationDepot(ConfiguratorManager configuratorManager) {
        trainsConfigurator = configuratorManager.getTrainsConfigurator();
    }
}
