import lombok.Builder;
import utilities.LoggerPrintAssistant;

import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class RailwayMainManager extends Thread {
    private static final Logger logger = Logger.getLogger(RailwayMainManager.class.getName());

    private final TrainInformationManifest informationManifest;

    @Override
    public void run() {
       informationManifest.getDepartureStation().startRunningTrainsInUse(informationManifest);
    }

    public void terminate() {
        informationManifest.getDepartureStation().terminateDepot();
        this.interrupt();
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Full railway system was successfully stopped");
    }
}
