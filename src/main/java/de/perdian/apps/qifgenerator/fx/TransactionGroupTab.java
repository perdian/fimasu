package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.model.QifGeneratorModel;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

class TransactionGroupTab extends Tab {

    TransactionGroupTab(TransactionGroup transactionGroup, QifGeneratorModel model) {
        this.textProperty().bind(transactionGroup.titleProperty());
        this.setOnCloseRequest(event -> {
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete transaction group");
            confirmationAlert.setHeaderText("Delete transaction group");
            confirmationAlert.setContentText("Really delete the transaction group?");
            if (confirmationAlert.showAndWait().get().equals(ButtonType.OK)) {
                model.transactionGroupsProperty().remove(transactionGroup);
            } else {
                event.consume();
            }
        });
        this.setContent(new TransactionGroupPane(transactionGroup));
    }

}
