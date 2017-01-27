package de.perdian.apps.qifgenerator.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.model.QifGeneratorModel;
import de.perdian.apps.qifgenerator.model.QifGeneratorModelHelper;
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

    public static void main(String[] args) {

        log.info("Launching application");
        Application.launch(QifGeneratorApplication.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Setting up model");
        QifGeneratorModel model = QifGeneratorModelHelper.createStockModel();
        QifGeneratorModelPane mainPane = new QifGeneratorModelPane(model);

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("StockQifGenerator");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(Math.min(Screen.getPrimary().getBounds().getHeight() - 100, 900));
        primaryStage.show();

    }

}