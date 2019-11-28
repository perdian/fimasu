package de.perdian.apps.qifgenerator_OLD.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator_OLD.fx.modules.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The main JavaFX application that makes up the TagTiger application
 *
 * @author Christian Robert
 */

public class QifGeneratorApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Setting up model");
        QifGeneratorPreferences preferences = new QifGeneratorPreferences();
        MainPane mainPane = new MainPane(preferences);

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("QifGenerator");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(Math.min(Screen.getPrimary().getBounds().getHeight() - 150, 1100));
        primaryStage.show();

    }

}
