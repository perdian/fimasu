package de.perdian.apps.fimasu4.quicken.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import de.perdian.apps.fimasu4.quicken.RecordItem;

public class CommissionRecordItem implements RecordItem {

    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    private Number maklercourtage = null;
    private Number kapitalertragsteuer = null;
    private Number spesen = null;
    private Number bankprovision = null;
    private Number spesenausland = null;
    private Number auslaendischequellensteuer = null;
    private Number zinsabschlagsteuer = null;
    private Number sonstigekosten = null;
    private Number solidaritaetsuzschlag = null;
    private Number boersenplatzentgelt = null;
    private Number abgeltungssteuer = null;
    private Number kirchensteuer = null;

    @Override
    public String toQifString() {
        return "O" + this.toQifStringForValues(this.getMaklercourtage(), this.getKapitalertragsteuer(), this.getSpesen(), this.getBankprovision(), this.getSpesenausland(), this.getAuslaendischequellensteuer(), this.getZinsabschlagsteuer(), this.getSonstigekosten(), this.getSolidaritaetsuzschlag(), this.getBoersenplatzentgelt(), this.getAbgeltungssteuer(), this.getKirchensteuer());
    }

    private String toQifStringForValues(Number... values) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i < values.length; i++) {
            result.append(i > 0 ? "|" : "").append(NUMBER_FORMAT.format(values[i] == null ? 0d : values[i].doubleValue()));
        }
        return result.toString();
    }

    public Number getMaklercourtage() {
        return this.maklercourtage;
    }
    public void setMaklercourtage(Number maklercourtage) {
        this.maklercourtage = maklercourtage;
    }

    public Number getKapitalertragsteuer() {
        return this.kapitalertragsteuer;
    }
    public void setKapitalertragsteuer(Number kapitalertragsteuer) {
        this.kapitalertragsteuer = kapitalertragsteuer;
    }

    public Number getSpesen() {
        return this.spesen;
    }
    public void setSpesen(Number spesen) {
        this.spesen = spesen;
    }

    public Number getBankprovision() {
        return this.bankprovision;
    }
    public void setBankprovision(Number bankprovision) {
        this.bankprovision = bankprovision;
    }

    public Number getSpesenausland() {
        return this.spesenausland;
    }
    public void setSpesenausland(Number spesenausland) {
        this.spesenausland = spesenausland;
    }

    public Number getAuslaendischequellensteuer() {
        return this.auslaendischequellensteuer;
    }
    public void setAuslaendischequellensteuer(Number auslaendischequellensteuer) {
        this.auslaendischequellensteuer = auslaendischequellensteuer;
    }

    public Number getZinsabschlagsteuer() {
        return this.zinsabschlagsteuer;
    }
    public void setZinsabschlagsteuer(Number zinsabschlagsteuer) {
        this.zinsabschlagsteuer = zinsabschlagsteuer;
    }

    public Number getSonstigekosten() {
        return this.sonstigekosten;
    }
    public void setSonstigekosten(Number sonstigekosten) {
        this.sonstigekosten = sonstigekosten;
    }

    public Number getSolidaritaetsuzschlag() {
        return this.solidaritaetsuzschlag;
    }
    public void setSolidaritaetsuzschlag(Number solidaritaetsuzschlag) {
        this.solidaritaetsuzschlag = solidaritaetsuzschlag;
    }

    public Number getBoersenplatzentgelt() {
        return this.boersenplatzentgelt;
    }
    public void setBoersenplatzentgelt(Number boersenplatzentgelt) {
        this.boersenplatzentgelt = boersenplatzentgelt;
    }

    public Number getAbgeltungssteuer() {
        return this.abgeltungssteuer;
    }
    public void setAbgeltungssteuer(Number abgeltungssteuer) {
        this.abgeltungssteuer = abgeltungssteuer;
    }

    public Number getKirchensteuer() {
        return this.kirchensteuer;
    }
    public void setKirchensteuer(Number kirchensteuer) {
        this.kirchensteuer = kirchensteuer;
    }

}
