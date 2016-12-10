package de.perdian.personal.stockimporter.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BuyingTime {

    static final long serialVersionUID = 1L;

    private final StringProperty title = new SimpleStringProperty();
    private final ObservableList<Share> shares = FXCollections.observableArrayList();
    private final List<ChangeListener<BuyingTime>> changeListeners = new ArrayList<>();

    public BuyingTime() {
        ChangeListener<Share> shareChangeListener = (x, oldValue, newValue) -> this.fireChange();
        this.sharesProperty().addListener((ListChangeListener<Share>)event -> {
            while (event.next()) {
                for (Share removedShare : event.getRemoved()) {
                    removedShare.removeChangeListener(shareChangeListener);
                }
                for (Share addedShare : event.getAddedSubList()) {
                    addedShare.addChangeListener(shareChangeListener);
                }
            }
            this.fireChange();
        });
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public ObservableList<Share> sharesProperty() {
        return this.shares;
    }

    void fireChange() {
        for (ChangeListener<BuyingTime> changeListener : this.changeListeners) {
            changeListener.changed(null, null, this);
        }
    }
    void addChangeListener(ChangeListener<BuyingTime> changeListener) {
        if (!this.changeListeners.contains(changeListener)) {
            this.changeListeners.add(changeListener);
        }
    }
    void removeChangeListener(ChangeListener<BuyingTime> changeListener) {
        this.changeListeners.remove(changeListener);
    }

}
