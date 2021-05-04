import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;
import utilities.TrainsConfigurator;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartureStationDepot {
    private static final Logger logger = Logger.getLogger(DepartureStationDepot.class.getName());

    private final TrainsConfigurator trainsConfigurator;
    private TrainInformationManifest informationManifest;

    private final ArrayList<Train> depotTrains = new ArrayList<>();
    private final ExecutorService threadPool;

    public DepartureStationDepot(ConfiguratorManager configuratorManager) {
        trainsConfigurator = configuratorManager.getTrainsConfigurator();
        threadPool = Executors.newFixedThreadPool(TrainsConfigurator.trainsList.size());
    }

    public void startTrainProducing(TrainInformationManifest informationManifest) {
        this.informationManifest = informationManifest;

        ArrayList<String> trainsNames = TrainsConfigurator.trainsList;
        for (String train : trainsNames) {
            Properties trainProperties = trainsConfigurator.getDataAboutTrainByName(train);
            launchNewTrain(trainProperties);
        }
    }

    private void launchNewTrain(Properties trainProperties) {
        threadPool.submit(() -> {
            try {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "Start to produce new train...");
                Thread.sleep(TimeUtilities.convertSecsToMillis(Long.parseLong(trainProperties.getProperty("createTime"))));

                Train train = Train.builder()
                        .trainProperties(trainProperties)
                        .informationManifest(informationManifest)
                        .totalTimeInTrips(0)
                        .build();
                depotTrains.add(train);

                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "Train " + trainProperties.getProperty("name") + " was created. Send it on its way...");
                train.start();
            } catch (InterruptedException exception) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                        "Production of the train was halted by interrupt", exception);
            }
        });
    }

    public synchronized void replaceOldTrainToNewOne(Train oldTrain) {
        removeTrainFromUse(oldTrain);

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Send request for production of new train to replace the old one");
        launchNewTrain(oldTrain.getTrainProperties());
    }

    public synchronized void removeTrainFromUse(Train train) {
        train.interrupt();
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " was successfully disposed of");
    }

    public void stopDepotOperations() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(1, TimeUnit.SECONDS);
            for (Train train : depotTrains) {
                removeTrainFromUse(train);
            }
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "All threads independently completed successfully on the interrupt");
        } finally {
            if (!threadPool.isTerminated()) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                        "It is planned to force the termination of all threads in thread pool");
            }
            threadPool.shutdownNow();
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "All trains were successfully remove from use. The depot is closing");
        }
    }

}
