package de.perdian.apps.fimasu4.model.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public class Transaction implements Serializable {

    static final long serialVersionUID = 1L;

    private ObjectProperty<TransactionType> type = null;
    private BooleanProperty persistent = null;
    private StockIdentfier stockIdentifier = null;
    private List<ChangeListener<Object>> changeListeners = null;

    public Transaction() {

        List<ChangeListener<Object>> changeListeners = new ArrayList<>();
        this.setChangeListeners(changeListeners);
        ChangeListener<Object> changeListener = (o, oldValue, newValue) -> {
            for (ChangeListener<Object> delegeeChangeListener : changeListeners) {
                delegeeChangeListener.changed(o, oldValue, newValue);
            }
        };

        ObjectProperty<TransactionType> type = new SimpleObjectProperty<>();
        type.addListener(changeListener);
        this.setType(type);

        StockIdentfier stockIdentifier = new StockIdentfier();
        stockIdentifier.getWkn().addListener(changeListener);
        stockIdentifier.getIsin().addListener(changeListener);
        stockIdentifier.getTitle().addListener(changeListener);
        this.setStockIdentifier(stockIdentifier);

        BooleanProperty persistent = new SimpleBooleanProperty();
        persistent.addListener(changeListener);
        this.setPersistent(persistent);

    }

    public ObjectProperty<TransactionType> getType() {
        return this.type;
    }
    private void setType(ObjectProperty<TransactionType> type) {
        this.type = type;
    }

    public BooleanProperty getPersistent() {
        return this.persistent;
    }
    private void setPersistent(BooleanProperty persistent) {
        this.persistent = persistent;
    }

    public StockIdentfier getStockIdentifier() {
        return this.stockIdentifier;
    }
    private void setStockIdentifier(StockIdentfier stockIdentifier) {
        this.stockIdentifier = stockIdentifier;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    private List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    private void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

}
