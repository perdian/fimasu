package de.perdian.apps.fimasu.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.export.quicken.QIFWriter;
import de.perdian.apps.fimasu.persistence.PersistenceHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public abstract class Transaction {

    static final long serialVersionUID = 1L;

    private final StringProperty wkn = new SimpleStringProperty();
    private final StringProperty isin = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty marketCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty marketAmount = new SimpleDoubleProperty();
    private final DoubleProperty marketExchangeRate = new SimpleDoubleProperty();
    private final StringProperty bookingCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty bookingAmount = new SimpleDoubleProperty();
    private final DoubleProperty chargesAmount = new SimpleDoubleProperty();
    private final StringProperty chargesCurrency = new SimpleStringProperty();
    private final DoubleProperty financeTaxAmount = new SimpleDoubleProperty();
    private final StringProperty financeTaxCurrency = new SimpleStringProperty();
    private final DoubleProperty solidarityTaxAmount = new SimpleDoubleProperty();
    private final StringProperty solidarityTaxCurrency = new SimpleStringProperty();
    private final DoubleProperty totalAmount = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> bookingDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> valutaDate = new SimpleObjectProperty<>();
    private final BooleanProperty persistent = new SimpleBooleanProperty(false);
    private final List<ChangeListener<Transaction>> changeListeners = new CopyOnWriteArrayList<>();

    public Transaction() {

        this.getWkn().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getIsin().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTitle().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getChargesAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTaxAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTaxAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getTotalAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getBookingDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getValutaDate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getPersistent().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getBookingDate().addListener((o, oldValue, newValue) -> this.recomputeValutaDate(newValue));

        this.getMarketAmount().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(newValue, this.getMarketCurrency().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue()));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getMarketAmount().getValue(), newValue, this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue()));
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getMarketAmount().getValue(), this.getMarketCurrency().getValue(), newValue, this.getBookingCurrency().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getMarketAmount().getValue(), this.getMarketCurrency().getValue(), this.getMarketExchangeRate().getValue(), newValue));

        this.getBookingAmount().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(newValue, this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), newValue, this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), newValue, this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getChargesAmount().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), newValue, this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), newValue, this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTaxAmount().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), newValue, this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), newValue, this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTaxAmount().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), newValue, this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), newValue));

        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(newValue, this.getMarketCurrency().getValue())));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(this.getBookingCurrency().getValue(), newValue)));

    }

    protected void recomputeValutaDate(LocalDate bookingDate) {
        if (bookingDate == null) {
            this.getValutaDate().setValue(null);
        } else if (this.getValutaDate().getValue() == null) {
            LocalDate nextValutaDate = bookingDate.plusDays(2);
            if (bookingDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                nextValutaDate = nextValutaDate.plusDays(2);
            } else if (bookingDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                nextValutaDate = nextValutaDate.plusDays(1);
            }
            this.getValutaDate().setValue(nextValutaDate);
        }
    }

    protected void recomputeBookingAmount(Number marketAmount, String marketCurrency, Number marketExchangeRate, String bookingCurrency) {
        if (marketAmount == null || marketAmount.doubleValue() == 0d) {
            this.getBookingAmount().setValue(null);
        } else if (Objects.equals(marketCurrency, bookingCurrency)) {
            this.getBookingAmount().setValue(marketAmount);
        } else if (marketExchangeRate == null || marketExchangeRate.doubleValue() == 0) {
            this.getBookingAmount().setValue(null);
        } else {
            this.getBookingAmount().setValue(marketAmount.doubleValue() / marketExchangeRate.doubleValue());
        }
    }

    protected void recomputeTotalAmount(Number bookingAmount, Number marketExchangeRate, String bookingCurrency, Number chargesAmount, String chargesCurrency, Number financeTaxAmount, String financeTaxCurrency, Number solidarityTaxAmount, String solidarityTaxCurrency) {
        if (bookingAmount == null || bookingAmount.doubleValue() == 0d) {
            this.getTotalAmount().setValue(null);
        } else {
            double chargesFactor = this.computeChargesFactor();
            double totalAmount = bookingAmount.doubleValue();
            totalAmount += TransactionHelper.convert(chargesAmount, StringUtils.defaultIfEmpty(chargesCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * chargesFactor;
            totalAmount += TransactionHelper.convert(financeTaxAmount, StringUtils.defaultIfEmpty(financeTaxCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * chargesFactor;
            totalAmount += TransactionHelper.convert(solidarityTaxAmount, StringUtils.defaultIfEmpty(solidarityTaxCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * chargesFactor;
            this.getTotalAmount().setValue(totalAmount == 0d ? null : Double.valueOf(totalAmount));
        }
    }

    protected abstract double computeChargesFactor();

    protected void recomputeCurrencies(List<String> inputValues) {
        List<String> consolidatedCurrencies = inputValues.stream().filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        List.of(this.getChargesCurrency(), this.getFinanceTaxCurrency(), this.getSolidarityTaxCurrency()).stream().forEach(property -> {
            if (StringUtils.isEmpty(property.getValue()) || !consolidatedCurrencies.contains(property.getValue())) {
                property.setValue(consolidatedCurrencies.isEmpty() ? null : consolidatedCurrencies.get(0));
            }
        });
    }

    protected void loadFromXML(Element transactionElement) {
        this.getTitle().setValue(PersistenceHelper.extractAttributeString(transactionElement, "title").orElse(null));
        this.getWkn().setValue(PersistenceHelper.extractAttributeString(transactionElement, "wkn").orElse(null));
        this.getIsin().setValue(PersistenceHelper.extractAttributeString(transactionElement, "isin").orElse(null));
        this.getBookingCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "bookingCurrency").orElse("EUR"));
        this.getMarketCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "marketCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getChargesAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "chargesAmount").orElse(null));
        this.getChargesCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "chargesCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getFinanceTaxAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "financeTaxAmount").orElse(null));
        this.getFinanceTaxCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "financeTaxCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getSolidarityTaxAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "solidarityTaxAmount").orElse(null));
        this.getSolidarityTaxCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "solidarityTaxTaxCurrency").orElse(this.getBookingCurrency().getValue()));
    }

    protected void appendToXML(Element transactionElement, Document document) {
        PersistenceHelper.appendAttribute(transactionElement, "title", this.getTitle().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "wkn", this.getWkn().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "isin", this.getIsin().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "bookingCurrency", this.getBookingCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "marketCurrency", this.getMarketCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "chargesAmount", this.getChargesAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "chargesCurrency", this.getChargesCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "financeTaxAmount", this.getFinanceTaxAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "financeTexCurrency", this.getFinanceTaxCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "solidarityTaxAmount", this.getSolidarityTaxAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "solidarityTaxCurrency", this.getSolidarityTaxCurrency().getValue());
    }

    protected abstract void appendToQIF(QIFWriter qifWriter, TransactionGroup parentGroup);

    public void copyValuesInto(Transaction targetTransaction) {
        Optional.ofNullable(this.getBookingDate().getValue()).ifPresent(targetTransaction.getBookingDate()::setValue);
        Optional.ofNullable(this.getValutaDate().getValue()).ifPresent(targetTransaction.getValutaDate()::setValue);
        if (StringUtils.isEmpty(targetTransaction.getTitle().getValue())) {
            Optional.ofNullable(this.getTitle().getValue()).ifPresent(targetTransaction.getTitle()::setValue);
        }
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getBookingCurrency, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getChargesAmount, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getChargesCurrency, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getFinanceTaxAmount, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getFinanceTaxCurrency, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getIsin, StringUtils::isEmpty);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getMarketAmount, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getMarketCurrency, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getMarketExchangeRate, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getSolidarityTaxAmount, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getSolidarityTaxCurrency, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getTotalAmount, existingValue -> true);
        TransactionHelper.copyValue(this, targetTransaction, Transaction::getWkn, StringUtils::isEmpty);
    }

    public StringProperty getWkn() {
        return this.wkn;
    }


    public StringProperty getIsin() {
        return this.isin;
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public StringProperty getMarketCurrency() {
        return this.marketCurrency;
    }

    public DoubleProperty getMarketAmount() {
        return this.marketAmount;
    }

    public DoubleProperty getMarketExchangeRate() {
        return this.marketExchangeRate;
    }

    public StringProperty getBookingCurrency() {
        return this.bookingCurrency;
    }

    public DoubleProperty getBookingAmount() {
        return this.bookingAmount;
    }

    public DoubleProperty getChargesAmount() {
        return this.chargesAmount;
    }

    public StringProperty getChargesCurrency() {
        return this.chargesCurrency;
    }

    public DoubleProperty getFinanceTaxAmount() {
        return this.financeTaxAmount;
    }

    public StringProperty getFinanceTaxCurrency() {
        return this.financeTaxCurrency;
    }

    public DoubleProperty getSolidarityTaxAmount() {
        return this.solidarityTaxAmount;
    }

    public StringProperty getSolidarityTaxCurrency() {
        return this.solidarityTaxCurrency;
    }

    public DoubleProperty getTotalAmount() {
        return this.totalAmount;
    }

    public ObjectProperty<LocalDate> getBookingDate() {
        return this.bookingDate;
    }

    public ObjectProperty<LocalDate> getValutaDate() {
        return this.valutaDate;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }

    public List<ChangeListener<Transaction>> getChangeListeners() {
        return this.changeListeners;
    }

    protected void fireChange() {
        for (ChangeListener<Transaction> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    public void addChangeListener(ChangeListener<Transaction> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    public void removeChangeListener(ChangeListener<Transaction> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
