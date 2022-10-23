package de.perdian.apps.fimasu.quicken.model;

public class ConversionFactorRecordItem extends AbstractLongDoubleRecordItem {

    public ConversionFactorRecordItem(Number conversionFactor) {
        super('G', conversionFactor);
    }

}
