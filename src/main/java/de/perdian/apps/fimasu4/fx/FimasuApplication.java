package de.perdian.apps.fimasu4.fx;

import de.perdian.apps.fimasu4.model.TransactionGroupModel;
import de.perdian.apps.fimasu4.model.TransactionGroupRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FimasuApplication extends Application {

    private TransactionGroupModel transactionGroupModel = null;

    @Override
    public void init() throws Exception {
        this.setTransactionGroupModel(TransactionGroupRepository.loadTransactionGroupModel());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FimasuApplicationPane applicationPane = new FimasuApplicationPane(this.getTransactionGroupModel());
        Scene applicationScene = new Scene(applicationPane, 1600, 1200);

        primaryStage.setTitle("FinanzManager Support by perdian");
        primaryStage.setScene(applicationScene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

    private TransactionGroupModel getTransactionGroupModel() {
        return this.transactionGroupModel;
    }
    private void setTransactionGroupModel(TransactionGroupModel transactionGroupModel) {
        this.transactionGroupModel = transactionGroupModel;
    }

}
