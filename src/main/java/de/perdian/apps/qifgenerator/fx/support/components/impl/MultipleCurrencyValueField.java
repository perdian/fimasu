package de.perdian.apps.qifgenerator.fx.support.components.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.support.components.converters.DoubleStringConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MultipleCurrencyValueField extends GridPane {

    public MultipleCurrencyValueField(DoubleProperty targetProperty, StringProperty targetCurrencyProperty, StringProperty sourceCurrencyProperty, DoubleProperty conversionRateProperty, ComponentBuilder componentBuilder) {

        StringProperty selectedCurrencyProperty = new SimpleStringProperty(targetCurrencyProperty.getValue());
        DoubleProperty selectedValueProperty = new SimpleDoubleProperty();
        selectedValueProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                this.recomputeValue(targetProperty, newValue == null ? 0 : newValue.doubleValue(), selectedCurrencyProperty.getValue(), sourceCurrencyProperty.getValue(), conversionRateProperty.getValue());
            }
        });
        selectedCurrencyProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                this.recomputeValue(targetProperty, selectedValueProperty.getValue(), newValue, sourceCurrencyProperty.getValue(), conversionRateProperty.getValue());
            }
        });

        ObservableList<String> availableCurrencies = FXCollections.observableArrayList();
        if (StringUtils.isNotEmpty(targetCurrencyProperty.getValue())) {
            availableCurrencies.add(targetCurrencyProperty.getValue());
        }
        if (StringUtils.isNotEmpty(sourceCurrencyProperty.getValue()) && !Objects.equals(sourceCurrencyProperty.getValue(), targetCurrencyProperty.getValue())) {
            availableCurrencies.add(sourceCurrencyProperty.getValue());
        }
        TextField selectedValueField = componentBuilder.createTextField(selectedValueProperty, new DoubleStringConverter("0.00"));
        selectedValueField.setPrefWidth(85);

        ComboBox<String> availableCurrenciesBox = new ComboBox<>(availableCurrencies);
        availableCurrenciesBox.setPrefWidth(80);
        availableCurrenciesBox.setValue(selectedCurrencyProperty.getValue());
        selectedCurrencyProperty.bind(availableCurrenciesBox.valueProperty());

        targetCurrencyProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                boolean wasSelected = Objects.equals(availableCurrenciesBox.getValue(), oldValue);
                if (StringUtils.isNotEmpty(newValue) && !availableCurrenciesBox.itemsProperty().getValue().contains(newValue)) {
                    availableCurrenciesBox.itemsProperty().getValue().add(0, newValue);
                }
                if (!Objects.equals(oldValue, sourceCurrencyProperty.getValue())) {
                    availableCurrenciesBox.itemsProperty().getValue().remove(oldValue);
                }
                if (wasSelected && StringUtils.isNotEmpty(newValue)) {
                    availableCurrenciesBox.setValue(newValue);
                }
                availableCurrenciesBox.setDisable(availableCurrenciesBox.getItems().size() <= 1);
            }
        });

        sourceCurrencyProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                boolean wasSelected = Objects.equals(availableCurrenciesBox.getValue(), oldValue) && !Objects.equals(availableCurrenciesBox.getValue(), targetCurrencyProperty.getValue());
                if (StringUtils.isNotEmpty(newValue) && !availableCurrenciesBox.itemsProperty().getValue().contains(newValue)) {
                    availableCurrenciesBox.itemsProperty().getValue().add(newValue);
                }
                if (!Objects.equals(oldValue, targetCurrencyProperty.getValue())) {
                    availableCurrenciesBox.itemsProperty().getValue().remove(oldValue);
                }
                if (wasSelected && StringUtils.isNotEmpty(newValue)) {
                    availableCurrenciesBox.setValue(newValue);
                }
                availableCurrenciesBox.setDisable(availableCurrenciesBox.getItems().size() <= 1);
            }
        });

        this.add(selectedValueField, 0, 0, 1, 1);
        this.add(availableCurrenciesBox, 1, 0, 1, 1);

    }

    private void recomputeValue(DoubleProperty targetProperty, Double newValue, String selectedCurrency, String sourceCurrency, Double conversionRate) {

        double useConversionRate = 1;
        if (Objects.equals(selectedCurrency, sourceCurrency)) {
            useConversionRate = conversionRate.doubleValue();
        }
        targetProperty.setValue(newValue.doubleValue() / useConversionRate);

        System.err.println(""
            + " NEW:        " + newValue + "\n"
            + " SEL_CUR:    " + selectedCurrency + "\n"
            + " SOURCE_CUR: " + sourceCurrency + "\n"
            + " CONV_IN:    " + conversionRate + "\n"
            + " CONV_USE:   " + useConversionRate + "\n"
            + " VALUE:      " + (newValue.doubleValue() / useConversionRate) + "\n"
        );


    }

}
