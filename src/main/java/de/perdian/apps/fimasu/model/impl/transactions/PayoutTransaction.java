package de.perdian.apps.fimasu.model.impl.transactions;

import de.perdian.apps.fimasu.model.Transaction;
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

}
