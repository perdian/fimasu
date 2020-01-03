package de.perdian.apps.fimasu.model.impl.transactions;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.export.quicken.QIFWriter;
import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.persistence.PersistenceHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockChangeTransaction extends Transaction {

    private final ObjectProperty<StockChangeType> type = new SimpleObjectProperty<>(StockChangeType.BUY);
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final StringProperty marketCurrency = new SimpleStringProperty("EUR");
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();
    private final DoubleProperty marketAmount = new SimpleDoubleProperty();
    private final DoubleProperty marketAmountConverted = new SimpleDoubleProperty();
    private final DoubleProperty marketExchangeRate = new SimpleDoubleProperty();
    private final DoubleProperty chargesAmount = new SimpleDoubleProperty();
    private final StringProperty chargesCurrency = new SimpleStringProperty();
    private final DoubleProperty financeTaxAmount = new SimpleDoubleProperty();
    private final StringProperty financeTaxCurrency = new SimpleStringProperty();
    private final DoubleProperty solidarityTaxAmount = new SimpleDoubleProperty();
    private final StringProperty solidarityTaxCurrency = new SimpleStringProperty();

    public StockChangeTransaction() {

        this.getType().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getChargesAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTaxAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTaxAmount().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.recomputeMarketAmount(newValue, this.getMarketPrice().getValue()));
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.recomputeMarketAmount(this.getNumberOfShares().getValue(), newValue));

        this.getMarketAmount().addListener((o, oldValue, newValue) -> this.recomputeMarketAmountConverted(newValue, this.getMarketCurrency().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue()));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeMarketAmountConverted(this.getMarketAmount().getValue(), newValue, this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue()));
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeMarketAmountConverted(this.getMarketAmount().getValue(), this.getMarketCurrency().getValue(), newValue, this.getBookingCurrency().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeMarketAmountConverted(this.getMarketAmount().getValue(), this.getMarketCurrency().getValue(), this.getMarketExchangeRate().getValue(), newValue));

        this.getType().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(newValue, this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getMarketAmountConverted().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), newValue, this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getMarketExchangeRate().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), newValue, this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), newValue, this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getChargesAmount().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), newValue, this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getChargesCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), newValue, this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTaxAmount().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), newValue, this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getFinanceTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), newValue, this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTaxAmount().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), newValue, this.getSolidarityTaxCurrency().getValue()));
        this.getSolidarityTaxCurrency().addListener((o, oldValue, newValue) -> this.recomputeBookingAmount(this.getType().getValue(), this.getMarketAmountConverted().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), newValue));

        this.getBookingCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(newValue, this.getMarketCurrency().getValue())));
        this.getMarketCurrency().addListener((o, oldValue, newValue) -> this.recomputeCurrencies(List.of(this.getBookingCurrency().getValue(), newValue)));

    }

    private void recomputeMarketAmount(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.getMarketAmount().setValue(null);
        } else {
            this.getMarketAmount().setValue(numberOfShares.doubleValue() * marketPrice.doubleValue());
        }
    }

    private void recomputeMarketAmountConverted(Number marketAmount, String marketCurrency, Number marketExchangeRate, String bookingCurrency) {
        if (marketAmount == null || marketAmount.doubleValue() == 0d) {
            this.getMarketAmountConverted().setValue(null);
        } else if (Objects.equals(marketCurrency, bookingCurrency)) {
            this.getMarketAmountConverted().setValue(marketAmount);
        } else if (marketExchangeRate == null || marketExchangeRate.doubleValue() == 0) {
            this.getMarketAmountConverted().setValue(null);
        } else {
            this.getMarketAmountConverted().setValue(marketAmount.doubleValue() / marketExchangeRate.doubleValue());
        }
    }

    private void recomputeBookingAmount(StockChangeType type, Number marketAmountConverted, Number marketExchangeRate, String bookingCurrency, Number chargesAmount, String chargesCurrency, Number financeTaxAmount, String financeTaxCurrency, Number solidarityTaxAmount, String solidarityTaxCurrency) {
        if (marketAmountConverted == null || marketAmountConverted.doubleValue() == 0d) {
            this.getBookingAmount().setValue(null);
        } else {
            double bookingAmount = marketAmountConverted.doubleValue();
            bookingAmount += StockChangeHelper.convert(chargesAmount, StringUtils.defaultIfEmpty(chargesCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * type.getFactor();
            bookingAmount += StockChangeHelper.convert(financeTaxAmount, StringUtils.defaultIfEmpty(financeTaxCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * type.getFactor();
            bookingAmount += StockChangeHelper.convert(solidarityTaxAmount, StringUtils.defaultIfEmpty(solidarityTaxCurrency, bookingCurrency), marketExchangeRate, bookingCurrency) * type.getFactor();
            this.getBookingAmount().setValue(bookingAmount == 0d ? null : Double.valueOf(bookingAmount));
        }
    }

    private void recomputeCurrencies(List<String> inputValues) {
        List<String> consolidatedCurrencies = inputValues.stream().filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        List.of(this.getChargesCurrency(), this.getFinanceTaxCurrency(), this.getSolidarityTaxCurrency()).stream().forEach(property -> {
            if (StringUtils.isEmpty(property.getValue()) || !consolidatedCurrencies.contains(property.getValue())) {
                property.setValue(consolidatedCurrencies.isEmpty() ? null : consolidatedCurrencies.get(0));
            }
        });
    }

    @Override
    protected void loadFromXML(Element transactionElement) {
        super.loadFromXML(transactionElement);
        this.getPersistent().setValue(Boolean.TRUE);
        this.getBookingCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "bookingCurrency").orElse("EUR"));
        this.getMarketCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "marketCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getChargesAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "chargesAmount").orElse(null));
        this.getChargesCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "chargesCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getFinanceTaxAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "financeTaxAmount").orElse(null));
        this.getFinanceTaxCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "financeTaxCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getSolidarityTaxAmount().setValue(PersistenceHelper.extractAttributeDouble(transactionElement, "solidarityTaxAmount").orElse(null));
        this.getSolidarityTaxCurrency().setValue(PersistenceHelper.extractAttributeString(transactionElement, "solidarityTaxTaxCurrency").orElse(this.getBookingCurrency().getValue()));
        this.getType().setValue(PersistenceHelper.extractAttributeEnum(transactionElement, "type", StockChangeType.class).orElse(StockChangeType.BUY));
    }

    @Override
    protected void appendToXML(Element transactionElement, Document document) {
        super.appendToXML(transactionElement, document);
        PersistenceHelper.appendAttribute(transactionElement, "bookingCurrency", this.getBookingCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "marketCurrency", this.getMarketCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "chargesAmount", this.getChargesAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "chargesCurrency", this.getChargesCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "financeTaxAmount", this.getFinanceTaxAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "financeTexCurrency", this.getFinanceTaxCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "solidarityTaxAmount", this.getSolidarityTaxAmount().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "solidarityTaxCurrency", this.getSolidarityTaxCurrency().getValue());
        PersistenceHelper.appendAttribute(transactionElement, "type", this.getType().getValue().name());
    }

    @Override
    public void appendToQIF(QIFWriter qifWriter, TransactionGroup parentGroup) {
        if (this.getBookingAmount().getValue() != null && this.getBookingAmount().getValue().doubleValue() > 0d && this.getNumberOfShares().getValue() != null && this.getNumberOfShares().getValue().doubleValue() > 0d) {

            Number conversionFactorInput = this.getMarketExchangeRate().getValue();
            double conversionFactor = conversionFactorInput == null || conversionFactorInput.doubleValue() == 0d ? 1d : conversionFactorInput.doubleValue();

            StringBuilder memo = new StringBuilder();
            memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getNumberOfShares().getValue())).append(" a ");
            memo.append(QIFWriter.LONG_NUMBER_FORMAT.format(this.getMarketPrice().getValue())).append(" ").append(this.getMarketCurrency().getValue()).append(" = ");
            memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getMarketAmount().getValue())).append(" ").append(this.getMarketCurrency().getValue());
            if (!Objects.equals(this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue())) {
                memo.append(" (= ");
                memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getMarketAmountConverted().getValue())).append(" ").append(this.getBookingCurrency().getValue());
                memo.append(")");
            }

            String charges = this.computeQifCommision(this.getChargesAmount(), this.getChargesCurrency());
            StringBuilder commission = new StringBuilder();
            commission.append(""); // maklercourtage
            commission.append("|").append(this.computeQifCommision(this.getFinanceTaxAmount(), this.getFinanceTaxCurrency())); // kapitalertragsteuer
            commission.append("|"); // spesen
            commission.append("|").append(this.getChargesAmount().getValue() != null && this.getChargesAmount().getValue() > 0d ? charges : ""); // bankprovision
            commission.append("|"); // spesenausland
            commission.append("|"); // auslaendischequellensteuer
            commission.append("|"); // zinsabschlagsteuer
            commission.append("|").append(this.getChargesAmount().getValue() != null && this.getChargesAmount().getValue() < 0d ? charges : ""); // sonstigekosten
            commission.append("|").append(this.computeQifCommision(this.getSolidarityTaxAmount(), this.getSolidarityTaxCurrency())); // solidaritaetsuzschlag
            commission.append("|"); // boersenplatzentgelt
            commission.append("|"); // abgeltungssteuer
            commission.append("|"); // kirchensteuer

            qifWriter.appendLine('D', QIFWriter.DATE_FORMATTER.format(Optional.ofNullable(this.getBookingDate().getValue()).orElseGet(LocalDate::now)));
            qifWriter.appendLine('V', QIFWriter.DATE_FORMATTER.format(Optional.ofNullable(this.getValutaDate().getValue()).orElseGet(LocalDate::now)));
            qifWriter.appendLine('N', this.getType().getValue().getQifType());
            qifWriter.appendLine('F', StringUtils.defaultIfEmpty(this.getMarketCurrency().getValue(), "EUR"));
            qifWriter.appendLine('G', QIFWriter.LONG_NUMBER_FORMAT.format(conversionFactor));
            qifWriter.appendLine('Y', this.getTitle().getValue());
            qifWriter.appendLine('~', this.getWkn().getValue());
            qifWriter.appendLine('@', this.getIsin().getValue());
            qifWriter.appendLine('&', "2");
            qifWriter.appendLine('I', QIFWriter.LONG_NUMBER_FORMAT.format(this.getMarketPrice().getValue()));
            qifWriter.appendLine('Q', QIFWriter.LONG_NUMBER_FORMAT.format(this.getNumberOfShares().getValue()));
            qifWriter.appendLine('U', QIFWriter.SHORT_NUMBER_FORMAT.format(this.getBookingAmount().getValue()));
            qifWriter.appendLine('O', commission.toString());
            qifWriter.appendLine('L', "|[" + parentGroup.getAccount().getValue() + "]");
            qifWriter.appendLine('$', QIFWriter.SHORT_NUMBER_FORMAT.format(this.getBookingAmount().getValue()));
            qifWriter.appendLine('B', "0.00|0.00|0.00");
            qifWriter.appendLine('M', memo.toString());
            qifWriter.appendLine('^');

        }
    }

    private String computeQifCommision(Property<Number> doubleProperty, Property<String> currencyProperty) {
        if (doubleProperty.getValue() == null || doubleProperty.getValue().doubleValue() == 0d) {
            return "";
        } else {
            double convertedValue = StockChangeHelper.convert(doubleProperty.getValue(), currencyProperty.getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue());
            return convertedValue == 0d ? "" : QIFWriter.SHORT_NUMBER_FORMAT.format(convertedValue);
        }
    }

    @Override
    public void copyValuesInto(Transaction targetTransaction) {
        super.copyValuesInto(targetTransaction);
        if (targetTransaction instanceof StockChangeTransaction) {
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getBookingAmount, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getBookingCurrency, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getChargesAmount, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getChargesCurrency, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getFinanceTaxAmount, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getFinanceTaxCurrency, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getIsin, StringUtils::isEmpty);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getMarketAmount, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getMarketCurrency, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getMarketExchangeRate, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getMarketPrice, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getNumberOfShares, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getSolidarityTaxAmount, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getSolidarityTaxCurrency, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getType, existingValue -> true);
            StockChangeHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getWkn, StringUtils::isEmpty);
        }
    }

    public ObjectProperty<StockChangeType> getType() {
        return this.type;
    }

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }

    public StringProperty getMarketCurrency() {
        return this.marketCurrency;
    }

    public DoubleProperty getMarketPrice() {
        return this.marketPrice;
    }

    public DoubleProperty getMarketAmount() {
        return this.marketAmount;
    }

    public DoubleProperty getMarketAmountConverted() {
        return this.marketAmountConverted;
    }

    public DoubleProperty getMarketExchangeRate() {
        return this.marketExchangeRate;
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

}
