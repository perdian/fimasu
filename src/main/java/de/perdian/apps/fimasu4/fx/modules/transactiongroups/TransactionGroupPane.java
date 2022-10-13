package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.model.TransactionGroup;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TransactionGroupPane extends GridPane {

    private TransactionGroup transactionGroup = null;

    TransactionGroupPane(TransactionGroup transactionGroup) {

        ComponentFactory componentFactory = new ComponentFactory();

        ToggleButton persistentButton = componentFactory.createToggleButton(transactionGroup.getPersistent(), null, MaterialDesignC.CONTENT_SAVE);

        Label titleLabel = componentFactory.createLabel("Title");
        TextField titleField = componentFactory.createTextField(transactionGroup.getTitle());
        titleField.setPrefWidth(300);

        Label bankAccountNameLabel = componentFactory.createLabel("Bank accout name");
        TextField bankAccountNameField = componentFactory.createTextField(transactionGroup.getBankAccountName());
        GridPane.setHgrow(bankAccountNameField, Priority.SOMETIMES);

        this.add(titleLabel, 0, 0, 2, 1);
        this.add(bankAccountNameLabel, 2, 0, 1, 1);
        this.add(persistentButton, 0, 1, 1, 1);
        this.add(titleField, 1, 1, 1, 1);
        this.add(bankAccountNameField, 2, 1, 1, 1);
        this.setHgap(5);

        this.setTransactionGroup(transactionGroup);

    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}
