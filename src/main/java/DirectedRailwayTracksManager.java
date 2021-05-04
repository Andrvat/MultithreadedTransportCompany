import utilities.LoggerPrintAssistant;
import utilities.TimeUtilities;
import utilities.TransportCompanyConfigurator;

import javax.naming.TimeLimitExceededException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectedRailwayTracksManager {
    private static final Logger logger = Logger.getLogger(DirectedRailwayTracksManager.class.getName());

    private final ConcurrentLinkedQueue<RailwayTrack> freeForwardRailwayTracks = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<RailwayTrack> freeBackRailwayTracks = new ConcurrentLinkedQueue<>();

    private final TransportCompanyConfigurator companyConfigurator;

    public DirectedRailwayTracksManager(TransportCompanyConfigurator companyConfigurator) {
        this.companyConfigurator = companyConfigurator;

        initializeDirectedRailwayTracks();
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "All forward and back tracks were created by directed railway tracks manager");
    }

    private void initializeDirectedRailwayTracks() {
        int totalForwardRailwayTracksNumber = companyConfigurator.getForwardRailwayTracksNumber();
        for (int i = 0; i < totalForwardRailwayTracksNumber; i++) {
            freeForwardRailwayTracks.add(RailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }

        int totalBackRailwayTracksNumber = companyConfigurator.getBackRailwayTracksNumber();
        for (int i = 0; i < totalBackRailwayTracksNumber; i++) {
            freeBackRailwayTracks.add(RailwayTrack.builder()
                    .trackId(UUID.randomUUID().toString())
                    .isTrackOccupied(false)
                    .build());
        }
    }

    public RailwayTrack getFreeForwardRailwayTrack() throws InterruptedException {
        synchronized (freeForwardRailwayTracks) {
            while (freeForwardRailwayTracks.isEmpty()) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "All forward tracks occupied. Waiting for free some one...");
                freeForwardRailwayTracks.wait();
            }

            RailwayTrack track = freeForwardRailwayTracks.remove();
            track.occupyTrack();

            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Forward track " + track.getTrackId() + " was occupied");

            freeForwardRailwayTracks.notifyAll();
            return track;
        }
    }

    public void freeOccupiedForwardRailwayTrack(RailwayTrack track) {
        synchronized (freeForwardRailwayTracks) {
            track.freeTrack();
            freeForwardRailwayTracks.add(track);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Forward track " + track.getTrackId() + " was returned to directed railway tracks manager");
            freeForwardRailwayTracks.notifyAll();
        }
    }

    public RailwayTrack getFreeBackRailwayTrack() throws InterruptedException {
        synchronized (freeBackRailwayTracks) {
            while (freeBackRailwayTracks.isEmpty()) {
                LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                        "All back tracks occupied. Waiting for free some one...");
                freeBackRailwayTracks.wait();
            }

            RailwayTrack track = freeBackRailwayTracks.remove();
            track.occupyTrack();

            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Back track " + track.getTrackId() + " was occupied");

            freeBackRailwayTracks.notifyAll();
            return track;
        }
    }

    public void freeOccupiedBackRailwayTrack(RailwayTrack track) {
        synchronized (freeBackRailwayTracks) {
            track.freeTrack();
            freeBackRailwayTracks.add(track);
            LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                    "Back track " + track.getTrackId() + " was returned to directed railway tracks manager");
            freeBackRailwayTracks.notifyAll();
        }
    }

    public void startTrainRunningOnTrack(Train train, RailwayTrack track)
            throws InterruptedException, TimeLimitExceededException {
        int trainTotalTravelTime = (int) Math.ceil((double) companyConfigurator.getStationsDistance() /
                Integer.parseInt(train.getTrainProperties().getProperty("speed")));

        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") +
                        " is running on forward track " + track.getTrackId());
        Thread.sleep(TimeUtilities.convertSecsToMillis(trainTotalTravelTime));

        if (train.getTotalTimeInTrips() >= Integer.parseInt(train.getTrainProperties().getProperty("amortizationTime"))) {
            throw new TimeLimitExceededException();
        }

        train.increaseTotalTimeInTripsByValue(trainTotalTravelTime);
        LoggerPrintAssistant.printMessageWithSpecifiedThreadName(logger, Level.INFO,
                "Train " + train.getTrainProperties().getProperty("name") +
                        " arrived to station by " + track.getTrackId());
    }
}
