package de.perdian.personal.stockimporter.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class StockModel {

    static final long serialVersionUID = 1L;

    private final ObservableList<BuyingTime> buyingTimes = FXCollections.observableArrayList();
    private final List<ChangeListener<StockModel>> changeListeners = new ArrayList<>();

    StockModel() {
        ChangeListener<BuyingTime> buyingTimeChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.buyingTimesProperty().addListener((ListChangeListener<BuyingTime>)event -> {
            while (event.next()) {
                for (BuyingTime removedBuyingTime : event.getRemoved()) {
                    removedBuyingTime.removeChangeListener(buyingTimeChangeListener);
                }
                for (BuyingTime addedBuyingTime : event.getAddedSubList()) {
                    addedBuyingTime.addChangeListener(buyingTimeChangeListener);
                }
            }
            this.fireChange();
        });
    }

    public ObservableList<BuyingTime> buyingTimesProperty() {
        return this.buyingTimes;
    }

    void fireChange() {
        for (ChangeListener<StockModel> changeListener : this.changeListeners) {
            changeListener.changed(null, this, this);
        }
    }
    void addChangeListener(ChangeListener<StockModel> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<StockModel> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
