package de.perdian.personal.stockimporter.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.personal.stockimporter.fx.panels.StockModelPane;
import de.perdian.personal.stockimporter.model.StockModel;
import de.perdian.personal.stockimporter.model.StockModelFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main JavaFX application that makes up the TagTiger application
 *
 * @author Christian Robert
 */

public class StockimporterApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(StockimporterApplication.class);

    public static void main(String[] args) {

        log.info("Launching application");
        Application.launch(StockimporterApplication.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Setting up model");
        StockModel model = StockModelFactory.createStockModel();
        StockModelPane mainPane = new StockModelPane(model);

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("StockImporter");
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();

    }

}