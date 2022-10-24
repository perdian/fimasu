package de.perdian.apps.fimasu.model.types;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableList;

public class Transaction implements Serializable {

    static final long serialVersionUID = 1L;

    private ObjectProperty<TransactionType> type = null;
    private BooleanProperty persistent = null;
    private ObjectProperty<LocalDate> bookingDate = null;
    private ObjectProperty<LocalDate> valutaDate = null;
    private StockIdentfier stockIdentifier = null;
    private ObjectProperty<BigDecimal> stockCount = null;
    private ObjectProperty<BigDecimal> stockPricePerUnit = null;
    private StringProperty stockCurrency = null;
    private ObjectProperty<BigDecimal> stockValue = null;
    private ObjectProperty<BigDecimal> payoutValue = null;
    private StringProperty payoutCurrency = null;
    private ReadOnlyObjectProperty<BigDecimal> bookingValue = null;
    private StringProperty bookingCurrency = null;
    private ObjectProperty<BigDecimal> bookingConversionRate = null;
    private ObjectProperty<BigDecimal> chargesValue = null;
    private StringProperty chargesCurrency = null;
    private ObjectProperty<BigDecimal> financeTaxValue = null;
    private StringProperty financeTaxCurrency = null;
    private ObjectProperty<BigDecimal> solidarityTaxValue = null;
    private StringProperty solidarityTaxCurrency = null;
    private ReadOnlyObjectProperty<BigDecimal> totalValue = null;
    private ReadOnlyStringProperty totalCurrency = null;
    private ObservableList<String> availableCurrencies = null;
    private List<ChangeListener<Object>> changeListeners = null;

    public Transaction() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        ChangeListener<Object> changeListenersDelegate = TransactionHelper.createChangeListenerDelegate(changeListeners);
        this.setChangeListeners(changeListeners);

        BooleanProperty persistentProperty = new SimpleBooleanProperty(false);
        persistentProperty.addListener(changeListenersDelegate);
        this.setPersistent(persistentProperty);

        ObjectProperty<LocalDate> bookingDateProperty = new SimpleObjectProperty<>();
        bookingDateProperty.addListener(changeListenersDelegate);
        this.setBookingDate(bookingDateProperty);
        ObjectProperty<LocalDate> valutaDateProperty = new SimpleObjectProperty<>();
        valutaDateProperty.addListener(changeListenersDelegate);
        this.setValutaDate(valutaDateProperty);
        bookingDateProperty.addListener((o, oldValue, newValue) -> TransactionHelper.recomputeValutaDate(valutaDateProperty, bookingDateProperty));

        StockIdentfier stockIdentifier = new StockIdentfier();
        stockIdentifier.addChangeListener(changeListenersDelegate);
        this.setStockIdentifier(stockIdentifier);

        ObjectProperty<BigDecimal> stockCountProperty = new SimpleObjectProperty<>();
        stockCountProperty.addListener(changeListenersDelegate);
        this.setStockCount(stockCountProperty);
        ObjectProperty<BigDecimal> stockPricePerUnitProperty = new SimpleObjectProperty<>();
        stockPricePerUnitProperty.addListener(changeListenersDelegate);
        this.setStockPricePerUnit(stockPricePerUnitProperty);
        StringProperty stockCurrencyProperty = new SimpleStringProperty("EUR");
        stockCurrencyProperty.addListener(changeListenersDelegate);
        this.setStockCurrency(stockCurrencyProperty);
        ObjectProperty<BigDecimal> stockValueProperty = new SimpleObjectProperty<>();
        this.setStockValue(stockValueProperty);
        ChangeListener<Object> recomputeStockValueListener = (o, oldValue, newValue) -> TransactionHelper.recomputeStockValue(stockValueProperty, stockCountProperty, stockPricePerUnitProperty);
        stockCountProperty.addListener(recomputeStockValueListener);
        stockPricePerUnitProperty.addListener(recomputeStockValueListener);

        ObjectProperty<BigDecimal> payoutValueProperty = new SimpleObjectProperty<>();
        payoutValueProperty.addListener(changeListenersDelegate);
        this.setPayoutValue(payoutValueProperty);
        StringProperty payoutCurrencyProperty = new SimpleStringProperty("EUR");
        payoutCurrencyProperty.addListener(changeListenersDelegate);
        this.setPayoutCurrency(payoutCurrencyProperty);

        ObjectProperty<BigDecimal> bookingValueProperty = new SimpleObjectProperty<>();
        this.setBookingValue(bookingValueProperty);
        StringProperty bookingCurrencyProperty = new SimpleStringProperty("EUR");
        bookingCurrencyProperty.addListener(changeListenersDelegate);
        this.setBookingCurrency(bookingCurrencyProperty);
        ObjectProperty<BigDecimal> bookingConversionRateProperty = new SimpleObjectProperty<>();
        bookingConversionRateProperty.addListener(changeListenersDelegate);
        this.setBookingConversionRate(bookingConversionRateProperty);

        ObjectProperty<ObservableObjectValue<BigDecimal>> bookingInputValueProperty = new SimpleObjectProperty<>();
        ObjectProperty<ObservableStringValue> bookingInputCurrencyProperty = new SimpleObjectProperty<>();
        ObjectProperty<TransactionType> transactionTypeProperty = new SimpleObjectProperty<>();
        transactionTypeProperty.addListener(changeListenersDelegate);
        transactionTypeProperty.addListener((o, oldValue, newValue) -> TransactionHelper.recomputeBookingInput(bookingInputValueProperty, bookingInputCurrencyProperty, transactionTypeProperty, stockCountProperty, stockPricePerUnitProperty, stockValueProperty, stockCurrencyProperty, payoutValueProperty, payoutCurrencyProperty, bookingValueProperty, bookingCurrencyProperty, bookingConversionRateProperty));
        this.setType(transactionTypeProperty);

        ChangeListener<Object> recomputeBookingValueListener = (o, oldValue, newValue) -> TransactionHelper.recomputeBookingValue(bookingValueProperty, bookingCurrencyProperty, bookingInputValueProperty.getValue(), bookingInputCurrencyProperty.getValue(), bookingConversionRateProperty);
        stockValueProperty.addListener(recomputeBookingValueListener);
        stockCurrencyProperty.addListener(recomputeBookingValueListener);
        payoutValueProperty.addListener(recomputeBookingValueListener);
        payoutCurrencyProperty.addListener(recomputeBookingValueListener);



//
//        MonetaryValue stockValue = MonetaryValueBindings.multiply(stockPrice, stockCount);
//        stockValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
//        stockValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
//        this.setStockValue(stockValue);
//
//        MonetaryValue payoutValue = new MonetaryValue();
//        payoutValue.addListener(changeListener);
//        payoutValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
//        payoutValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
//        this.setPayoutValue(payoutValue);
//
//        MonetaryValue bookingValue = new MonetaryValue();
//        bookingValue.addListener(changeListener);
//        bookingValue.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
//        bookingValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
//        bookingValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
//        this.setBookingValue(bookingValue);
//
//        ObjectProperty<BigDecimal> bookingConversionRate = new SimpleObjectProperty<>();
//        bookingConversionRate.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
//        this.setBookingConversionRate(bookingConversionRate);
//
//        MonetaryValue additionalCharges = new MonetaryValue();
//        additionalCharges.addListener(changeListener);
//        additionalCharges.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
//        this.setAdditionalCharges(additionalCharges);
//
//        MonetaryValue additionalFinanceTax = new MonetaryValue();
//        additionalFinanceTax.addListener(changeListener);
//        additionalFinanceTax.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
//        this.setAdditionalFinanceTax(additionalFinanceTax);
//
//        MonetaryValue additionalSolidarityTax = new MonetaryValue();
//        additionalSolidarityTax.addListener(changeListener);
//        additionalSolidarityTax.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
//        this.setAdditionalSolidarityTax(additionalSolidarityTax);
//
//        MonetaryValue totalValue = new MonetaryValue();
//        this.setTotalValue(totalValue);
//
//        ObservableList<String> availableCurrencies = FXCollections.observableArrayList();
//        if (StringUtils.isNotEmpty(bookingValue.getCurrency().getValue())) {
//            availableCurrencies.add(bookingValue.getCurrency().getValue());
//        }
//        this.setAvailableCurrencies(availableCurrencies);
//
//        BooleanProperty persistent = new SimpleBooleanProperty();
//        persistent.addListener(changeListener);
//        this.setPersistent(persistent);

    }

//    private void recomputeBookingValue(MonetaryValue inputValue) {
//        MonetaryValue targetValue = this.getBookingValue();
//        BigDecimal inputAmount = inputValue.getAmount().getValue();
//        if (inputAmount == null) {
//            targetValue.getAmount().setValue(null);
//        } else {
//            String inputCurrency = inputValue.getCurrency().getValue();
//            String bookingCurrency = targetValue.getCurrency().getValue();
//            if (Objects.equals(inputCurrency, bookingCurrency)) {
//                targetValue.getAmount().setValue(inputValue.getAmount().getValue());
//                this.getBookingConversionRate().setValue(null);
//            } else {
//                BigDecimal currencyConversionRate = this.getBookingConversionRate().getValue();
//                if (currencyConversionRate == null) {
//                    targetValue.getAmount().setValue(null);
//                } else {
//                    targetValue.getAmount().setValue(currencyConversionRate.multiply(inputValue.getAmount().getValue()));
//                }
//            }
//        }
//    }
//
//    private void recomputeTotalValue() {
//        String bookingCurrency = this.getBookingValue().getCurrency().getValue();
//        BigDecimal targetAmount = this.getBookingValue().getAmount().getValue();
//        List<MonetaryValue> additionalValues = List.of(this.getAdditionalCharges(), this.getAdditionalFinanceTax(), this.getAdditionalSolidarityTax());
//        for (MonetaryValue additionalValue : additionalValues) {
//            BigDecimal additionalValueAmountInSourceCurrency = additionalValue.getAmount().getValue();
//            BigDecimal additionalValueAmountInBookingCurrency = null;
//            if (Objects.equals(additionalValue.getCurrency().getValue(), bookingCurrency)) {
//                additionalValueAmountInBookingCurrency = additionalValueAmountInSourceCurrency;
//            } else {
//                BigDecimal conversionRate = this.getBookingConversionRate().getValue();
//                if (conversionRate != null) {
//                    additionalValueAmountInBookingCurrency = additionalValueAmountInSourceCurrency.multiply(conversionRate);
//                }
//            }
//            if (additionalValueAmountInBookingCurrency != null) {
//                TransactionType type = this.getType().getValue();
//                BigDecimal targetAmountChangeFactor = type == null ? BigDecimal.ONE : BigDecimal.valueOf(type.getChargesFactor());
//                BigDecimal targetAmountChange = additionalValueAmountInBookingCurrency.multiply(targetAmountChangeFactor);
//                targetAmount = targetAmount == null ? targetAmountChange : targetAmount.add(targetAmountChange);
//            }
//        }
//        this.getTotalValue().getCurrency().setValue(bookingCurrency);
//        this.getTotalValue().getAmount().setValue(targetAmount);
//    }
//
//    private void recomputeAvailableCurrencies(String oldCurrencyValue, MonetaryValue inputValue) {
//        Set<String> availableCurrencies = new LinkedHashSet<>();
//        if (StringUtils.isNotEmpty(this.getBookingValue().getCurrency().getValue())) {
//            availableCurrencies.add(this.getBookingValue().getCurrency().getValue());
//        }
//        if (StringUtils.isNotEmpty(inputValue.getCurrency().getValue())) {
//            availableCurrencies.add(inputValue.getCurrency().getValue());
//        }
//        if (availableCurrencies.isEmpty()) {
//            availableCurrencies.add("EUR");
//        }
//        if (!this.getAvailableCurrencies().containsAll(availableCurrencies) || this.getAvailableCurrencies().size() != availableCurrencies.size()) {
//            this.getAvailableCurrencies().setAll(availableCurrencies);
//        }
//        for (MonetaryValue additionalValue : List.of(this.getAdditionalCharges(), this.getAdditionalFinanceTax(), this.getAdditionalSolidarityTax())) {
//            if (oldCurrencyValue != null && Objects.equals(oldCurrencyValue, additionalValue.getCurrency().getValue())) {
//                additionalValue.getCurrency().setValue(inputValue.getCurrency().getValue());
//            }
//            if (!availableCurrencies.contains(additionalValue.getCurrency().getValue())) {
//                additionalValue.getCurrency().setValue(availableCurrencies.iterator().next());
//            }
//        }
//    }

    public void writeValuesInto(Transaction targetTransaction) {
    }

    public ObjectProperty<TransactionType> getType() {
        return this.type;
    }
    private void setType(ObjectProperty<TransactionType> type) {
        this.type = type;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    private void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
    }

    public ObjectProperty<LocalDate> getBookingDate() {
        return this.bookingDate;
    }
    private void setBookingDate(ObjectProperty<LocalDate> bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ObjectProperty<LocalDate> getValutaDate() {
        return this.valutaDate;
    }
    private void setValutaDate(ObjectProperty<LocalDate> valutaDate) {
        this.valutaDate = valutaDate;
    }

    public StockIdentfier getStockIdentifier() {
        return this.stockIdentifier;
    }
    private void setStockIdentifier(StockIdentfier stockIdentifier) {
        this.stockIdentifier = stockIdentifier;
    }

    public ObjectProperty<BigDecimal> getStockCount() {
        return this.stockCount;
    }
    private void setStockCount(ObjectProperty<BigDecimal> stockCount) {
        this.stockCount = stockCount;
    }

    public ObjectProperty<BigDecimal> getStockPricePerUnit() {
        return this.stockPricePerUnit;
    }
    private void setStockPricePerUnit(ObjectProperty<BigDecimal> stockPricePerUnit) {
        this.stockPricePerUnit = stockPricePerUnit;
    }

    public StringProperty getStockCurrency() {
        return this.stockCurrency;
    }
    private void setStockCurrency(StringProperty stockCurrency) {
        this.stockCurrency = stockCurrency;
    }

    public ObjectProperty<BigDecimal> getStockValue() {
        return this.stockValue;
    }
    private void setStockValue(ObjectProperty<BigDecimal> stockValue) {
        this.stockValue = stockValue;
    }

    public ObjectProperty<BigDecimal> getPayoutValue() {
        return this.payoutValue;
    }
    private void setPayoutValue(ObjectProperty<BigDecimal> payoutValue) {
        this.payoutValue = payoutValue;
    }

    public StringProperty getPayoutCurrency() {
        return this.payoutCurrency;
    }
    private void setPayoutCurrency(StringProperty payoutCurrency) {
        this.payoutCurrency = payoutCurrency;
    }

    public ReadOnlyObjectProperty<BigDecimal> getBookingValue() {
        return this.bookingValue;
    }
    private void setBookingValue(ReadOnlyObjectProperty<BigDecimal> bookingValue) {
        this.bookingValue = bookingValue;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }
    private void setBookingCurrency(StringProperty bookingCurrency) {
        this.bookingCurrency = bookingCurrency;
    }

    public ObjectProperty<BigDecimal> getBookingConversionRate() {
        return this.bookingConversionRate;
    }
    private void setBookingConversionRate(ObjectProperty<BigDecimal> bookingConversionRate) {
        this.bookingConversionRate = bookingConversionRate;
    }

    public ObjectProperty<BigDecimal> getChargesValue() {
        return this.chargesValue;
    }
    private void setChargesValue(ObjectProperty<BigDecimal> chargesValue) {
        this.chargesValue = chargesValue;
    }

    public StringProperty getChargesCurrency() {
        return this.chargesCurrency;
    }
    private void setChargesCurrency(StringProperty chargesCurrency) {
        this.chargesCurrency = chargesCurrency;
    }

    public ObjectProperty<BigDecimal> getFinanceTaxValue() {
        return this.financeTaxValue;
    }
    private void setFinanceTaxValue(ObjectProperty<BigDecimal> financeTaxValue) {
        this.financeTaxValue = financeTaxValue;
    }

    public StringProperty getFinanceTaxCurrency() {
        return this.financeTaxCurrency;
    }
    private void setFinanceTaxCurrency(StringProperty financeTaxCurrency) {
        this.financeTaxCurrency = financeTaxCurrency;
    }

    public ObjectProperty<BigDecimal> getSolidarityTaxValue() {
        return this.solidarityTaxValue;
    }
    private void setSolidarityTaxValue(ObjectProperty<BigDecimal> solidarityTaxValue) {
        this.solidarityTaxValue = solidarityTaxValue;
    }

    public StringProperty getSolidarityTaxCurrency() {
        return this.solidarityTaxCurrency;
    }
    private void setSolidarityTaxCurrency(StringProperty solidarityTaxCurrency) {
        this.solidarityTaxCurrency = solidarityTaxCurrency;
    }

    public ReadOnlyObjectProperty<BigDecimal> getTotalValue() {
        return this.totalValue;
    }
    private void setTotalValue(ReadOnlyObjectProperty<BigDecimal> totalValue) {
        this.totalValue = totalValue;
    }

    public ReadOnlyStringProperty getTotalCurrency() {
        return this.totalCurrency;
    }
    private void setTotalCurrency(ReadOnlyStringProperty totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    public ObservableList<String> getAvailableCurrencies() {
        return this.availableCurrencies;
    }
    private void setAvailableCurrencies(ObservableList<String> availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
