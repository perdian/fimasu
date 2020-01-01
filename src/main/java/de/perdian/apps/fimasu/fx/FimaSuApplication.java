package de.perdian.apps.fimasu.fx;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.model.TransactionGroupFactory;
import de.perdian.commons.fx.AbstractApplication;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FimaSuApplication extends AbstractApplication {

    private static final Logger log = LoggerFactory.getLogger(FimaSuApplication.class);

    @Override
    protected void configurePrimaryStage(Stage primaryStage) {
        primaryStage.setTitle("FimaSu");
        primaryStage.setWidth(1600);
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setHeight(Math.min(Screen.getPrimary().getBounds().getHeight() - 150, 1200));
    }

    @Override
    protected Pane createMainPane() {

        log.debug("Loading transaction groups");
        Path transactionGroupsPath = this.resolveApplicationDirectory().resolve("transactionGroups");
        ObservableList<TransactionGroup> transactionGroups = TransactionGroupFactory.loadTransactionGroups(transactionGroupsPath);

        log.debug("Creating FimaSuPane");
        return new FimaSuPane(transactionGroups, this.getPreferences());

    }

    @Override
    protected Scene createMainScene(Pane mainPane) {
        Scene scene = super.createMainScene(mainPane);
        scene.getStylesheets().add("META-INF/stylesheets/fimasu.css");
        return scene;
    }

}
