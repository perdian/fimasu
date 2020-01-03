package de.perdian.apps.fimasu.fx.widgets.transactions;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.fimasu.fx.widgets.transactions.actions.MoveTransactionActionEventHandler;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

class TransactionActionsPane extends BorderPane {

    TransactionActionsPane(String title, Transaction transaction, ObservableList<Transaction> allTransactions, ComponentBuilder componentBuilder, Preferences preferences) {

        ToggleButton persistButton = new ToggleButton(null, new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        persistButton.setTooltip(new Tooltip("Save transaction when existing the application"));
        persistButton.selectedProperty().bindBidirectional(transaction.getPersistent());
        Button deleteButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteButton.setMaxHeight(Double.MAX_VALUE);
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(event -> allTransactions.remove(transaction));
        Button moveUpButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
        moveUpButton.setMaxHeight(Double.MAX_VALUE);
        moveUpButton.disableProperty().bind(Bindings.valueAt(allTransactions, 0).isEqualTo(transaction));
        moveUpButton.setFocusTraversable(false);
        moveUpButton.setOnAction(new MoveTransactionActionEventHandler(transaction, allTransactions, -1));
        Button moveDownButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_DOWN));
        moveDownButton.setMaxHeight(Double.MAX_VALUE);
        moveDownButton.disableProperty().bind(Bindings.valueAt(allTransactions, Bindings.size(allTransactions).subtract(1)).isEqualTo(transaction));
        moveDownButton.setFocusTraversable(false);
        moveDownButton.setOnAction(new MoveTransactionActionEventHandler(transaction, allTransactions, 1));

        this.setLeft(new HBox(1, persistButton, deleteButton, moveUpButton, moveDownButton));

        Label titleLabel = new Label(title);
        titleLabel.setMaxHeight(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        this.setRight(titleLabel);

    }

}
