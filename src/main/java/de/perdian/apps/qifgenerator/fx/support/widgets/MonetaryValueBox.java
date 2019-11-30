package de.perdian.apps.qifgenerator.fx.support.widgets;

import de.perdian.apps.qifgenerator.fx.support.converters.DoubleStringConverter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.GridPane;

public class MonetaryValueBox extends GridPane {

    public MonetaryValueBox(Property<Double> valueProperty, StringProperty currencyProperty, DoubleStringConverter valueStringConverter, StringProperty bookingCurrencyProperty, DoubleProperty bookingCurrencyConversionRateProperty) {

//        List<String> availableCurrenciesSet = List.of(marketCurrencyProperty.getValue(), bookingCurrencyProperty.getValue()).stream().filter(StringUtils::isNotEmpty).distinct().sorted().collect(Collectors.toList());
//        ObservableList<String> availableCurrencies = FXCollections.observableArrayList(availableCurrenciesSet);
//        marketCurrencyProperty.addListener((o, oldValue, newValue) -> this.recomputeCurrencies(availableCurrencies, List.of(newValue, bookingCurrencyProperty.getValue()), amountCurrencyProperty));
//        bookingCurrencyProperty.addListener((o, oldValue, newValue) -> this.recomputeCurrencies(availableCurrencies, List.of(marketCurrencyProperty.getValue(), newValue), amountCurrencyProperty));
//
//        if (!availableCurrenciesSet.contains(amountCurrencyProperty.getValue())) {
//            amountCurrencyProperty.setValue(availableCurrenciesSet.isEmpty() ? null : availableCurrenciesSet.get(0));
//        }
//
//        ComboBox<String> amountCurrencyBox = new ComboBox<>(availableCurrencies);
//        Bindings.bindBidirectional(amountCurrencyBox.valueProperty(), amountCurrencyProperty);
//        amountCurrencyBox.disableProperty().bind(Bindings.size(availableCurrencies).lessThanOrEqualTo(1));
//        amountCurrencyBox.setMinWidth(75);
//        GridPane.setVgrow(amountCurrencyBox, Priority.ALWAYS);
//
//        this.add(amountField, 0, 0, 1, 1);
//        this.add(amountCurrencyBox, 1, 0, 1, 1);
//        this.setHgap(2);

    }
//
//    private void recomputeCurrencies(ObservableList<String> targetCurrencies, Collection<String> inputCurrencies, Property<String> property) {
//        List<String> consolidatedCurrencies = inputCurrencies.stream().filter(StringUtils::isNotEmpty).filter(code -> code.length() == 3).distinct().collect(Collectors.toList());
//        if (consolidatedCurrencies.size() != targetCurrencies.size() || !targetCurrencies.containsAll(consolidatedCurrencies)) {
//            for (String currency : consolidatedCurrencies) {
//                if (!targetCurrencies.contains(currency)) {
//                    targetCurrencies.add(currency);
//                }
//            }
//            for (String currency : new ArrayList<>(targetCurrencies)) {
//                if (!consolidatedCurrencies.contains(currency)) {
//                    if (Objects.equals(currency, property.getValue())) {
//                        property.setValue(consolidatedCurrencies.isEmpty() ? null : consolidatedCurrencies.get(0));
//                    }
//                    targetCurrencies.remove(currency);
//                }
//            }
//            if (StringUtils.isEmpty(property.getValue()) && !consolidatedCurrencies.isEmpty()) {
//                property.setValue(consolidatedCurrencies.get(0));
//            }
//        }
//    }


















//
//        StringProperty selectedCurrencyProperty = new SimpleStringProperty(StringUtils.defaultIfEmpty(inputCurrencyProperty.getValue(), targetCurrencyProperty.getValue()));
//        DoubleProperty selectedValueProperty = new SimpleDoubleProperty(targetProperty.getValue());
//        selectedValueProperty.addListener((o, oldValue, newValue) -> {
//            if (!Objects.equals(oldValue, newValue)) {
//                this.recomputeValue(targetProperty, newValue == null ? 0 : newValue.doubleValue(), selectedCurrencyProperty.getValue(), sourceCurrencyProperty.getValue(), conversionRateProperty.getValue());
//            }
//        });
//        selectedCurrencyProperty.addListener((o, oldValue, newValue) -> {
//            if (!Objects.equals(oldValue, newValue)) {
//                this.recomputeValue(targetProperty, selectedValueProperty.getValue(), newValue, sourceCurrencyProperty.getValue(), conversionRateProperty.getValue());
//            }
//        });
//
//        ObservableList<String> availableCurrencies = FXCollections.observableArrayList();
//        if (StringUtils.isNotEmpty(targetCurrencyProperty.getValue())) {
//            availableCurrencies.add(targetCurrencyProperty.getValue());
//        }
//        if (StringUtils.isNotEmpty(sourceCurrencyProperty.getValue()) && !Objects.equals(sourceCurrencyProperty.getValue(), targetCurrencyProperty.getValue())) {
//            availableCurrencies.add(sourceCurrencyProperty.getValue());
//        }
//        TextField selectedValueField = componentBuilder.createTextField(selectedValueProperty, new DoubleStringConverter("0.00"));
//        selectedValueField.setPrefWidth(85);
//
//        ComboBox<String> availableCurrenciesBox = new ComboBox<>(availableCurrencies);
//        availableCurrenciesBox.setPrefWidth(80);
//        availableCurrenciesBox.setDisable(availableCurrencies.size() <= 1);
//        availableCurrenciesBox.setValue(selectedCurrencyProperty.getValue());
//        availableCurrenciesBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                Event.fireEvent(event.getTarget(), new KeyEvent(event.getSource(), event.getTarget(), event.getEventType(), "", "\t", KeyCode.TAB, event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown()));
//            }
//        });
//        selectedCurrencyProperty.bind(availableCurrenciesBox.valueProperty());
//
//        targetCurrencyProperty.addListener((o, oldValue, newValue) -> {
//            if (!Objects.equals(oldValue, newValue)) {
//                boolean wasSelected = Objects.equals(availableCurrenciesBox.getValue(), oldValue);
//                if (StringUtils.isNotEmpty(newValue) && !availableCurrenciesBox.itemsProperty().getValue().contains(newValue)) {
//                    availableCurrenciesBox.itemsProperty().getValue().add(0, newValue);
//                }
//                if (!Objects.equals(oldValue, sourceCurrencyProperty.getValue())) {
//                    availableCurrenciesBox.itemsProperty().getValue().remove(oldValue);
//                }
//                if (wasSelected && StringUtils.isNotEmpty(newValue)) {
//                    availableCurrenciesBox.setValue(newValue);
//                }
//                availableCurrenciesBox.setDisable(availableCurrenciesBox.getItems().size() <= 1);
//            }
//        });
//
//        sourceCurrencyProperty.addListener((o, oldValue, newValue) -> {
//            if (!Objects.equals(oldValue, newValue)) {
//                boolean wasSelected = Objects.equals(availableCurrenciesBox.getValue(), oldValue) && !Objects.equals(availableCurrenciesBox.getValue(), targetCurrencyProperty.getValue());
//                if (StringUtils.isNotEmpty(newValue) && !availableCurrenciesBox.itemsProperty().getValue().contains(newValue)) {
//                    availableCurrenciesBox.itemsProperty().getValue().add(newValue);
//                }
//                if (!Objects.equals(oldValue, targetCurrencyProperty.getValue())) {
//                    availableCurrenciesBox.itemsProperty().getValue().remove(oldValue);
//                }
//                if (wasSelected && StringUtils.isNotEmpty(newValue)) {
//                    availableCurrenciesBox.setValue(newValue);
//                }
//                availableCurrenciesBox.setDisable(availableCurrenciesBox.getItems().size() <= 1);
//            }
//        });
//
//        this.add(selectedValueField, 0, 0, 1, 1);
//        this.add(availableCurrenciesBox, 1, 0, 1, 1);
//
//    }

//    private void recomputeValue(DoubleProperty targetProperty, Double newValue, String selectedCurrency, String sourceCurrency, Double conversionRate) {
//        double useConversionRate = 1;
//        if (Objects.equals(selectedCurrency, sourceCurrency) && conversionRate != 0 && conversionRate != null) {
//            useConversionRate = conversionRate.doubleValue();
//        }
//        targetProperty.setValue(newValue.doubleValue() / useConversionRate);
//    }

}
