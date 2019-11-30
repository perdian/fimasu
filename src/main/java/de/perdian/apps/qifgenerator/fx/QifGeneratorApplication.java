package de.perdian.apps.qifgenerator.fx;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.apps.qifgenerator.model.TransactionGroupFactory;
import de.perdian.apps.qifgenerator.preferences.Preferences;
import de.perdian.apps.qifgenerator.preferences.PreferencesFactory;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class QifGeneratorApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(QifGeneratorApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Loading preferences");
        Preferences preferences = PreferencesFactory.createPreferences();

        log.info("Loading transaction groups");
        File transactionGroupsFile = new File(preferences.getStorageDirectory(), "transactionGroups");
        ObservableList<TransactionGroup> transactionGroups = TransactionGroupFactory.loadTransactionGroups(transactionGroupsFile);

        log.info("Preparing JavaFX components");
        QifGeneratorPane generatorPane = new QifGeneratorPane(transactionGroups, preferences);
        Scene generatorScene = new Scene(generatorPane);
        generatorScene.getStylesheets().add("META-INF/stylesheets/qifgenerator.css");

        log.info("Opening JavaFX stage");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(generatorScene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("QifGenerator");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(Math.min(Screen.getPrimary().getBounds().getHeight() - 150, 1200));
        primaryStage.show();

    }

}
