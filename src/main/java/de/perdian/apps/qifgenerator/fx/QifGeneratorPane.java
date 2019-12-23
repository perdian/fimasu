package de.perdian.apps.qifgenerator.fx;

import java.io.File;

import de.perdian.apps.qifgenerator.fx.widgets.files.FilesPane;
import de.perdian.apps.qifgenerator.fx.widgets.transactiongroups.TransactionGroupsPane;
import de.perdian.apps.qifgenerator.model.TransactionGroup;
import de.perdian.commons.fx.components.ComponentBuilder;
import de.perdian.commons.fx.execution.GuiExecutorImpl;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class QifGeneratorPane extends GridPane {

    QifGeneratorPane(ObservableList<TransactionGroup> transactionGroups, Preferences preferences) {

        ObservableList<File> files = FXCollections.observableArrayList();
        ObjectProperty<File> selectedFile = new SimpleObjectProperty<>();
        FilesPane filesPane = new FilesPane(files, selectedFile, preferences);
        GridPane.setHgrow(filesPane, Priority.ALWAYS);
        GridPane.setVgrow(filesPane, Priority.ALWAYS);

        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.addListener(component -> component.setOnKeyPressed(filesPane.createNavigationKeyPressedEventHandler()));

        GuiExecutorImpl executorImpl = new GuiExecutorImpl();

        TransactionGroupsPane transactionGroupsPane = new TransactionGroupsPane(transactionGroups, files, executorImpl, componentBuilder, preferences);
        transactionGroupsPane.setMinWidth(775);
        transactionGroupsPane.setMaxWidth(775);
        GridPane.setVgrow(transactionGroupsPane, Priority.ALWAYS);

        this.add(transactionGroupsPane, 0, 0, 1, 1);
        this.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 1);
        this.add(filesPane, 2, 0, 1, 1);

    }

}
