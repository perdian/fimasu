package de.perdian.apps.fimasu.model.impl.transactions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.model.TransactionHelper;
import de.perdian.apps.fimasu.support.quicken.Record;
import de.perdian.apps.fimasu.support.quicken.model.ConversionFactorRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.MarketPriceRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.MemoRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.NumberOfSharesRecordItem;
import de.perdian.commons.fx.persistence.PersistenceHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
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
    public BooleanBinding computeTaxesEditable() {
        return Bindings.equal(StockChangeType.SELL, this.getType());
    }

    @Override
    public Record toQifRecord(TransactionGroup parentGroup) {
        if (this.getTotalAmount().getValue() != null && this.getTotalAmount().getValue().doubleValue() > 0d && this.getNumberOfShares().getValue() != null && this.getNumberOfShares().getValue().doubleValue() > 0d) {

            NumberFormat shortNumberFormat = new DecimalFormat("#,##0.00");
            NumberFormat longNumberFormat = new DecimalFormat("#,##0.00000");
            StringBuilder memo = new StringBuilder();
            memo.append(shortNumberFormat.format(this.getNumberOfShares().getValue())).append(" a ");
            memo.append(longNumberFormat.format(this.getMarketPrice().getValue())).append(" ").append(this.getMarketCurrency().getValue()).append(" = ");
            memo.append(shortNumberFormat.format(this.getMarketAmount().getValue())).append(" ").append(this.getMarketCurrency().getValue());
            if (!Objects.equals(this.getMarketCurrency().getValue(), this.getBookingCurrency().getValue())) {
                memo.append(" (= ").append(shortNumberFormat.format(this.getBookingAmount().getValue())).append(" ").append(this.getBookingCurrency().getValue()).append(")");
            }

            Record qifRecord = super.toQifRecord(parentGroup);
            qifRecord.setConversionFactor(new ConversionFactorRecordItem(this.getMarketExchangeRate().getValue() == null || this.getMarketExchangeRate().getValue().doubleValue() == 0d ? 1d : this.getMarketExchangeRate().getValue()));
            qifRecord.setMarketPrice(new MarketPriceRecordItem(this.getMarketPrice().getValue()));
            qifRecord.setNumberOfShares(new NumberOfSharesRecordItem(this.getNumberOfShares().getValue()));
            qifRecord.setMemo(new MemoRecordItem(memo.toString()));
            qifRecord.setTransactionType(this.getType().getValue().getTransactionTypeRecordItem());
            return qifRecord;

        } else {
            return null;
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
