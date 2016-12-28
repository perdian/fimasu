package de.perdian.personal.stockqifgenerator.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.personal.stockqifgenerator.model.StockQifGeneratorModel;
import de.perdian.personal.stockqifgenerator.model.StockQifGeneratorModelHelper;
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

public class StockQifGeneratorApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(StockQifGeneratorApplication.class);

    public static void main(String[] args) {

        log.info("Launching application");
        Application.launch(StockQifGeneratorApplication.class);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Setting up model");
        StockQifGeneratorModel model = StockQifGeneratorModelHelper.createStockModel();
        StockQifGeneratorModelPane mainPane = new StockQifGeneratorModelPane(model);

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