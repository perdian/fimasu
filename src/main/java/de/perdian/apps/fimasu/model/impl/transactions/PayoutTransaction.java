package de.perdian.apps.fimasu.model.impl.transactions;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu4.quicken.Record;
import de.perdian.apps.fimasu4.quicken.model.TransactionTypeRecordItem;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public class PayoutTransaction extends Transaction {

    @Override
    protected double computeChargesFactor() {
        return 1;
    }

    @Override
    public BooleanExpression computeTaxesEditable() {
        return new ReadOnlyBooleanWrapper(true);
    }

    @Override
    public BooleanExpression computeMarketAmountEditable() {
        return new ReadOnlyBooleanWrapper(true);
    }

    @Override
    public Record toQifRecord(TransactionGroup parentGroup) {
        Record qifRecord = super.toQifRecord(parentGroup);
        qifRecord.setTransactionType(TransactionTypeRecordItem.PAYOUT);
        return qifRecord;
    }

}
