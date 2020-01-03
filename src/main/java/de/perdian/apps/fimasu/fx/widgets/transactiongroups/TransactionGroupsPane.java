package de.perdian.apps.fimasu.fx.widgets.transactiongroups;

import java.io.File;

import de.perdian.apps.fimasu.model.TransactionGroup;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.execution.GuiExecutor;
import de.perdian.commons.fx.execution.GuiExecutorListener;
import de.perdian.commons.fx.execution.GuiJob;
import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.properties.PropertyFactory;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class TransactionGroupsPane extends BorderPane {

    public TransactionGroupsPane(ObservableList<TransactionGroup> transactionGroups, ObservableList<File> files, GuiExecutor guiExecutor, ComponentBuilder componentBuilder, Preferences preferences) {

        IntegerProperty selectedTabIndexProperty = PropertyFactory.createIntegerProperty(preferences.getStringProperty("transactions.selectedTabIndex", "0"));
        TabPane tabPane = new TabPane();
        for (TransactionGroup transactionGroup : transactionGroups) {
            tabPane.getTabs().add(new TransactionGroupTab(transactionGroups, transactionGroup, files, guiExecutor, componentBuilder.createChild(), preferences));
        }
        tabPane.setContextMenu(new TransactionGroupsContextMenu(transactionGroups, guiExecutor, preferences));
        tabPane.getSelectionModel().select(selectedTabIndexProperty.getValue());
        tabPane.setOnKeyPressed(new TransactionGroupKeyPressedEventHandler(() -> ((TransactionGroupTab)tabPane.getSelectionModel().getSelectedItem()).getTransactionGroup(), files, guiExecutor));
        transactionGroups.addListener((ListChangeListener.Change<? extends TransactionGroup> change) -> {
            while (change.next()) {
                for (TransactionGroup newGroup : change.getAddedSubList()) {
                    Platform.runLater(() -> {
                        Tab newTab = new TransactionGroupTab(transactionGroups, newGroup, files, guiExecutor, componentBuilder.createChild(), preferences);
                        tabPane.getTabs().add(newTab);
                        tabPane.getSelectionModel().select(newTab);
                    });
                }
            }
        });
        tabPane.getSelectionModel().selectedIndexProperty().addListener((o, oldValue, newValue) -> selectedTabIndexProperty.setValue(newValue));

        this.setCenter(tabPane);

        guiExecutor.addExecutorListener(new GuiExecutorListener() {
            @Override public void onExecutionStarting(GuiJob job) {
                TransactionGroupsPane.this.setDisable(true);
            }
            @Override public void onExecutionCompleted(GuiJob job) {
                TransactionGroupsPane.this.setDisable(false);
            }
        });

    }

}
