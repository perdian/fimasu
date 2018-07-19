package de.perdian.apps.qifgenerator.fxnew.support.components;

import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class ComponentBuilder {

    private EventHandler<KeyEvent> onKeyPressedEventHandler = null;

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 90%");
        return label;
    }

    public <T> TextField createTextField(Property<T> typedProperty, StringConverter<T> converter) {

        Property<String> stringProperty = new SimpleStringProperty(converter.toString(typedProperty.getValue()));
        typedProperty.addListener((o, oldValue, newValue) -> stringProperty.setValue(converter.toString(newValue)));
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                typedProperty.setValue(converter.fromString(newValue));
            }
        });

        TextField textField = new TextField();
        textField.setMinHeight(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);
        textField.textProperty().bindBidirectional(stringProperty);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> { if (newValue.booleanValue()) { Platform.runLater(() -> textField.selectAll()); } });
        if (this.getOnKeyPressedEventHandler() != null) {
            textField.setOnKeyPressed(this.getOnKeyPressedEventHandler());
        }
        return textField;

    }

    public <T> ComboBox<T> createComboBox(Property<T> property, T[] values) {
        ObservableList<T> valuesList = FXCollections.observableArrayList(values);
        ComboBox<T> comboBox = new ComboBox<>(valuesList);
        comboBox.valueProperty().bindBidirectional(property);
        if (this.getOnKeyPressedEventHandler() != null) {
            comboBox.setOnKeyPressed(this.getOnKeyPressedEventHandler());
        }
        return comboBox;
    }

    public EventHandler<KeyEvent> getOnKeyPressedEventHandler() {
        return this.onKeyPressedEventHandler;
    }
    public void setOnKeyPressedEventHandler(EventHandler<KeyEvent> onKeyPressedEventHandler) {
        this.onKeyPressedEventHandler = onKeyPressedEventHandler;
    }

}
