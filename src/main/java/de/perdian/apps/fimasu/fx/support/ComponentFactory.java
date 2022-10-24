package de.perdian.apps.fimasu.fx.support;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import de.perdian.apps.fimasu.fx.support.converters.BigDecimalStringConverter;
import de.perdian.apps.fimasu.fx.support.converters.ToStringStringConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

    public TextField createReadOnlyDecimalField(ReadOnlyProperty<BigDecimal> property, int precisionDigits) {
        BigDecimalStringConverter bigDecimalStringConverter = new BigDecimalStringConverter(precisionDigits);
        TextField textField = new TextField(bigDecimalStringConverter.toString(property.getValue()));
        textField.setDisable(true);
        property.addListener((o, oldValue, newValue) -> textField.setText(bigDecimalStringConverter.toString(newValue)));
        return textField;
    }

    public TextField createDecimalField(Property<BigDecimal> property, int precisionDigits) {
        return this.createTextField(property, new BigDecimalStringConverter(precisionDigits));
    }

    public TextField createCurrencyField(StringProperty currencyProperty) {
        TextField textField = this.createTextField(currencyProperty);
        textField.setPrefWidth(50);
        textField.setOnKeyTyped(keyEvent -> {
            String character = keyEvent.getCharacter();
            if (!keyEvent.isMetaDown() && !keyEvent.isControlDown() && !keyEvent.isAltDown() && !character.isEmpty() && Character.isLetterOrDigit(character.charAt(0))) {
                textField.commitValue();
            }
        });
        return textField;
    }

    public TextField createReadOnlyCurrencyField(ReadOnlyStringProperty currencyProperty) {
        TextField textField = new TextField();
        textField.textProperty().bind(currencyProperty);
        textField.setPrefWidth(50);
        textField.setDisable(true);
        return textField;
    }

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setMnemonicParsing(true);
        return label;
    }

    public <T> Label createLabel(ObjectProperty<T> property) {
        return this.createLabel(property, new ToStringStringConverter<>());
    }

    public <T> Label createLabel(ObjectProperty<T> property, StringConverter<T> converter) {
        Label label = this.createLabel(converter.toString(property.getValue()));
        property.addListener((o, oldValue, newValue) -> label.setText(converter.toString(newValue)));
        return label;
    }

    public ToggleButton createToggleButton(BooleanProperty property, Ikon icon) {
        return this.createToggleButton(property, null, icon);
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

    public ComboBox<String> createCurrencyComboBox(Property<String> property, ObservableList<String> availableValues) {
        ComboBox<String> comboBox = new ComboBox<>(availableValues);
        comboBox.disableProperty().bind(Bindings.size(availableValues).lessThanOrEqualTo(1));
        comboBox.setValue(property.getValue());
        comboBox.valueProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                comboBox.setValue(availableValues.get(0));
            } else {
                property.setValue(newValue);
            }
        });
        property.addListener((o, oldValue, newValue) -> {
            if (StringUtils.isNotEmpty(newValue)) {
                comboBox.setValue(newValue);
            }
        });
        comboBox.setPrefWidth(75);
        return comboBox;
    }

    public <T> ComboBox<T> createComboBox(Property<T> property, StringConverter<T> stringConverter, ObservableList<T> availableValues) {
        ComboBox<T> comboBox = new ComboBox<>(availableValues);
        comboBox.setConverter(new ToStringStringConverter<>());
        comboBox.valueProperty().bindBidirectional(property);
        comboBox.disableProperty().bind(Bindings.size(availableValues).lessThanOrEqualTo(1));
        return comboBox;
    }

}
