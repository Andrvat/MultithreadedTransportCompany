package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransportCompanyConfigurator {
    private static final Logger logger = Logger.getLogger(TransportCompanyConfigurator.class.getName());

    private final Properties railwayTransportSystemConf = new Properties();

    public TransportCompanyConfigurator() throws IOException {
        try {
            InputStream railwayTransportSystemConfInputStream = getRailwayTransportSystemConfigFileInputStream();
            railwayTransportSystemConf.load(railwayTransportSystemConfInputStream);
            logger.log(Level.INFO, "Transport company configs were successfully read");
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "Could not read configs of transport company", exception);
            throw exception;
        }
    }

    private InputStream getRailwayTransportSystemConfigFileInputStream() throws IOException {
        InputStream inputStream = TransportCompanyConfigurator.class.getResourceAsStream(
                "/stations/railwayTransportSystemConf.properties");
        if (inputStream == null) {
            logger.log(Level.SEVERE, "Could not read railwayTransportSystemConf.properties file");
            throw new IOException();
        }

        return inputStream;
    }

    public int getStationsDistance() {
        String stationsDistance = railwayTransportSystemConf.getProperty("stationsDistance");
        return Integer.parseInt(stationsDistance);
    }

    public int getDepartureRailwayTracksNumber() {
        String departureRailwayTracksNumber = railwayTransportSystemConf.getProperty("departureRailwayTracksNumber");
        return Integer.parseInt(departureRailwayTracksNumber);
    }

    public int getArrivalRailwayTracksNumber() {
        String arrivalRailwayTracksNumber = railwayTransportSystemConf.getProperty("arrivalRailwayTracksNumber");
        return Integer.parseInt(arrivalRailwayTracksNumber);
    }

    public int getForwardRailwayTracksNumber() {
        String forwardRailwayTracksNumber = railwayTransportSystemConf.getProperty("forwardRailwayTracksNumber");
        return Integer.parseInt(forwardRailwayTracksNumber);
    }

    public int getBackRailwayTracksNumber() {
        String backRailwayTracksNumber = railwayTransportSystemConf.getProperty("backRailwayTracksNumber");
        return Integer.parseInt(backRailwayTracksNumber);
    }

}
