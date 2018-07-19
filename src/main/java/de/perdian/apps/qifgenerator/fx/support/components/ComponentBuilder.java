package de.perdian.apps.qifgenerator.fx.support.components;

import java.util.ArrayList;
import java.util.List;
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
import javafx.util.converter.DefaultStringConverter;

public class ComponentBuilder {

    private List<EventHandler<KeyEvent>> onKeyPressedEventHandlers = new ArrayList<>();

    public ComponentBuilder() {
    }

    public ComponentBuilder(ComponentBuilder parent) {
        this.getOnKeyPressedEventHandlers().addAll(parent.getOnKeyPressedEventHandlers());
    }

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 90%");
        return label;
    }

    public TextField createTextField(Property<String> stringProperty) {
        return this.createTextField(stringProperty, new DefaultStringConverter());
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
        this.getOnKeyPressedEventHandlers().forEach(eventHandler -> textField.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler));
        return textField;

    }

    public <T> ComboBox<T> createComboBox(Property<T> property, T[] values) {
        ObservableList<T> valuesList = FXCollections.observableArrayList(values);
        ComboBox<T> comboBox = new ComboBox<>(valuesList);
        comboBox.valueProperty().bindBidirectional(property);
        this.getOnKeyPressedEventHandlers().forEach(eventHandler -> comboBox.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler));
        return comboBox;
    }

    public void addOnKeyPressedEventHandler(EventHandler<KeyEvent> eventHandler) {
        this.getOnKeyPressedEventHandlers().add(eventHandler);
    }
    public List<EventHandler<KeyEvent>> getOnKeyPressedEventHandlers() {
        return this.onKeyPressedEventHandlers;
    }
    public void setOnKeyPressedEventHandlers(List<EventHandler<KeyEvent>> onKeyPressedEventHandlers) {
        this.onKeyPressedEventHandlers = onKeyPressedEventHandlers;
    }

}
