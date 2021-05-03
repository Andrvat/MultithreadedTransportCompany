import lombok.Builder;
import utilities.LoggerPrintAssistant;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Builder
public class Train extends Thread {
    private static final Logger logger = Logger.getLogger(Train.class.getName());

    private final Properties trainProperties;

    private final TrainInformationLog informationLog;

    private final ArrayList<Good> transportedGoods = new ArrayList<>();

    public Properties getTrainProperties() {
        return trainProperties;
    }

    @Override
    public void run() {
        try {
            StationsRailwayTrack track = informationLog.getDepartureStation().getFreeStationRailwayTrack();
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for free track", exception);
            return;
        }

        try {
            informationLog.getDepartureStation().loadTrainWithGoodsByCapacities(this);
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for goods in storages", exception);
            return;
        }

    }

    public void loadGood(Good good) {
        transportedGoods.add(good);
    }
}
