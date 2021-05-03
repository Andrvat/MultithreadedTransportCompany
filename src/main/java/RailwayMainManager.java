import lombok.Builder;

@Builder
public class RailwayMainManager extends Thread {
    private final TransportCompanyLauncher companyLauncher;

    private final TrainInformationLog informationLog;

    @Override
    public void run() {
        companyLauncher.getDepartureStation().startRunningTrainsInUse(informationLog);
    }
}
