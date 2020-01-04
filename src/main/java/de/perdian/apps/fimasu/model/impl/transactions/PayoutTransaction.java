package de.perdian.apps.fimasu.model.impl.transactions;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.apps.fimasu.support.quicken.QIFWriter;

public class PayoutTransaction extends Transaction {

    @Override
    protected double computeChargesFactor() {
        return 1;
    }

    @Override
    protected void appendToQIF(QIFWriter qifWriter, TransactionGroup parentGroup) {
        throw new UnsupportedOperationException();
    }

}
