package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrainsConfigurator {
    private static final Logger logger = Logger.getLogger(TrainsConfigurator.class.getName());

    private final HashMap<String, Properties> trainsDatabase = new LinkedHashMap<>();

    public static final ArrayList<String> trainsList = new ArrayList<>() {{
        add("avangard");
        add("redStar");
        add("torpedo");
    }};

    public TrainsConfigurator() throws IOException {
        for (String trainName : trainsList) {
            try {
                Properties trainProperties = new Properties() {{
                    load(getTrainsConfigFileInputStreamByName(trainName));
                }};
                trainsDatabase.put(trainName, trainProperties);
                logger.log(Level.INFO, trainName + "'s configs were successfully read");
            } catch (IOException exception) {
                logger.log(Level.SEVERE, "Could not find configs for " + trainName + " train", exception);
                throw exception;
            }
        }
    }

    private InputStream getTrainsConfigFileInputStreamByName(String trainName) throws IOException {
        String fullPropertiesFilename = "/trains/" + trainName + "Train.properties";
        InputStream inputStream = TrainsConfigurator.class.getResourceAsStream(fullPropertiesFilename);
        if (inputStream == null) {
            logger.log(Level.SEVERE, "Could not read " + fullPropertiesFilename + " file");
            throw new IOException();
        }

        return inputStream;
    }

    public Properties getDataAboutTrainByName(String trainName) {
        Properties trainData = trainsDatabase.get(trainName);
        if (trainData == null) {
            logger.log(Level.WARNING, "There is no data about " + trainName + " train in database. " +
                    "Null result returned");
        }
        return trainData;
    }
}
