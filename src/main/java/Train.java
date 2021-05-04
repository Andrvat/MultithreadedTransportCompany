import lombok.Builder;
import utilities.LoggerPrintAssistant;

import java.io.IOException;
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
        RailwayTrack departureFreeRailwayTrack;
        try {
            departureFreeRailwayTrack = informationLog.getDepartureStation().getFreeStationRailwayTrack();
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for free departure track", exception);
            return;
        }

        try {
            informationLog.getDepartureStation().loadTrainWithGoodsByCapacities(this);
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for goods in storages", exception);
            return;
        }

        RailwayTrack forwardRailwayTrack;
        try {
            forwardRailwayTrack = informationLog.getRailwayTracksManager().getFreeForwardRailwayTrack();
            informationLog.getDepartureStation().freeOccupiedStationRailwayTrack(departureFreeRailwayTrack);
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for free forward tracks", exception);
            return;
        }

        try {
            informationLog.getRailwayTracksManager().startTrainRunningOnTrack(this, forwardRailwayTrack);
        } catch (IOException | InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting running on forward railway track", exception);
            return;
        }

        RailwayTrack arrivalFreeRailwayTrack;
        try {
            arrivalFreeRailwayTrack = informationLog.getArrivalStation().getFreeStationRailwayTrack();
            informationLog.getRailwayTracksManager().freeOccupiedForwardRailwayTrack(forwardRailwayTrack);
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for free arrival track", exception);
            return;
        }

        try {
            informationLog.getArrivalStation().unloadTrainWithGoodsToAssociateStorages(this);
        } catch (InterruptedException exception) {
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.WARNING,
                    "Use of train was stopped while waiting for free places in arrival storages", exception);
            return;
        }


        informationLog.getArrivalStation().freeOccupiedStationRailwayTrack(arrivalFreeRailwayTrack);
        /// TODO: отправить поезд в депо города-получателя
    }

    public void loadGood(Good good) {
        transportedGoods.add(good);
    }

    public Good unloadNextGood() {
        return transportedGoods.remove(0);
    }

    public boolean areThereAnyUnloadedGoodsLeft() {
        return transportedGoods.size() > 0;
    }
}
