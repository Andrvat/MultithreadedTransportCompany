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

    public int getStationsDistance() throws IOException {
        String stationsDistance = railwayTransportSystemConf.getProperty("stationsDistance");
        if (stationsDistance == null) {
            logger.log(Level.SEVERE, "There is no stationsDistance field in the *.properties file");
            throw new IOException();
        }
        return Integer.parseInt(stationsDistance);
    }

    public int getDepartureRailwayTracksNumber() throws IOException {
        String departureRailwayTracksNumber = railwayTransportSystemConf.getProperty("departureRailwayTracksNumber");
        if (departureRailwayTracksNumber == null) {
            logger.log(Level.SEVERE, "There is no departureRailwayTracksNumber field in the *.properties file");
            throw new IOException();
        }
        return Integer.parseInt(departureRailwayTracksNumber);
    }

    public int getArrivalRailwayTracksNumber() throws IOException {
        String arrivalRailwayTracksNumber = railwayTransportSystemConf.getProperty("arrivalRailwayTracksNumber");
        if (arrivalRailwayTracksNumber == null) {
            logger.log(Level.SEVERE, "There is no arrivalRailwayTracksNumber field in the *.properties file");
            throw new IOException();
        }
        return Integer.parseInt(arrivalRailwayTracksNumber);
    }

    public int getForwardRailwayTracksNumber() throws IOException {
        String forwardRailwayTracksNumber = railwayTransportSystemConf.getProperty("forwardRailwayTracksNumber");
        if (forwardRailwayTracksNumber == null) {
            logger.log(Level.SEVERE, "There is no forwardRailwayTracksNumber field in the *.properties file");
            throw new IOException();
        }
        return Integer.parseInt(forwardRailwayTracksNumber);
    }

    public int getBackRailwayTracksNumber() throws IOException {
        String backRailwayTracksNumber = railwayTransportSystemConf.getProperty("backRailwayTracksNumber");
        if (backRailwayTracksNumber == null) {
            logger.log(Level.SEVERE, "There is no backRailwayTracksNumber field in the *.properties file");
            throw new IOException();
        }
        return Integer.parseInt(backRailwayTracksNumber);
    }

}
