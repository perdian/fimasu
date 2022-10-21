package de.perdian.apps.fimasu4.model.types;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
    private MonetaryValue stockPrice = null;
    private MonetaryValue stockValue = null;
    private MonetaryValue payoutValue = null;
    private MonetaryValue bookingValue = null;
    private ObjectProperty<BigDecimal> bookingConversionRate = null;
    private MonetaryValue additionalCharges = null;
    private MonetaryValue additionalFinanceTax = null;
    private MonetaryValue additionalSolidarityTax = null;
    private MonetaryValue totalValue = null;
    private ObservableList<String> availableCurrencies = null;
    private List<ChangeListener<Object>> changeListeners = null;

    public Transaction() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        this.setChangeListeners(changeListeners);
        ChangeListener<Object> changeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<Object> delegeeChangeListener : changeListeners) {
                delegeeChangeListener.changed(o, oldValue, newValue);
            }
        };

        ObjectProperty<MonetaryValue> bookingValueSource = new SimpleObjectProperty<>();
        bookingValueSource.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(newValue));

        ObjectProperty<TransactionType> type = new SimpleObjectProperty<>();
        type.addListener((o, oldValue, newValue) -> {
            if (List.of(TransactionType.BUY, TransactionType.SELL).contains(newValue)) {
                this.getPayoutValue().getAmount().setValue(null);
                bookingValueSource.setValue(this.getStockValue());
            } else if (List.of(TransactionType.PAYOUT).contains(newValue)) {
                this.getStockPrice().getAmount().setValue(null);
                this.getStockCount().setValue(null);
                bookingValueSource.setValue(this.getPayoutValue());
            } else {
                bookingValueSource.setValue(null);
            }
            this.recomputeAvailableCurrencies(null, bookingValueSource.getValue());
            this.recomputeTotalValue();
        });
        type.addListener(changeListener);
        this.setType(type);

        StockIdentfier stockIdentifier = new StockIdentfier();
        stockIdentifier.getWkn().addListener(changeListener);
        stockIdentifier.getIsin().addListener(changeListener);
        stockIdentifier.getTitle().addListener(changeListener);
        this.setStockIdentifier(stockIdentifier);

        ObjectProperty<LocalDate> bookingDate = new SimpleObjectProperty<>();
        bookingDate.addListener(changeListener);
        this.setBookingDate(bookingDate);
        ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
        valutaDate.addListener(changeListener);
        this.setValutaDate(valutaDate);

        ObjectProperty<BigDecimal> stockCount = new SimpleObjectProperty<>();
        stockCount.addListener(changeListener);
        this.setStockCount(stockCount);

        MonetaryValue stockPrice = new MonetaryValue();
        stockPrice.addListener(changeListener);
        this.setStockPrice(stockPrice);

        MonetaryValue stockValue = MonetaryValueBindings.multiply(stockPrice, stockCount);
        stockValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
        stockValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
        this.setStockValue(stockValue);

        MonetaryValue payoutValue = new MonetaryValue();
        payoutValue.addListener(changeListener);
        payoutValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
        payoutValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
        this.setPayoutValue(payoutValue);

        MonetaryValue bookingValue = new MonetaryValue();
        bookingValue.addListener(changeListener);
        bookingValue.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
        bookingValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
        bookingValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeAvailableCurrencies(oldValue, bookingValueSource.getValue()));
        this.setBookingValue(bookingValue);

        ObjectProperty<BigDecimal> bookingConversionRate = new SimpleObjectProperty<>();
        bookingConversionRate.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(bookingValueSource.getValue()));
        this.setBookingConversionRate(bookingConversionRate);

        MonetaryValue additionalCharges = new MonetaryValue();
        additionalCharges.addListener(changeListener);
        additionalCharges.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
        this.setAdditionalCharges(additionalCharges);

        MonetaryValue additionalFinanceTax = new MonetaryValue();
        additionalFinanceTax.addListener(changeListener);
        additionalFinanceTax.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
        this.setAdditionalFinanceTax(additionalFinanceTax);

        MonetaryValue additionalSolidarityTax = new MonetaryValue();
        additionalSolidarityTax.addListener(changeListener);
        additionalSolidarityTax.addListener((o, oldValue, newValue) -> this.recomputeTotalValue());
        this.setAdditionalSolidarityTax(additionalSolidarityTax);

        MonetaryValue totalValue = new MonetaryValue();
        this.setTotalValue(totalValue);

        ObservableList<String> availableCurrencies = FXCollections.observableArrayList();
        if (StringUtils.isNotEmpty(bookingValue.getCurrency().getValue())) {
            availableCurrencies.add(bookingValue.getCurrency().getValue());
        }
        this.setAvailableCurrencies(availableCurrencies);

        BooleanProperty persistent = new SimpleBooleanProperty();
        persistent.addListener(changeListener);
        this.setPersistent(persistent);

    }

    private void recomputeBookingValue(MonetaryValue inputValue) {
        MonetaryValue targetValue = this.getBookingValue();
        BigDecimal inputAmount = inputValue.getAmount().getValue();
        if (inputAmount == null) {
            targetValue.getAmount().setValue(null);
        } else {
            String inputCurrency = inputValue.getCurrency().getValue();
            String bookingCurrency = targetValue.getCurrency().getValue();
            if (Objects.equals(inputCurrency, bookingCurrency)) {
                targetValue.getAmount().setValue(inputValue.getAmount().getValue());
                this.getBookingConversionRate().setValue(null);
            } else {
                BigDecimal currencyConversionRate = this.getBookingConversionRate().getValue();
                if (currencyConversionRate == null) {
                    targetValue.getAmount().setValue(null);
                } else {
                    targetValue.getAmount().setValue(currencyConversionRate.multiply(inputValue.getAmount().getValue()));
                }
            }
        }
    }

    private void recomputeTotalValue() {
        String bookingCurrency = this.getBookingValue().getCurrency().getValue();
        BigDecimal targetAmount = this.getBookingValue().getAmount().getValue();
        List<MonetaryValue> additionalValues = List.of(this.getAdditionalCharges(), this.getAdditionalFinanceTax(), this.getAdditionalSolidarityTax());
        for (MonetaryValue additionalValue : additionalValues) {
            BigDecimal additionalValueAmountInSourceCurrency = additionalValue.getAmount().getValue();
            BigDecimal additionalValueAmountInBookingCurrency = null;
            if (Objects.equals(additionalValue.getCurrency().getValue(), bookingCurrency)) {
                additionalValueAmountInBookingCurrency = additionalValueAmountInSourceCurrency;
            } else {
                BigDecimal conversionRate = this.getBookingConversionRate().getValue();
                if (conversionRate != null) {
                    additionalValueAmountInBookingCurrency = additionalValueAmountInSourceCurrency.multiply(conversionRate);
                }
            }
            if (additionalValueAmountInBookingCurrency != null) {
                TransactionType type = this.getType().getValue();
                BigDecimal targetAmountChangeFactor = type == null ? BigDecimal.ONE : BigDecimal.valueOf(type.getChargesFactor());
                BigDecimal targetAmountChange = additionalValueAmountInBookingCurrency.multiply(targetAmountChangeFactor);
                targetAmount = targetAmount == null ? targetAmountChange : targetAmount.add(targetAmountChange);
            }
        }
        this.getTotalValue().getCurrency().setValue(bookingCurrency);
        this.getTotalValue().getAmount().setValue(targetAmount);
    }

    private void recomputeAvailableCurrencies(String oldCurrencyValue, MonetaryValue inputValue) {
        Set<String> availableCurrencies = new LinkedHashSet<>();
        if (StringUtils.isNotEmpty(this.getBookingValue().getCurrency().getValue())) {
            availableCurrencies.add(this.getBookingValue().getCurrency().getValue());
        }
        if (StringUtils.isNotEmpty(inputValue.getCurrency().getValue())) {
            availableCurrencies.add(inputValue.getCurrency().getValue());
        }
        if (availableCurrencies.isEmpty()) {
            availableCurrencies.add("EUR");
        }
        if (!this.getAvailableCurrencies().containsAll(availableCurrencies) || this.getAvailableCurrencies().size() != availableCurrencies.size()) {
            this.getAvailableCurrencies().setAll(availableCurrencies);
        }
        for (MonetaryValue additionalValue : List.of(this.getAdditionalCharges(), this.getAdditionalFinanceTax(), this.getAdditionalSolidarityTax())) {
            if (oldCurrencyValue != null && Objects.equals(oldCurrencyValue, additionalValue.getCurrency().getValue())) {
                additionalValue.getCurrency().setValue(inputValue.getCurrency().getValue());
            }
            if (!availableCurrencies.contains(additionalValue.getCurrency().getValue())) {
                additionalValue.getCurrency().setValue(availableCurrencies.iterator().next());
            }
        }
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

    public MonetaryValue getStockPrice() {
        return this.stockPrice;
    }
    private void setStockPrice(MonetaryValue stockPrice) {
        this.stockPrice = stockPrice;
    }

    public MonetaryValue getStockValue() {
        return this.stockValue;
    }
    private void setStockValue(MonetaryValue stockValue) {
        this.stockValue = stockValue;
    }

    public MonetaryValue getPayoutValue() {
        return this.payoutValue;
    }
    private void setPayoutValue(MonetaryValue payoutValue) {
        this.payoutValue = payoutValue;
    }

    public MonetaryValue getBookingValue() {
        return this.bookingValue;
    }
    private void setBookingValue(MonetaryValue bookingValue) {
        this.bookingValue = bookingValue;
    }

    public ObjectProperty<BigDecimal> getBookingConversionRate() {
        return this.bookingConversionRate;
    }
    private void setBookingConversionRate(ObjectProperty<BigDecimal> bookingConversionRate) {
        this.bookingConversionRate = bookingConversionRate;
    }

    public MonetaryValue getAdditionalCharges() {
        return this.additionalCharges;
    }
    private void setAdditionalCharges(MonetaryValue additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public MonetaryValue getAdditionalFinanceTax() {
        return this.additionalFinanceTax;
    }
    private void setAdditionalFinanceTax(MonetaryValue additionalFinanceTax) {
        this.additionalFinanceTax = additionalFinanceTax;
    }

    public MonetaryValue getAdditionalSolidarityTax() {
        return this.additionalSolidarityTax;
    }
    private void setAdditionalSolidarityTax(MonetaryValue additionalSolidarityTax) {
        this.additionalSolidarityTax = additionalSolidarityTax;
    }

    public MonetaryValue getTotalValue() {
        return this.totalValue;
    }
    private void setTotalValue(MonetaryValue totalValue) {
        this.totalValue = totalValue;
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
