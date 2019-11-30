package de.perdian.apps.qifgenerator.fx.widgets.transactions;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

class TransactionComponentBuilderItem<T extends Region> {

    private TransactionComponentBuilder owner = null;
    private T component = null;
    private Double width = null;
    private ObservableBooleanValue disabled = null;
    private ObservableBooleanValue focusTraversable = null;

    TransactionComponentBuilderItem(TransactionComponentBuilder owner, T component) {
        this.setOwner(owner);
        this.setComponent(component);
    }

    TransactionComponentBuilderItem<T> width(Double width) {
        this.setWidth(width);
        return this;
    }

    TransactionComponentBuilderItem<T> disabled() {
        return this.disabled(new ReadOnlyBooleanWrapper(true));
    }

    TransactionComponentBuilderItem<T> disabled(ObservableBooleanValue booleanValue) {
        this.setDisabled(booleanValue);
        return this;
    }

    TransactionComponentBuilderItem<T> focusTraversable(boolean booleanValue) {
        return this.focusTraversable(new ReadOnlyBooleanWrapper(booleanValue));
    }

    TransactionComponentBuilderItem<T> focusTraversable(ObservableBooleanValue booleanValue) {
        this.setFocusTraversable(booleanValue);
        return this;
    }

    private TransactionComponentBuilder getOwner() {
        return this.owner;
    }
    private void setOwner(TransactionComponentBuilder owner) {
        this.owner = owner;
    }

    public T get() {
        if (this.getWidth() != null) {
            this.getComponent().setMinWidth(this.getWidth().doubleValue());
            this.getComponent().setMaxWidth(this.getWidth().doubleValue());
        } else {
            this.getComponent().setMinWidth(0);
            this.getComponent().setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(this.getComponent(), Priority.ALWAYS);
        }
        if (this.getDisabled() != null) {
            this.getComponent().disableProperty().bind(this.getDisabled());
        }
        if (this.getFocusTraversable() != null) {
            this.getComponent().focusTraversableProperty().bind(this.getFocusTraversable());
        }
        this.getOwner().getOnKeyPressedEventHandlers().forEach(eventHandler -> this.getComponent().addEventHandler(KeyEvent.KEY_PRESSED, eventHandler));
        return this.getComponent();
    }

    private T getComponent() {
        return this.component;
    }
    private void setComponent(T component) {
        this.component = component;
    }

    private Double getWidth() {
        return this.width;
    }
    private void setWidth(Double width) {
        this.width = width;
    }

    private ObservableBooleanValue getDisabled() {
        return this.disabled;
    }
    private void setDisabled(ObservableBooleanValue disabled) {
        this.disabled = disabled;
    }

    private ObservableBooleanValue getFocusTraversable() {
        return this.focusTraversable;
    }
    private void setFocusTraversable(ObservableBooleanValue focusTraversable) {
        this.focusTraversable = focusTraversable;
    }

}
