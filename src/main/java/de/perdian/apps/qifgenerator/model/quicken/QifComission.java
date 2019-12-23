package de.perdian.apps.qifgenerator.model.quicken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import de.perdian.apps.qifgenerator.model.transactions.Transaction;

class QifComission {

    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    private Double maklercourtage = null;
    private Double kapitalertragsteuer = null;
    private Double spesen = null;
    private Double bankprovision = null;
    private Double spesenausland = null;
    private Double auslaendischequellensteuer = null;
    private Double zinsabschlagsteuer = null;
    private Double sonstigekosten = null;
    private Double solidaritaetsuzschlag = null;
    private Double boersenplatzentgelt = null;
    private Double abgeltungssteuer = null;
    private Double kirchensteuer = null;

    static QifComission compute(Transaction transaction) {

        QifComission commission = new QifComission();

        double charges = QifComission.computeValueInBookingCurrency(transaction.getCharges().getValue() == null ? 0d : transaction.getCharges().getValue().doubleValue(), transaction.getChargesCurrency().getValue(), transaction);
        if (charges > 0) {
            commission.bankprovision = charges;
        } else if (charges < 0) {
            commission.sonstigekosten = charges;
        }

        if (transaction.getFinanceTax().getValue() != null) {
            commission.kapitalertragsteuer = QifComission.computeValueInBookingCurrency(transaction.getFinanceTax().getValue().doubleValue(), transaction.getFinanceTaxCurrency().getValue(), transaction);
        }
        if (transaction.getSolidarityTax().getValue() != null) {
            commission.solidaritaetsuzschlag = QifComission.computeValueInBookingCurrency(transaction.getSolidarityTax().getValue().doubleValue(), transaction.getSolidarityTaxCurrency().getValue(), transaction);
        }
        return commission;

    }

    private static double computeValueInBookingCurrency(double value, String sourceCurrency, Transaction transaction) {
        if (Objects.equals(sourceCurrency, transaction.getBookingCurrency().getValue())) {
            return value;
        } else {
            return value / transaction.getBookingCurrencyExchangeRate().doubleValue();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.toString(this.maklercourtage));
        result.append("|").append(this.toString(this.kapitalertragsteuer));
        result.append("|").append(this.toString(this.spesen));
        result.append("|").append(this.toString(this.bankprovision));
        result.append("|").append(this.toString(this.spesenausland));
        result.append("|").append(this.toString(this.auslaendischequellensteuer));
        result.append("|").append(this.toString(this.zinsabschlagsteuer));
        result.append("|").append(this.toString(this.sonstigekosten));
        result.append("|").append(this.toString(this.solidaritaetsuzschlag));
        result.append("|").append(this.toString(this.boersenplatzentgelt));
        result.append("|").append(this.toString(this.abgeltungssteuer));
        result.append("|").append(this.toString(this.kirchensteuer));
        return result.toString();
    }

    private String toString(Double value) {
        return NUMBER_FORMAT.format(value == null ? 0d : value.doubleValue());
    }

}
