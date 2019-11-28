package de.perdian.apps.qifgenerator.fx.widgets.transactions;

import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class TransactionComponentBuilderItem<T extends Control> {

    private TransactionComponentBuilder owner = null;
    private T component = null;

    TransactionComponentBuilderItem(TransactionComponentBuilder owner, T component) {
        this.setOwner(owner);
        this.setComponent(component);
    }

    TransactionComponentBuilderItem<T> width(Double width) {
        if (width != null) {
            this.getComponent().setMinWidth(width.doubleValue());
            this.getComponent().setMaxWidth(width.doubleValue());
        } else {
            this.getComponent().setMinWidth(0);
            this.getComponent().setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(this.getComponent(), Priority.ALWAYS);
        }
        return this;
    }

    private TransactionComponentBuilder getOwner() {
        return this.owner;
    }
    private void setOwner(TransactionComponentBuilder owner) {
        this.owner = owner;
    }

    public T get() {
        this.getOwner().getOnKeyPressedEventHandlers().forEach(eventHandler -> this.getComponent().addEventHandler(KeyEvent.KEY_PRESSED, eventHandler));
        return this.getComponent();
    }
    private T getComponent() {
        return this.component;
    }
    private void setComponent(T component) {
        this.component = component;
    }

}
