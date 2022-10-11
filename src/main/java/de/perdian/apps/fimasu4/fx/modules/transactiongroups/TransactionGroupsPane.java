package de.perdian.apps.fimasu4.fx.modules.transactiongroups;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Wrapper for all available (and possible) transaction groups
 *
 * @author Christian Seifert
 */

public class TransactionGroupsPane extends BorderPane {

    public TransactionGroupsPane() {

        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(Double.MAX_VALUE);
        this.setCenter(tabPane);

        Tab tab1 = new Tab("XXX");
        tab1.closableProperty().bind(Bindings.size(tabPane.getTabs()).greaterThan(1));

        Tab tab2 = new Tab("YYY");
        tab2.closableProperty().bind(Bindings.size(tabPane.getTabs()).greaterThan(1));

        tabPane.getTabs().addAll(tab1, tab2);
        this.setCenter(tabPane);

    }

}
