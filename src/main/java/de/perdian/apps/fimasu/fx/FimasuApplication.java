package de.perdian.apps.fimasu.fx;

import java.io.File;

import de.perdian.apps.fimasu.model.FimasuModel;
import de.perdian.apps.fimasu.model.persistence.FimasuModelRepository;
import de.perdian.apps.fimasu.model.persistence.xml.XmlModelRepository;
import de.perdian.apps.fimasu.model.types.TransactionGroup;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FimasuApplication extends Application {

    private FimasuModel model = null;
    private FimasuPreferences preferences = null;

    @Override
    public void init() throws Exception {

        File preferencesDirectory = new File(System.getProperty("user.home"), ".fimasu/");
        FimasuPreferences preferences = new FimasuPreferences(preferencesDirectory);
        this.setPreferences(preferences);

        FimasuModelRepository modelRepository = new XmlModelRepository(preferences);
        FimasuModel model = modelRepository.loadModel();
        if (model.getTransactionGroups().isEmpty()) {
            TransactionGroup transactionGroup = new TransactionGroup();
            transactionGroup.getSelected().setValue(true);
            model.getTransactionGroups().add(transactionGroup);
        }
        if (model.getSelectedTransactionGroup().getValue() == null) {
            TransactionGroup selectedTransactionGroup = model.getTransactionGroups().stream()
                .filter(t -> t.getSelected().getValue())
                .findFirst()
                .orElseGet(() -> model.getTransactionGroups().get(0));
            model.getSelectedTransactionGroup().setValue(selectedTransactionGroup);
        }
        this.setModel(model);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FimasuApplicationPane applicationPane = new FimasuApplicationPane(this.getModel(), this.getPreferences());
        Scene applicationScene = new Scene(applicationPane, 1400, 1100);

        primaryStage.setTitle("FinanzManager Support by perdian");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(applicationScene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

    private FimasuModel getModel() {
        return this.model;
    }
    private void setModel(FimasuModel model) {
        this.model = model;
    }

    private FimasuPreferences getPreferences() {
        return this.preferences;
    }
    private void setPreferences(FimasuPreferences preferences) {
        this.preferences = preferences;
    }

}
