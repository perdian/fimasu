package de.perdian.apps.fimasu.fx;

import javafx.application.Application;
import javafx.application.Platform;

public class FimasuApplicationLauncher {

    public static void main(String[] args) {
        Platform.setImplicitExit(true);
        Application.launch(FimasuApplication.class, args);
    }

}
