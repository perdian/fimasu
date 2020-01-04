package de.perdian.apps.fimasu.fx.widgets.transactions;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

class TransactionLineItem {

    private Label titleLabel = null;
    private List<Region> fieldComponents = null;

    TransactionLineItem(Label titleLabel, List<Region> fieldComponents) {
        this.setTitleLabel(titleLabel);
        this.setFieldComponents(fieldComponents);
    }

    Label getTitleLabel() {
        return this.titleLabel;
    }
    void setTitleLabel(Label titleLabel) {
        this.titleLabel = titleLabel;
    }

    List<Region> getFieldComponents() {
        return this.fieldComponents;
    }
    void setFieldComponents(List<Region> fieldComponents) {
        this.fieldComponents = fieldComponents;
    }

}
