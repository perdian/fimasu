package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class TransactionGroupPane extends VBox {

    private TransactionGroup transactionGroup = null;

    TransactionGroupPane(TransactionGroup transactionGroup) {

        ComponentFactory componentFactory = new ComponentFactory();

        ToggleButton persistentButton = componentFactory.createToggleButton(transactionGroup.getPersistent(), MaterialDesignC.CONTENT_SAVE);
        persistentButton.setTooltip(new Tooltip("Persist transaction group"));

        TextField titleField = componentFactory.createTextField(transactionGroup.getTitle());
        titleField.setPrefWidth(400);
        Label titleLabel = componentFactory.createLabel("_Title");
        titleLabel.setLabelFor(titleField);

        TextField bankAccountNameField = componentFactory.createTextField(transactionGroup.getBankAccountName());
        GridPane.setHgrow(bankAccountNameField, Priority.ALWAYS);
        Label bankAccountNameLabel = componentFactory.createLabel("_Bank accout name");
        bankAccountNameLabel.setLabelFor(bankAccountNameField);

        TextField exportFileNameField = componentFactory.createTextField(transactionGroup.getExportFileName());
        Button exportFileNameButton = componentFactory.createButton(MaterialDesignF.FILE, action -> {
            throw new UnsupportedOperationException();
        });
        GridPane.setHgrow(exportFileNameField, Priority.ALWAYS);
        Label exportFileNameLabel = componentFactory.createLabel("Export _file name");
        exportFileNameLabel.setLabelFor(exportFileNameField);

        GridPane firstLine = new GridPane();
        firstLine.setHgap(5);
        firstLine.add(persistentButton, 0, 1, 1, 1);
        firstLine.add(titleLabel, 1, 0, 1, 1);
        firstLine.add(titleField, 1, 1, 1, 1);
        firstLine.add(bankAccountNameLabel, 2, 0, 1, 1);
        firstLine.add(bankAccountNameField, 2, 1, 1, 1);

        GridPane secondLine = new GridPane();
        secondLine.setHgap(5);
        secondLine.add(exportFileNameLabel, 0, 0, 2, 1);
        secondLine.add(exportFileNameField, 0, 1, 1, 1);
        secondLine.add(exportFileNameButton, 1, 1, 1, 1);

        this.setSpacing(10);
        this.getChildren().addAll(firstLine, secondLine);
        this.setTransactionGroup(transactionGroup);

    }

    TransactionGroup getTransactionGroup() {
        return this.transactionGroup;
    }
    private void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

}
