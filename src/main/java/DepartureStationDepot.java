import utilities.TrainsConfigurator;

public class DepartureStationDepot {
    private final TrainsConfigurator trainsConfigurator;

    public DepartureStationDepot(ConfiguratorManager configuratorManager) {
        trainsConfigurator = configuratorManager.getTrainsConfigurator();
    }
}
