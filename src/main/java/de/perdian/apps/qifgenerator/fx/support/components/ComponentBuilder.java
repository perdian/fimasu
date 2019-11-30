package de.perdian.apps.qifgenerator.fx.support.components;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.qifgenerator.fx.support.converters.IdentityStringConverter;
import de.perdian.apps.qifgenerator.fx.support.converters.MapEntryStringConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ComponentBuilder {

    private List<EventHandler<KeyEvent>> onKeyPressedEventHandlers = null;

    public ComponentBuilder() {
        this.setOnKeyPressedEventHandlers(new CopyOnWriteArrayList<>(List.of(new DefaultKeyPressEventHandler())));
    }

    public ComponentBuilder createChild() {
        ComponentBuilder childBuilder = new ComponentBuilder();
        childBuilder.getOnKeyPressedEventHandlers().addAll(this.getOnKeyPressedEventHandlers());
        return childBuilder;
    }

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 80%");
        return label;
    }

    public ComponentBuilderItem<TextField> createTextField(StringProperty property) {
        return this.createTextField(property, new IdentityStringConverter());
    }

    public <T> ComponentBuilderItem<TextField> createTextField(Property<T> property, StringConverter<T> stringConverter) {
        TextField textField = new TextField();
        Bindings.bindBidirectional(textField.textProperty(), property, stringConverter);
        textField.setMinHeight(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> { if (newValue.booleanValue()) { Platform.runLater(() -> textField.selectAll()); } });
        GridPane.setVgrow(textField, Priority.ALWAYS);
        return new ComponentBuilderItem<>(this, textField);
    }

    public <T> ComponentBuilderItem<ComboBox<T>> createComboBox(Property<T> property, Function<T, String> valueToStringFunction, List<Map.Entry<String, T>> availableValues) {
        List<T> comboBoxValues = availableValues.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(comboBoxValues));
        comboBox.setConverter(new MapEntryStringConverter<>(valueToStringFunction));
        Bindings.bindBidirectional(comboBox.valueProperty(), property);
        GridPane.setVgrow(comboBox, Priority.ALWAYS);
        return new ComponentBuilderItem<>(this, comboBox);
    }

    public ComponentBuilderItem<ComboBox<String>> createCurrencySelectionComboBox(Property<String> currencyProperty, List<Property<String>> allCurrencyProperties) {
        List<String> comboBoxInitialValues = allCurrencyProperties.stream().map(Property::getValue).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        ObservableList<String> comboBoxValues = FXCollections.observableArrayList(comboBoxInitialValues);
        allCurrencyProperties.forEach(changedProperty -> changedProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                Set<String> consolidatedCurrencies = new LinkedHashSet<>();
                allCurrencyProperties.forEach(property -> {
                    if (property.equals(o)) {
                        if (StringUtils.isNotEmpty(newValue)) {
                            consolidatedCurrencies.add(newValue);
                        }
                    } else {
                        if (StringUtils.isNotEmpty(property.getValue())) {
                            consolidatedCurrencies.add(property.getValue());
                        }
                    }
                });
                for (String consolidatedValue : consolidatedCurrencies) {
                    if (!comboBoxValues.contains(consolidatedValue)) {
                        comboBoxValues.add(consolidatedValue);
                    }
                }
                for (String existingValue : new ArrayList<>(comboBoxValues)) {
                    if (!consolidatedCurrencies.contains(existingValue)) {
                        comboBoxValues.remove(existingValue);
                    }
                }
                if (comboBoxValues.isEmpty()) {
                    allCurrencyProperties.forEach(property -> {
                        if (StringUtils.isNotEmpty(property.getValue())) {
                            comboBoxValues.add(property.getValue());
                        }
                    });
                }
            }
        }));
        ComboBox<String> comboBox = new ComboBox<>(comboBoxValues);
        comboBox.disableProperty().bind(Bindings.size(comboBoxValues).lessThanOrEqualTo(1));
        Bindings.bindBidirectional(comboBox.valueProperty(), currencyProperty);
        GridPane.setVgrow(comboBox, Priority.ALWAYS);
        return new ComponentBuilderItem<>(this, comboBox);
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
