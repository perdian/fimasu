package de.perdian.apps.qifgenerator.fx.modules.transactions;

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.qifgenerator.fx.model.Transaction;
import de.perdian.apps.qifgenerator.fx.model.TransactionType;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.support.components.converters.DoubleStringConverter;
import de.perdian.apps.qifgenerator.fx.support.components.converters.LocalDateStringConverter;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

class TransactionPane_OLD extends VBox {

    private Transaction transaction = null;
    private List<Region> allControls = new ArrayList<>();
    private List<Region> highPriorityControls = new ArrayList<>();

    TransactionPane_OLD(Transaction transaction, ObservableList<Transaction> transactions, ComponentBuilder componentBuilder) {

        Button removeButton = new Button();
        removeButton.setFocusTraversable(false);
        removeButton.setGraphic(new ImageView(new Image(TransactionPane_OLD.class.getClassLoader().getResourceAsStream("icons/16/delete.png"))));
        removeButton.setOnAction(event -> transactions.remove(transaction));
        Button upButton = new Button();
        upButton.setFocusTraversable(false);
        upButton.setGraphic(new ImageView(new Image(TransactionPane_OLD.class.getClassLoader().getResourceAsStream("icons/16/go-up.png"))));
        upButton.setOnAction(event -> this.handleMoveTransaction(transaction, transactions, -1));
        Button downButton = new Button();
        downButton.setFocusTraversable(false);
        downButton.setGraphic(new ImageView(new Image(TransactionPane_OLD.class.getClassLoader().getResourceAsStream("icons/16/go-down.png"))));
        downButton.setOnAction(event -> this.handleMoveTransaction(transaction, transactions, 1));
        HBox buttonBox = new HBox(removeButton, upButton, downButton);
        buttonBox.setSpacing(1);

        TextField wknField = componentBuilder.createTextField(transaction.wknProperty(), new DefaultStringConverter());
        wknField.textProperty().addListener((o, oldValue, newValue) -> wknField.setText(newValue.toUpperCase()));
        TextField isinField = componentBuilder.createTextField(transaction.isinProperty(), new DefaultStringConverter());
        isinField.textProperty().addListener((o, oldValue, newValue) -> isinField.setText(newValue.toUpperCase()));
        GridPane topPane = new GridPane();
        topPane.setHgap(5);
        topPane.setVgap(1);
        this.append(topPane, buttonBox, 0, 1, 1, 1, null, Priority.NEVER, false);
        this.append(topPane, componentBuilder.createLabel("WKN"), 1, 0, 1, 1, null, Priority.NEVER, false);
        this.append(topPane, wknField, 1, 1, 1, 1, 70, Priority.NEVER, false);
        this.append(topPane, componentBuilder.createLabel("ISIN"), 2, 0, 1, 1, null, Priority.NEVER, false);
        this.append(topPane, isinField, 2, 1, 1, 1, 130, Priority.NEVER, false);
        this.append(topPane, componentBuilder.createLabel("Title"), 3, 0, 1, 1, null, Priority.ALWAYS, false);
        this.append(topPane, componentBuilder.createTextField(transaction.titleProperty(), new DefaultStringConverter()), 3, 1, 1, 1, 150, Priority.ALWAYS, false);

        TextField marketValueField = componentBuilder.createTextField(transaction.marketValueProperty(), new DoubleStringConverter("0.00"));
        marketValueField.setDisable(true);
        TextField totalValueField = componentBuilder.createTextField(transaction.totalValueProperty(), new DoubleStringConverter("0.00"));
        totalValueField.setDisable(true);
        GridPane bottomPane = new GridPane();
        bottomPane.setHgap(5);
        bottomPane.setVgap(1);
        this.append(bottomPane, componentBuilder.createLabel("Type"), 0, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createComboBox(transaction.typeProperty(), TransactionType.values()), 0, 1, 1, 1, 75, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createLabel("Booking date"), 1, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.bookingDateProperty(), new LocalDateStringConverter()), 1, 1, 1, 1, 80, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Valuta date"), 2, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.valutaDateProperty(), new LocalDateStringConverter()), 2, 1, 1, 1, 80, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("# Shares"), 3, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.numberOfSharesProperty(), new DoubleStringConverter("0.00000")), 3, 1, 1, 1, 75, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Market Price"), 4, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.marketPriceProperty(), new DoubleStringConverter("0.00000")), 4, 1, 1, 1, 80, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Market Value"), 5, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, marketValueField, 5, 1, 1, 1, 80, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createLabel("Charges"), 6, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.chargesProperty(), new DoubleStringConverter("0.00")), 6, 1, 1, 1, 60, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Fin. Tax"), 7, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.financeTaxProperty(), new DoubleStringConverter("0.00")), 7, 1, 1, 1, 60, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Sol. Tax"), 8, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, componentBuilder.createTextField(transaction.solidarityTaxProperty(), new DoubleStringConverter("0.00")), 8, 1, 1, 1, 60, Priority.NEVER, true);
        this.append(bottomPane, componentBuilder.createLabel("Total Value"), 9, 0, 1, 1, null, Priority.NEVER, false);
        this.append(bottomPane, totalValueField, 9, 1, 1, 1, 80, Priority.ALWAYS, false);

        this.setSpacing(5);
        this.getChildren().add(topPane);
        this.getChildren().add(bottomPane);
        this.setTransaction(transaction);

    }

    private void append(GridPane targetPane, Region control, int gridX, int gridY, int gridWidth, int gridHeight, Integer width, Priority hgrowPriority, boolean highPriorityFocus) {
        if (width != null) {
            control.setMinWidth(width.intValue());
            control.setPrefWidth(width.intValue());
        }
        if (hgrowPriority != null) {
            GridPane.setHgrow(control, hgrowPriority);
        }
        if (highPriorityFocus) {
            this.getHighPriorityControls().add(control);
        }
        this.getAllControls().add(control);
        targetPane.add(control, gridX, gridY, gridWidth, gridHeight);
    }

    private void handleOnKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            int currentControlIndex = this.getAllControls().indexOf(e.getSource());
            if (currentControlIndex > -1) {
                Region nextHighPriorityRegion = this.findNextHighPriorityRegion(currentControlIndex + 1);
                if (nextHighPriorityRegion != null) {
                    nextHighPriorityRegion.requestFocus();
                }
            }
        }
    }

    private Region findNextHighPriorityRegion(int startIndex) {

        // First check our internal fields
        Region followingRegionFromThis = this.findNextHighPriorityRegionFromThis(startIndex);
        if (followingRegionFromThis != null) {
            return followingRegionFromThis;
        }

        // Now check the next transaction pane
        ObservableList<Node> parentChildren = this.getParent().getChildrenUnmodifiable();
        int parentIndexOfThis = parentChildren.indexOf(this);
        Region followingRegionFromParent = this.findNextHighPriorityRegionFromParent(parentIndexOfThis + 1);
        if (followingRegionFromParent != null) {
            return followingRegionFromParent;
        } else {
            return this.findNextHighPriorityRegionFromParent(0);
        }

    }

    private Region findNextHighPriorityRegionFromThis(int startIndex) {
        for (int i = startIndex; i < this.getAllControls().size(); i++) {
            Region control = this.getAllControls().get(i);
            if (this.getHighPriorityControls().contains(control)) {
                return control;
            }
        }
        return null;
    }

    private Region findNextHighPriorityRegionFromParent(int parentStartIndex) {
        ObservableList<Node> parentChildren = this.getParent().getChildrenUnmodifiable();
        for (int i = parentStartIndex; i < parentChildren.size(); i++) {
            Node parentChildrenNode = parentChildren.get(i);
            if (parentChildrenNode instanceof TransactionPane_OLD) {
                Region region = ((TransactionPane_OLD)parentChildrenNode).findNextHighPriorityRegionFromThis(0);
                if (region != null) {
                    return region;
                }
            }
        }
        return null;
    }

    private void handleMoveTransaction(Transaction transaction, ObservableList<Transaction> transactions, int direction) {
        int currentIndex = transactions.indexOf(transaction);
        transactions.remove(transaction);
        if (direction < 0) {
            transactions.add(Math.max(0, currentIndex + direction), transaction);
        } else if (direction > 0) {
            transactions.add(Math.min(transactions.size(), currentIndex + direction), transaction);
        }
    }

    Transaction getTransaction() {
        return this.transaction;
    }
    void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    List<Region> getAllControls() {
        return this.allControls;
    }
    void setAllControls(List<Region> allControls) {
        this.allControls = allControls;
    }

    List<Region> getHighPriorityControls() {
        return this.highPriorityControls;
    }
    void setHighPriorityControls(List<Region> highPriorityControls) {
        this.highPriorityControls = highPriorityControls;
    }

}
