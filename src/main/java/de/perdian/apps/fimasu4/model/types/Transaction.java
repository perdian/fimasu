package de.perdian.apps.fimasu4.model.types;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public class Transaction implements Serializable {

    static final long serialVersionUID = 1L;

    private ObjectProperty<TransactionType> type = null;
    private BooleanProperty persistent = null;
    private StockIdentfier stockIdentifier = null;
    private ObjectProperty<BigDecimal> stockCount = null;
    private MonetaryValue stockPrice = null;
    private MonetaryValue stockValue = null;
    private MonetaryValue payoutValue = null;
    private ObjectProperty<MonetaryValue> bookingValueSource = null;
    private MonetaryValue bookingValue = null;
    private ObjectProperty<LocalDate> bookingDate = null;
    private ObjectProperty<LocalDate> valutaDate = null;
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
        this.setBookingValueSource(bookingValueSource);

        ObjectProperty<TransactionType> type = new SimpleObjectProperty<>();
        type.addListener((o, oldValue, newValue) -> {
            if (List.of(TransactionType.BUY, TransactionType.SELL).contains(newValue)) {
                this.getBookingValueSource().setValue(this.getStockValue());
            } else if (List.of(TransactionType.PAYOUT).contains(newValue)) {
                this.getBookingValueSource().setValue(this.getPayoutValue());
            } else {
                this.setBookingValueSource(null);
            }
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
        stockValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getBookingValueSource().getValue()));
        this.setStockValue(stockValue);

        MonetaryValue payoutValue = new MonetaryValue();
        payoutValue.addListener(changeListener);
        payoutValue.addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getBookingValueSource().getValue()));
        this.setPayoutValue(payoutValue);

        MonetaryValue bookingValue = new MonetaryValue();
        bookingValue.addListener(changeListener);
        bookingValue.getCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getBookingValueSource().getValue()));
        bookingValue.getConversionRate().addListener((o, oldValue, newValue) -> this.recomputeBookingValue(this.getBookingValueSource().getValue()));
        this.setBookingValue(bookingValue);

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
                targetValue.getConversionRate().setValue(null);
            } else {
                BigDecimal currencyConversionRate = this.getBookingValue().getConversionRate().getValue();
                if (currencyConversionRate == null) {
                    targetValue.getAmount().setValue(null);
                } else {
                    targetValue.getAmount().setValue(currencyConversionRate.multiply(inputValue.getAmount().getValue()));
                }
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

    private ObjectProperty<MonetaryValue> getBookingValueSource() {
        return this.bookingValueSource;
    }
    private void setBookingValueSource(ObjectProperty<MonetaryValue> bookingValueSource) {
        this.bookingValueSource = bookingValueSource;
    }

    public MonetaryValue getBookingValue() {
        return this.bookingValue;
    }
    private void setBookingValue(MonetaryValue bookingValue) {
        this.bookingValue = bookingValue;
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
