package handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuitButtonClickHandler {
    private static final Logger logger = Logger.getLogger(QuitButtonClickHandler.class.getName());

    public static void handle() {
        int character = 0;
        while (character != 'q') {
            try {
                character = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.log(Level.WARNING, "Quit symbol was handled");
    }
}
