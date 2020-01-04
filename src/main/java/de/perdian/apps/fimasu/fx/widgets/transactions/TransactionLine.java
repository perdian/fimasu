package de.perdian.apps.fimasu.fx.widgets.transactions;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class TransactionLine {

    private List<TransactionLineItem> items = null;

    public TransactionLine() {
        this.setItems(new ArrayList<>());
    }

    public Region toComponent() {
        GridPane linePane = new GridPane();
        linePane.setVgap(2);
        linePane.setHgap(4);
        int columnOffset = 0;
        for (TransactionLineItem item : this.getItems()) {
            linePane.add(item.getTitleLabel(), columnOffset, 0, Math.max(1, item.getFieldComponents().size()), 1);
            for (int fieldIndex = 0; fieldIndex < item.getFieldComponents().size(); fieldIndex++) {
                linePane.add(item.getFieldComponents().get(fieldIndex), columnOffset + fieldIndex, 1, 1, 1);
            }
            columnOffset += Math.max(1, item.getFieldComponents().size());
        }
        return linePane;
    }

    public TransactionLine item(Label titleLabel, Region... fields) {
        this.getItems().add(new TransactionLineItem(titleLabel, List.of(fields)));
        return this;
    }
    public List<TransactionLineItem> getItems() {
        return this.items;
    }
    private void setItems(List<TransactionLineItem> items) {
        this.items = items;
    }

}
