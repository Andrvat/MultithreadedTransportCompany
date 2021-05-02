package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoodsConfigurator {
    private static final Logger logger = Logger.getLogger(GoodsConfigurator.class.getName());

    private final HashMap<String, Properties> goodsDatabase = new LinkedHashMap<>();

    private static final ArrayList<String> goodsList = new ArrayList<>() {{
        add("computer");
        add("drink");
        add("tire");
        add("toy");
    }};

    public GoodsConfigurator() throws IOException {
        for (String goodName : goodsList) {
            try {
                Properties goodProperties = new Properties() {{
                    load(getGoodConfigFileInputStreamByName(goodName));
                }};
                goodsDatabase.put(goodName, goodProperties);
                logger.log(Level.INFO, goodName + "'s configs were successfully read");
            } catch (IOException exception) {
                logger.log(Level.SEVERE, "Could not find configs for " + goodName + " good", exception);
                throw exception;
            }
        }
    }

    private InputStream getGoodConfigFileInputStreamByName(String goodName) throws IOException {
        String fullPropertiesFilename = "/goods/" + goodName + "Good.properties";
        InputStream inputStream = GoodsConfigurator.class.getResourceAsStream(fullPropertiesFilename);
        if (inputStream == null) {
            logger.log(Level.SEVERE, "Could not read " + fullPropertiesFilename + " file");
            throw new IOException();
        }

        return inputStream;
    }

    public Properties getDataAboutGoodByName(String goodName) {
        Properties goodData = goodsDatabase.get(goodName);
        if (goodData == null) {
            logger.log(Level.WARNING, "There is no data about " + goodName + " good in database. " +
                    "Null result returned");
        }
        return goodData;
    }

    public static ArrayList<String> getGoodsList() {
        return goodsList;
    }
}
