package de.perdian.apps.fimasu4.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FimasuApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FimasuApplicationPane applicationPane = new FimasuApplicationPane();
        Scene applicationScene = new Scene(applicationPane, 1600, 1200);

        primaryStage.setTitle("FinanzManager Support by perdian");
        primaryStage.setScene(applicationScene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

}
