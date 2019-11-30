package de.perdian.apps.qifgenerator.fx.widgets.transactions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.perdian.apps.qifgenerator.fx.support.converters.DoubleStringConverter;
import de.perdian.apps.qifgenerator.fx.support.converters.IdentityStringConverter;
import de.perdian.apps.qifgenerator.fx.support.converters.MapEntryStringConverter;
import de.perdian.apps.qifgenerator.fx.support.widgets.MonetaryValueBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

class TransactionComponentBuilder {

    private List<EventHandler<KeyEvent>> onKeyPressedEventHandlers = null;

    TransactionComponentBuilder() {
        this.setOnKeyPressedEventHandlers(new CopyOnWriteArrayList<>(List.of(new DefaultKeyPressEventHandler())));
    }

    Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 80%");
        return label;
    }

    TransactionComponentBuilderItem<TextField> createTextField(StringProperty property) {
        return this.createTextField(property, new IdentityStringConverter());
    }

    <T> TransactionComponentBuilderItem<TextField> createTextField(Property<T> property, StringConverter<T> stringConverter) {
        TextField textField = new TextField();
        Bindings.bindBidirectional(textField.textProperty(), property, stringConverter);
        textField.setMinHeight(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> { if (newValue.booleanValue()) { Platform.runLater(() -> textField.selectAll()); } });
        GridPane.setVgrow(textField, Priority.ALWAYS);
        return new TransactionComponentBuilderItem<>(this, textField);
    }

    <T> TransactionComponentBuilderItem<ComboBox<T>> createComboBox(Property<T> property, Function<T, String> valueToStringFunction, List<Map.Entry<String, T>> availableValues) {
        List<T> comboBoxValues = availableValues.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(comboBoxValues));
        comboBox.setConverter(new MapEntryStringConverter<>(valueToStringFunction));
        Bindings.bindBidirectional(comboBox.valueProperty(), property);
        GridPane.setVgrow(comboBox, Priority.ALWAYS);
        return new TransactionComponentBuilderItem<>(this, comboBox);
    }

    TransactionComponentBuilderItem<MonetaryValueBox> createMonetaryValueBox(Property<Double> valueProperty, StringProperty currencyProperty, DoubleStringConverter valueStringConverter, StringProperty bookingCurrencyProperty, DoubleProperty bookingCurrencyConversionRateProperty) {
        MonetaryValueBox valueBox = new MonetaryValueBox(valueProperty, currencyProperty, valueStringConverter, bookingCurrencyProperty, bookingCurrencyConversionRateProperty);
        GridPane.setVgrow(valueBox, Priority.ALWAYS);
        return new TransactionComponentBuilderItem<>(this, valueBox);
    }

    private static class DefaultKeyPressEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                Event.fireEvent(event.getTarget(), new KeyEvent(event.getSource(), event.getTarget(), event.getEventType(), "", "\t", KeyCode.TAB, event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));
            }
        }

    }

    public void addOnKeyPressedEventHandler(EventHandler<KeyEvent> eventHandler) {
        this.getOnKeyPressedEventHandlers().add(eventHandler);
    }
    List<EventHandler<KeyEvent>> getOnKeyPressedEventHandlers() {
        return this.onKeyPressedEventHandlers;
    }
    private void setOnKeyPressedEventHandlers(List<EventHandler<KeyEvent>> onKeyPressedEventHandlers) {
        this.onKeyPressedEventHandlers = onKeyPressedEventHandlers;
    }

}
