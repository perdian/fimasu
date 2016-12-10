package de.perdian.personal.stockimporter.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class Share {

    static final long serialVersionUID = 1L;

    private final StringProperty wkn = new SimpleStringProperty();
    private final StringProperty isin = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final DoubleProperty discount = new SimpleDoubleProperty();
    private final DoubleProperty marketPrice = new SimpleDoubleProperty();
    private final DoubleProperty numberOfShares = new SimpleDoubleProperty();
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final List<ChangeListener<Share>> changeListeners = new ArrayList<>();

    public Share() {
        ChangeListener<Object> shareChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.discountProperty().addListener(shareChangeListener);
        this.isinProperty().addListener(shareChangeListener);
        this.titleProperty().addListener(shareChangeListener);
        this.valueProperty().addListener(shareChangeListener);
        this.wknProperty().addListener(shareChangeListener);
        this.numberOfSharesProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(newValue, this.marketPriceProperty().getValue()));
        this.marketPriceProperty().addListener((o, oldValue, newValue) -> this.recomputeTotalValue(this.numberOfSharesProperty().getValue(), newValue));
    }

    private void recomputeTotalValue(Number numberOfShares, Number marketPrice) {
        if (numberOfShares == null || numberOfShares.doubleValue() == 0d || marketPrice == null || marketPrice.doubleValue() == 0d) {
            this.totalValueProperty().setValue(null);
        } else {
            this.totalValueProperty().setValue(numberOfShares.doubleValue() * marketPrice.doubleValue());
        }
    }

    public StringProperty wknProperty() {
        return this.wkn;
    }

    public StringProperty isinProperty() {
        return this.isin;
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public DoubleProperty valueProperty() {
        return this.value;
    }

    public DoubleProperty discountProperty() {
        return this.discount;
    }

    public DoubleProperty marketPriceProperty() {
        return this.marketPrice;
    }

    public DoubleProperty numberOfSharesProperty() {
        return this.numberOfShares;
    }

    public DoubleProperty totalValueProperty() {
        return this.totalValue;
    }

    void fireChange() {
        for (ChangeListener<Share> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    void addChangeListener(ChangeListener<Share> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<Share> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
