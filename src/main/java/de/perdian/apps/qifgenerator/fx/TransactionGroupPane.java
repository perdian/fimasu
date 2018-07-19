package de.perdian.apps.qifgenerator.fx;

import de.perdian.apps.qifgenerator.fxnew.model.Transaction;
import de.perdian.apps.qifgenerator.fxnew.model.TransactionGroup;
import de.perdian.apps.qifgenerator.fxnew.modules.transactions.TransactionGroupExportEventHandler;
import de.perdian.apps.qifgenerator.fxnew.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fxnew.support.components.converters.FileStringConverter;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

class TransactionGroupPane extends GridPane {

    TransactionGroupPane(TransactionGroup transactionGroup) {

        ComponentBuilder componentBuilder = new ComponentBuilder();

        VBox transactionsBox = new VBox();
        transactionsBox.setSpacing(25);
        transactionsBox.setMaxWidth(Double.MAX_VALUE);
        for (Transaction transaction : transactionGroup.transactionsProperty()) {
            transactionsBox.getChildren().add(new TransactionPane(transaction, transactionGroup.transactionsProperty(), e -> transactionGroup.transactionsProperty().remove(transaction)));
        }
        ScrollPane transactionsScrollPane = new ScrollPane(transactionsBox);
        transactionsScrollPane.setFitToWidth(true);
        transactionsScrollPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane transactionsScrollTitledPane = new TitledPane("Transactions", transactionsScrollPane);
        transactionsScrollTitledPane.setMaxHeight(Double.MAX_VALUE);
        transactionsScrollTitledPane.setCollapsible(false);
        GridPane.setHgrow(transactionsScrollTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(transactionsScrollTitledPane, Priority.ALWAYS);

        TextField titleTextField = componentBuilder.createTextField(transactionGroup.titleProperty(), new DefaultStringConverter());
        titleTextField.setPrefWidth(150);
        TextField accountTextField = componentBuilder.createTextField(transactionGroup.accountProperty(), new DefaultStringConverter());
        GridPane.setHgrow(accountTextField, Priority.ALWAYS);
        TextField targetFileField = componentBuilder.createTextField(transactionGroup.targetFileProperty(), new FileStringConverter());
        GridPane.setHgrow(targetFileField, Priority.ALWAYS);
        GridPane transactionGroupDetailsPane = new GridPane();
        transactionGroupDetailsPane.setHgap(5);
        transactionGroupDetailsPane.add(componentBuilder.createLabel("Transaction group title"), 0, 0);
        transactionGroupDetailsPane.add(titleTextField, 0, 1);
        transactionGroupDetailsPane.add(componentBuilder.createLabel("Account name"), 1, 0);
        transactionGroupDetailsPane.add(accountTextField, 1, 1);
        transactionGroupDetailsPane.add(componentBuilder.createLabel("Target file"), 0, 2, 2, 1);
        transactionGroupDetailsPane.add(targetFileField, 0, 3, 2, 1);

        TitledPane transactionGroupDetailsTitledPane = new TitledPane("Transaction group details", transactionGroupDetailsPane);
        transactionGroupDetailsTitledPane.setExpanded(true);
        transactionGroupDetailsTitledPane.setCollapsible(false);
        GridPane.setHgrow(transactionGroupDetailsTitledPane, Priority.ALWAYS);

        Button addButton = new Button("Add transaction");
        addButton.setGraphic(new ImageView(new Image(TransactionGroupPane.class.getClassLoader().getResourceAsStream("icons/16/add.png"))));
        addButton.setOnAction(event -> transactionGroup.transactionsProperty().add(new Transaction()));
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setFocusTraversable(false);
        Button exportButton = new Button("Export");
        exportButton.setGraphic(new ImageView(new Image(TransactionGroupPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        exportButton.setOnAction(new TransactionGroupExportEventHandler(transactionGroup));
        exportButton.setMaxWidth(Double.MAX_VALUE);
        exportButton.setFocusTraversable(false);
        VBox transactionsButtonBox = new VBox(addButton, exportButton);
        transactionsButtonBox.setSpacing(5);
        transactionsButtonBox.setPadding(new Insets(10, 5, 10, 5));
        TitledPane transactionsButtonTitledPane = new TitledPane("Actions", transactionsButtonBox);
        transactionsButtonTitledPane.setExpanded(true);
        transactionsButtonTitledPane.setCollapsible(false);
        transactionsButtonTitledPane.setMaxHeight(Double.MAX_VALUE);

        this.add(transactionsScrollTitledPane, 0, 0, 2, 1);
        this.add(transactionGroupDetailsTitledPane, 0, 1, 1, 1);
        this.add(transactionsButtonTitledPane, 1, 1, 1, 1);
        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(5, 5, 5, 5));

        transactionGroup.transactionsProperty().addListener((ListChangeListener<Transaction>)event -> {
            while (event.next()) {
                for (Transaction removedTransaction : event.getRemoved()) {
                    for (Node transactionsBoxChild : transactionsBox.getChildren()) {
                        if (transactionsBoxChild instanceof TransactionPane) {
                            TransactionPane transactionPane = ((TransactionPane)transactionsBoxChild);
                            if (removedTransaction.equals(transactionPane.getTransaction())) {
                                Platform.runLater(() -> transactionsBox.getChildren().remove(transactionsBoxChild));
                            }
                        }
                    }
                }
                if (!event.getAddedSubList().isEmpty()) {
                    for (int i=0; i < event.getAddedSubList().size(); i++) {
                        Transaction newTransaction = event.getAddedSubList().get(i);
                        TransactionPane newTransactionEditPane = new TransactionPane(newTransaction, transactionGroup.transactionsProperty(), e -> transactionGroup.transactionsProperty().remove(newTransaction));
                        int targetIndex = transactionGroup.transactionsProperty().indexOf(newTransaction);
                        Platform.runLater(() -> transactionsBox.getChildren().add(targetIndex, newTransactionEditPane));
                    }
                }
            }
        });

    }

}
