package de.perdian.apps.fimasu.model.impl.transactions;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.model.TransactionHelper;
import de.perdian.apps.fimasu.persistence.PersistenceHelper;
import de.perdian.apps.fimasu.support.quicken.QIFWriter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class StockChangeTransaction extends Transaction {

    private final ObjectProperty<StockChangeType> type = new SimpleObjectProperty<>(StockChangeType.BUY);
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();

    public StockChangeTransaction() {

        this.getType().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.fireChange());
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.fireChange());

        this.getNumberOfShares().addListener((o, oldValue, newValue) -> this.recomputeMarketAmount(newValue, this.getMarketPrice().getValue()));
        this.getMarketPrice().addListener((o, oldValue, newValue) -> this.recomputeMarketAmount(this.getNumberOfShares().getValue(), newValue));

        this.getType().addListener((o, oldValue, newValue) -> this.recomputeTotalAmount(this.getBookingAmount().getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue(), this.getChargesAmount().getValue(), this.getChargesCurrency().getValue(), this.getFinanceTaxAmount().getValue(), this.getFinanceTaxCurrency().getValue(), this.getSolidarityTaxAmount().getValue(), this.getSolidarityTaxCurrency().getValue()));

    }

    protected void recomputeMarketAmount(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.getMarketAmount().setValue(null);
        } else {
            this.getMarketAmount().setValue(numberOfShares.doubleValue() * marketPrice.doubleValue());
        }
    }

    @Override
    protected double computeChargesFactor() {
        return this.getType().getValue().getFactor();
    }

    @Override
    protected void loadFromXML(Element transactionElement) {
        super.loadFromXML(transactionElement);
        this.getType().setValue(PersistenceHelper.extractAttributeEnum(transactionElement, "type", StockChangeType.class).orElse(StockChangeType.BUY));
    }

    @Override
    protected void appendToXML(Element transactionElement, Document document) {
        super.appendToXML(transactionElement, document);
        PersistenceHelper.appendAttribute(transactionElement, "type", this.getType().getValue().name());
    }

    @Override
    public void appendToQIF(QIFWriter qifWriter, TransactionGroup parentGroup) {
        if (this.getTotalAmount().getValue() != null && this.getTotalAmount().getValue().doubleValue() > 0d && this.getNumberOfShares().getValue() != null && this.getNumberOfShares().getValue().doubleValue() > 0d) {

            Number conversionFactorInput = this.getMarketExchangeRate().getValue();
            double conversionFactor = conversionFactorInput == null || conversionFactorInput.doubleValue() == 0d ? 1d : conversionFactorInput.doubleValue();

            StringBuilder memo = new StringBuilder();
            memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getNumberOfShares().getValue())).append(" a ");
            memo.append(QIFWriter.LONG_NUMBER_FORMAT.format(this.getMarketPrice().getValue())).append(" ").append(this.getMarketCurrency().getValue()).append(" = ");
            memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getMarketAmount().getValue())).append(" ").append(this.getMarketCurrency().getValue());
            if (!Objects.equals(this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue())) {
                memo.append(" (= ");
                memo.append(QIFWriter.SHORT_NUMBER_FORMAT.format(this.getBookingAmount().getValue())).append(" ").append(this.getBookingCurrency().getValue());
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
            qifWriter.appendLine('U', QIFWriter.SHORT_NUMBER_FORMAT.format(this.getTotalAmount().getValue()));
            qifWriter.appendLine('O', commission.toString());
            qifWriter.appendLine('L', "|[" + parentGroup.getAccount().getValue() + "]");
            qifWriter.appendLine('$', QIFWriter.SHORT_NUMBER_FORMAT.format(this.getTotalAmount().getValue()));
            qifWriter.appendLine('B', "0.00|0.00|0.00");
            qifWriter.appendLine('M', memo.toString());
            qifWriter.appendLine('^');

        }
    }

    private String computeQifCommision(Property<Number> doubleProperty, Property<String> currencyProperty) {
        if (doubleProperty.getValue() == null || doubleProperty.getValue().doubleValue() == 0d) {
            return "";
        } else {
            double convertedValue = TransactionHelper.convert(doubleProperty.getValue(), currencyProperty.getValue(), this.getMarketExchangeRate().getValue(), this.getBookingCurrency().getValue());
            return convertedValue == 0d ? "" : QIFWriter.SHORT_NUMBER_FORMAT.format(convertedValue);
        }
    }

    @Override
    public void copyValuesInto(Transaction targetTransaction) {
        super.copyValuesInto(targetTransaction);
        if (targetTransaction instanceof StockChangeTransaction) {
            TransactionHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getMarketPrice, existingValue -> true);
            TransactionHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getNumberOfShares, existingValue -> true);
            TransactionHelper.copyValue(this, (StockChangeTransaction)targetTransaction, StockChangeTransaction::getType, existingValue -> true);
        }
    }

    public ObjectProperty<StockChangeType> getType() {
        return this.type;
    }

    public DoubleProperty getNumberOfShares() {
        return this.numberOfShares;
    }

    public DoubleProperty getMarketPrice() {
        return this.marketPrice;
    }

}
