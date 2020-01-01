package de.perdian.apps.fimasu.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;

public class FimaSuLauncher {

    private static final Logger log = LoggerFactory.getLogger(FimaSuApplication.class);

    public static void main(String[] args) {

        log.info("Launching application");
        Application.launch(FimaSuApplication.class);

    }

}
