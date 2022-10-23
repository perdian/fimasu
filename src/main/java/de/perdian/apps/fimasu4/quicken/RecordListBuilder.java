package de.perdian.apps.fimasu4.quicken;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.perdian.apps.fimasu4.model.types.Transaction;
import de.perdian.apps.fimasu4.model.types.TransactionGroup;
import de.perdian.apps.fimasu4.model.types.TransactionType;
import de.perdian.apps.fimasu4.quicken.model.AccountRecordItem;
import de.perdian.apps.fimasu4.quicken.model.BookedAmountRecordItem;
import de.perdian.apps.fimasu4.quicken.model.BookingDateRecordItem;
import de.perdian.apps.fimasu4.quicken.model.CommissionRecordItem;
import de.perdian.apps.fimasu4.quicken.model.ConversionFactorRecordItem;
import de.perdian.apps.fimasu4.quicken.model.CurrencyRecordItem;
import de.perdian.apps.fimasu4.quicken.model.IsinRecordItem;
import de.perdian.apps.fimasu4.quicken.model.MarketPriceRecordItem;
import de.perdian.apps.fimasu4.quicken.model.MemoRecordItem;
import de.perdian.apps.fimasu4.quicken.model.NumberOfSharesRecordItem;
import de.perdian.apps.fimasu4.quicken.model.SecurityRecordItem;
import de.perdian.apps.fimasu4.quicken.model.TotalAmountRecordItem;
import de.perdian.apps.fimasu4.quicken.model.TransactionTypeRecordItem;
import de.perdian.apps.fimasu4.quicken.model.ValutaDateRecordItem;
import de.perdian.apps.fimasu4.quicken.model.WknRecordItem;

public class RecordListBuilder {

    public RecordList buildRecordList(TransactionGroup transactionGroup) {
        List<Record> records = new ArrayList<>(transactionGroup.getTransactions().size());
        for (Transaction transaction : transactionGroup.getTransactions()) {
            records.add(this.buildRecord(transactionGroup, transaction));
        }
        return new RecordList(records);
    }

    private Record buildRecord(TransactionGroup transactionGroup, Transaction transaction) {
        Record qifRecord = new Record();
        qifRecord.setAccount(new AccountRecordItem(transactionGroup.getBankAccountName().getValue()));
        qifRecord.setBookingDate(new BookingDateRecordItem(transaction.getBookingDate().getValue()));
        qifRecord.setValutaDate(new ValutaDateRecordItem(transaction.getValutaDate().getValue()));
        qifRecord.setCurrency(new CurrencyRecordItem(transaction.getStockPrice().getCurrency().getValue()));
        qifRecord.setSecurity(new SecurityRecordItem(transaction.getStockIdentifier().getTitle().getValue()));
        qifRecord.setWkn(new WknRecordItem(transaction.getStockIdentifier().getWkn().getValue()));
        qifRecord.setIsin(new IsinRecordItem(transaction.getStockIdentifier().getIsin().getValue()));
        qifRecord.setBookedAmount(new BookedAmountRecordItem(transaction.getTotalValue().getAmount().getValue()));
        qifRecord.setTotalAmount(new TotalAmountRecordItem(transaction.getTotalValue().getAmount().getValue()));
        qifRecord.setCommission(this.buildCommissionRecordItem(transaction));

        if (TransactionType.BUY.equals(transaction.getType().getValue()) || TransactionType.SELL.equals(transaction.getType().getValue())) {
            if (transaction.getTotalValue().getAmount().getValue() != null && transaction.getTotalValue().getAmount().getValue().doubleValue() > 0d && transaction.getStockCount().getValue() != null && transaction.getStockCount().getValue().doubleValue() > 0d) {

                NumberFormat shortNumberFormat = new DecimalFormat("#,##0.00");
                NumberFormat longNumberFormat = new DecimalFormat("#,##0.00000");
                StringBuilder memo = new StringBuilder();
                memo.append(shortNumberFormat.format(transaction.getStockCount().getValue())).append(" a ");
                memo.append(longNumberFormat.format(transaction.getStockPrice().getAmount().getValue())).append(" ").append(transaction.getStockPrice().getCurrency().getValue()).append(" = ");
                memo.append(shortNumberFormat.format(transaction.getStockValue().getAmount().getValue())).append(" ").append(transaction.getStockValue().getCurrency().getValue());
                if (!Objects.equals(transaction.getStockPrice().getCurrency().getValue(), transaction.getBookingValue().getCurrency().getValue())) {
                    memo.append(" (= ").append(shortNumberFormat.format(transaction.getBookingValue().getAmount().getValue())).append(" ").append(transaction.getBookingValue().getCurrency().getValue()).append(")");
                }

                qifRecord.setConversionFactor(new ConversionFactorRecordItem(transaction.getBookingConversionRate().getValue() == null || transaction.getBookingConversionRate().getValue().doubleValue() == 0d ? 1d : transaction.getBookingConversionRate().getValue()));
                qifRecord.setMarketPrice(new MarketPriceRecordItem(transaction.getStockPrice().getAmount().getValue()));
                qifRecord.setNumberOfShares(new NumberOfSharesRecordItem(transaction.getStockCount().getValue()));
                qifRecord.setMemo(new MemoRecordItem(memo.toString()));
                qifRecord.setTransactionType(TransactionType.BUY.equals(transaction.getType().getValue()) ? TransactionTypeRecordItem.BUY : TransactionTypeRecordItem.SELL);

            } else if (TransactionType.PAYOUT.equals(transaction.getType().getValue())) {
                qifRecord.setTransactionType(TransactionTypeRecordItem.PAYOUT);
            }
        }

        return qifRecord;
    }

    private CommissionRecordItem buildCommissionRecordItem(Transaction transaction) {
        CommissionRecordItem commissionRecordItem = new CommissionRecordItem();
        commissionRecordItem.setKapitalertragsteuer(this.convert(transaction.getAdditionalFinanceTax().getAmount().getValue(), transaction.getAdditionalFinanceTax().getCurrency().getValue(), transaction.getBookingConversionRate().getValue(), transaction.getBookingValue().getCurrency().getValue()));
        commissionRecordItem.setBankprovision(transaction.getAdditionalCharges().getAmount().getValue() == null || transaction.getAdditionalCharges().getAmount().getValue().doubleValue() <= 0d ? null : this.convert(transaction.getAdditionalCharges().getAmount().getValue(), transaction.getAdditionalCharges().getCurrency().getValue(), transaction.getBookingConversionRate().getValue(), transaction.getBookingValue().getCurrency().getValue()));
        commissionRecordItem.setSonstigekosten(transaction.getAdditionalCharges().getAmount().getValue() != null && transaction.getAdditionalCharges().getAmount().getValue().doubleValue() > 0d ? null : this.convert(transaction.getAdditionalCharges().getAmount().getValue(), transaction.getAdditionalCharges().getCurrency().getValue(), transaction.getBookingConversionRate().getValue(), transaction.getBookingValue().getCurrency().getValue()));
        commissionRecordItem.setSolidaritaetsuzschlag(this.convert(transaction.getAdditionalSolidarityTax().getAmount().getValue(), transaction.getAdditionalSolidarityTax().getCurrency().getValue(), transaction.getBookingConversionRate().getValue(), transaction.getBookingValue().getCurrency().getValue()));
        return commissionRecordItem;
    }

    private double convert(BigDecimal sourceValue, String sourceCurrency, BigDecimal exchangeRate, String targetCurrency) {
        if (sourceValue == null || sourceValue.doubleValue() == 0d) {
            return 0d;
        } else if (Objects.equals(sourceCurrency, targetCurrency)) {
            return sourceValue.doubleValue();
        } else if (exchangeRate == null || exchangeRate.doubleValue() == 0d) {
            return 0d;
        } else {
            return sourceValue.doubleValue() / exchangeRate.doubleValue();
        }
    }

}
