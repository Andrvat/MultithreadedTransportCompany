import lombok.Builder;
import utilities.LoggerPrintAssistant;

import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class RailwayMainManager extends Thread {
    private static final Logger logger = Logger.getLogger(RailwayMainManager.class.getName());

    private final TrainInformationLog informationLog;

    @Override
    public void run() {
       informationLog.getDepartureStation().startRunningTrainsInUse(informationLog);
    }

    public void terminate() {
        informationLog.getDepartureStation().terminateDepot();
        this.interrupt();
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Full railway system was successfully stopped");
    }
}
