package de.perdian.apps.fimasu4.fx;

import de.perdian.apps.fimasu4.model.FimasuModel;
import de.perdian.apps.fimasu4.model.FimasuModelRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FimasuApplication extends Application {

    private FimasuModel model = null;

    @Override
    public void init() throws Exception {
        this.setModel(FimasuModelRepository.getRepository().loadModel());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FimasuApplicationPane applicationPane = new FimasuApplicationPane(this.getModel());
        Scene applicationScene = new Scene(applicationPane, 1400, 1100);

        applicationScene.focusOwnerProperty().addListener((o, oldValue, newValue) -> {
            System.err.println("Focus: " + newValue);
        });

        primaryStage.setTitle("FinanzManager Support by perdian");
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

}
