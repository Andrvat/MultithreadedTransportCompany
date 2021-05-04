import lombok.Builder;

@Builder
public class TrainInformationManifest {
    private final DepartureStation departureStation;
    private final ArrivalStation arrivalStation;

    private final DirectedRailwayTracksManager railwayTracksManager;

    public DepartureStation getDepartureStation() {
        return departureStation;
    }

    public ArrivalStation getArrivalStation() {
        return arrivalStation;
    }

    public DirectedRailwayTracksManager getRailwayTracksManager() {
        return railwayTracksManager;
    }
}
