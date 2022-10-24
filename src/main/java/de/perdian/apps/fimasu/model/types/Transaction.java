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
import javafx.collections.FXCollections;
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
    private ReadOnlyObjectProperty<BigDecimal> stockValue = null;
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
        StringProperty stockCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
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
        StringProperty payoutCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
        payoutCurrencyProperty.addListener(changeListenersDelegate);
        this.setPayoutCurrency(payoutCurrencyProperty);

        ObjectProperty<BigDecimal> bookingValueProperty = new SimpleObjectProperty<>();
        this.setBookingValue(bookingValueProperty);
        StringProperty bookingCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
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

        ObjectProperty<BigDecimal> chargesValueProperty = new SimpleObjectProperty<>();
        chargesValueProperty.addListener(changeListenersDelegate);
        this.setChargesValue(chargesValueProperty);
        StringProperty chargesCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
        chargesCurrencyProperty.addListener(changeListenersDelegate);
        this.setChargesCurrency(chargesCurrencyProperty);

        ObjectProperty<BigDecimal> financeTaxValueProperty = new SimpleObjectProperty<>();
        financeTaxValueProperty.addListener(changeListenersDelegate);
        this.setFinanceTaxValue(financeTaxValueProperty);
        StringProperty financeTaxCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
        financeTaxCurrencyProperty.addListener(changeListenersDelegate);
        this.setFinanceTaxCurrency(financeTaxCurrencyProperty);

        ObjectProperty<BigDecimal> solidarityTaxValueProperty = new SimpleObjectProperty<>();
        solidarityTaxValueProperty.addListener(changeListenersDelegate);
        this.setSolidarityTaxValue(solidarityTaxValueProperty);
        StringProperty solidarityTaxCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
        solidarityTaxCurrencyProperty.addListener(changeListenersDelegate);
        this.setSolidarityTaxCurrency(solidarityTaxCurrencyProperty);

        ObjectProperty<BigDecimal> totalValueProperty = new SimpleObjectProperty<>();
        this.setTotalValue(totalValueProperty);
        StringProperty totalCurrencyProperty = new SimpleStringProperty(TransactionHelper.DEFAULT_CURRENCY);
        this.setTotalCurrency(totalCurrencyProperty);

        ChangeListener<Object> recomputeTotalValueListener = (o, oldValue, newValue) -> TransactionHelper.recomputeTotalValue(totalValueProperty, totalCurrencyProperty, bookingValueProperty, bookingCurrencyProperty, bookingConversionRateProperty, chargesValueProperty, chargesCurrencyProperty, financeTaxValueProperty, financeTaxCurrencyProperty, solidarityTaxValueProperty, solidarityTaxCurrencyProperty, transactionTypeProperty);
        bookingValueProperty.addListener(recomputeTotalValueListener);
        bookingCurrencyProperty.addListener(recomputeTotalValueListener);
        bookingConversionRateProperty.addListener(recomputeTotalValueListener);
        chargesValueProperty.addListener(recomputeTotalValueListener);
        chargesCurrencyProperty.addListener(recomputeTotalValueListener);
        financeTaxValueProperty.addListener(recomputeTotalValueListener);
        financeTaxCurrencyProperty.addListener(recomputeTotalValueListener);
        solidarityTaxValueProperty.addListener(recomputeTotalValueListener);
        solidarityTaxCurrencyProperty.addListener(recomputeTotalValueListener);
        transactionTypeProperty.addListener(recomputeTotalValueListener);

        ObservableList<String> availableCurrenciesProperty = FXCollections.observableArrayList(TransactionHelper.DEFAULT_CURRENCY);
        this.setAvailableCurrencies(availableCurrenciesProperty);
        ChangeListener<Object> recomputeAvailableCurrenciesListener = (o, oldValue, newValue) -> TransactionHelper.recomputeAvailableCurrencies(availableCurrenciesProperty, bookingInputCurrencyProperty.getValue(), bookingCurrencyProperty);
        bookingInputCurrencyProperty.addListener(recomputeAvailableCurrenciesListener);
        stockCurrencyProperty.addListener(recomputeAvailableCurrenciesListener);
        payoutCurrencyProperty.addListener(recomputeAvailableCurrenciesListener);
        bookingCurrencyProperty.addListener(recomputeAvailableCurrenciesListener);

    }

    public void writeValuesInto(Transaction targetTransaction) {
        targetTransaction.getType().setValue(this.getType().getValue());
        targetTransaction.getBookingDate().setValue(this.getBookingDate().getValue());
        targetTransaction.getValutaDate().setValue(this.getValutaDate().getValue());
        targetTransaction.getBookingConversionRate().setValue(this.getBookingConversionRate().getValue());
        targetTransaction.getBookingCurrency().setValue(this.getBookingCurrency().getValue());
        targetTransaction.getChargesCurrency().setValue(this.getChargesCurrency().getValue());
        targetTransaction.getChargesValue().setValue(this.getChargesValue().getValue());
        targetTransaction.getFinanceTaxCurrency().setValue(this.getFinanceTaxCurrency().getValue());
        targetTransaction.getFinanceTaxValue().setValue(this.getFinanceTaxValue().getValue());
        targetTransaction.getPayoutCurrency().setValue(this.getPayoutCurrency().getValue());
        targetTransaction.getPayoutValue().setValue(this.getPayoutValue().getValue());
        targetTransaction.getSolidarityTaxCurrency().setValue(this.getSolidarityTaxCurrency().getValue());
        targetTransaction.getSolidarityTaxValue().setValue(this.getSolidarityTaxValue().getValue());
        targetTransaction.getStockCount().setValue(this.getStockCount().getValue());
        targetTransaction.getStockCurrency().setValue(this.getStockCurrency().getValue());
        targetTransaction.getStockPricePerUnit().setValue(this.getStockPricePerUnit().getValue());
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

    public ReadOnlyObjectProperty<BigDecimal> getStockValue() {
        return this.stockValue;
    }
    private void setStockValue(ReadOnlyObjectProperty<BigDecimal> stockValue) {
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
