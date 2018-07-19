package de.perdian.apps.qifgenerator.fx.modules.transactions;

import java.io.File;

import de.perdian.apps.qifgenerator.fx.model.TransactionGroup;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.support.components.converters.FileStringConverter;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.converter.DefaultStringConverter;

class TransactionGroupDetailsPane extends GridPane {

    TransactionGroupDetailsPane(TransactionGroup transactionGroup, ComponentBuilder componentBuilder) {

        TextField titleField = componentBuilder.createTextField(transactionGroup.titleProperty(), new DefaultStringConverter());
        titleField.setPrefWidth(200);
        TextField accountField = componentBuilder.createTextField(transactionGroup.accountProperty(), new DefaultStringConverter());
        GridPane.setHgrow(accountField, Priority.ALWAYS);
        TextField targetFileField = componentBuilder.createTextField(transactionGroup.targetFileProperty(), new FileStringConverter());
        GridPane.setHgrow(targetFileField, Priority.ALWAYS);
        Button targetFileSelectButton = new Button(null, new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/document.png"))));
        targetFileSelectButton.setTooltip(new Tooltip("Select target file"));
        targetFileSelectButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select target file");
            if (transactionGroup.targetFileProperty().getValue() != null) {
                fileChooser.setInitialDirectory(transactionGroup.targetFileProperty().getValue().getParentFile());
            }
            File fileSelected = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (fileSelected != null) {
                transactionGroup.targetFileProperty().setValue(fileSelected);
            }
        });

        GridPane topRowPane = new GridPane();
        topRowPane.setHgap(2);
        topRowPane.setVgap(2);
        topRowPane.add(componentBuilder.createLabel("Transaction group title"), 0, 0, 1, 1);
        topRowPane.add(titleField, 0, 1, 1, 1);
        topRowPane.add(componentBuilder.createLabel("Account name"), 1, 0, 1, 1);
        topRowPane.add(accountField, 1, 1, 1, 1);
        GridPane.setHgrow(topRowPane, Priority.ALWAYS);

        GridPane bottomRowPane = new GridPane();
        bottomRowPane.setHgap(2);
        bottomRowPane.setVgap(2);
        bottomRowPane.add(componentBuilder.createLabel("Target file"), 0, 0, 2, 1);
        bottomRowPane.add(targetFileField, 0, 1, 1, 1);
        bottomRowPane.add(targetFileSelectButton, 1, 1, 1, 1);
        GridPane.setHgrow(bottomRowPane, Priority.ALWAYS);

        this.add(topRowPane, 0, 0, 1, 1);
        this.add(bottomRowPane, 0, 1, 1, 1);
        this.setVgap(8);

    }

}
