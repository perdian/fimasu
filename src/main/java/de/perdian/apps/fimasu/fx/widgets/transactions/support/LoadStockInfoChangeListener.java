package de.perdian.apps.fimasu.fx.widgets.transactions.support;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.model.Transaction;
import de.perdian.apps.fimasu.support.stockinfo.StockInfo;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;

public class LoadStockInfoChangeListener implements ChangeListener<String> {

    private Transaction transaction = null;
    private Function<String, StockInfo> stockInfoFunction = null;
    private int requiredLength = 0;
    private List<Region> disableFields = null;
    private BooleanProperty busy = null;

    public LoadStockInfoChangeListener(Transaction transaction, Function<String, StockInfo> stockInfoFunction, int requiredLength, List<Region> disableFields, BooleanProperty busy) {
        this.setRequiredLength(requiredLength);
        this.setTransaction(transaction);
        this.setStockInfoFunction(stockInfoFunction);
        this.setDisableFields(disableFields);
        this.setBusy(busy);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!Objects.equals(oldValue, newValue) && newValue != null && this.getRequiredLength() == newValue.length() && (StringUtils.isEmpty(this.getTransaction().getTitle().getValue()) || StringUtils.isEmpty(this.getTransaction().getWkn().getValue()) || StringUtils.isEmpty(this.getTransaction().getIsin().getValue()))) {
            synchronized(this) {
                if (!this.getBusy().get()) {
                    this.getBusy().set(true);
                    Platform.runLater(() -> this.getDisableFields().forEach(field -> field.setDisable(true)));
                    new Thread(() -> {
                        try {
                            StockInfo stockInfo = this.getStockInfoFunction().apply(newValue);
                            if (stockInfo != null) {
                                if (StringUtils.isNotEmpty(stockInfo.getWkn()) && StringUtils.isEmpty(this.getTransaction().getWkn().getValue())) {
                                    this.getTransaction().getWkn().setValue(stockInfo.getWkn());
                                }
                                if (StringUtils.isNotEmpty(stockInfo.getIsin()) && StringUtils.isEmpty(this.getTransaction().getIsin().getValue())) {
                                    this.getTransaction().getIsin().setValue(stockInfo.getIsin());
                                }
                                if (StringUtils.isNotEmpty(stockInfo.getTitle()) && StringUtils.isEmpty(this.getTransaction().getTitle().getValue())) {
                                    this.getTransaction().getTitle().setValue(stockInfo.getTitle());
                                }
                            }
                        } finally {
                            Platform.runLater(() -> this.getDisableFields().forEach(field -> field.setDisable(false)));
                            this.getBusy().set(false);
                        }
                    }).start();
                }
            }
        }
    }

    private Transaction getTransaction() {
        return this.transaction;
    }
    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    private Function<String, StockInfo> getStockInfoFunction() {
        return this.stockInfoFunction;
    }
    private void setStockInfoFunction(Function<String, StockInfo> stockInfoFunction) {
        this.stockInfoFunction = stockInfoFunction;
    }

    private int getRequiredLength() {
        return this.requiredLength;
    }
    private void setRequiredLength(int requiredLength) {
        this.requiredLength = requiredLength;
    }

    private List<Region> getDisableFields() {
        return this.disableFields;
    }
    private void setDisableFields(List<Region> disableFields) {
        this.disableFields = disableFields;
    }

    private BooleanProperty getBusy() {
        return this.busy;
    }
    private void setBusy(BooleanProperty busy) {
        this.busy = busy;
    }

}
