import lombok.Builder;

@Builder
public class RailwayMainManager extends Thread {
    private final TransportCompanyLauncher companyLauncher;

    @Override
    public void run() {

    }
}
