import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;
import utilities.TrainsConfigurator;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartureStationDepot {
    private static final Logger logger = Logger.getLogger(DepartureStationDepot.class.getName());

    private final TrainsConfigurator trainsConfigurator;
    private TrainInformationManifest informationManifest;

    private final ConcurrentMap<String, Train> depotTrains = new ConcurrentHashMap<>();
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

                String trainNewId = UUID.randomUUID().toString();
                Train train = Train.builder()
                        .trainProperties(trainProperties)
                        .informationManifest(informationManifest)
                        .totalTimeInTrips(0)
                        .trainId(trainNewId)
                        .build();
                depotTrains.putIfAbsent(trainNewId, train);

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
        removeTrainFromUse(oldTrain.getTrainId(), oldTrain);

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Send request for production of new train to replace the old one");
        launchNewTrain(oldTrain.getTrainProperties());
    }

    private synchronized void removeTrainFromUse(String trainId, Train train) {
        train.interrupt();
        depotTrains.remove(trainId);
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") + " was successfully disposed of");
    }

    public synchronized void stopDepotOperations() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(1, TimeUnit.SECONDS);
            for (Map.Entry<String, Train> train : depotTrains.entrySet()) {
                removeTrainFromUse(train.getKey(), train.getValue());
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
