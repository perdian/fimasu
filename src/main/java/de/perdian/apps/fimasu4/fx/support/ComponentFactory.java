package de.perdian.apps.fimasu4.fx.support;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ComponentFactory {

    public <T> TextField createTextField(Property<String> property) {
        return this.createTextField(property, new DefaultStringConverter());
    }

    public <T> TextField createTextField(Property<T> property, StringConverter<T> converter) {

        TextFormatter<T> textFormatter = new TextFormatter<>(converter);
        textFormatter.valueProperty().bindBidirectional(property);

        TextField textField = new TextField();
        textField.setTextFormatter(textFormatter);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                textField.selectAll();
            }
        });
        return textField;

    }

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setMnemonicParsing(true);
        return label;
    }

    public ToggleButton createToggleButton(BooleanProperty property, String text, Ikon icon) {
        ToggleButton toggleButton = new ToggleButton(text,icon == null ? null :  new FontIcon(icon));
        toggleButton.selectedProperty().bindBidirectional(property);
        return toggleButton;
    }

    public Button createButton(Ikon icon, EventHandler<ActionEvent> eventHandler) {
        return this.createButton(null, icon, eventHandler);
    }

    public Button createButton(String title, Ikon icon, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(title, icon == null ? null : new FontIcon(icon));
        button.setOnAction(eventHandler);
        return button;
    }

}
