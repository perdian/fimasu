package de.perdian.apps.fimasu4.fx.modules.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;

import de.perdian.apps.fimasu4.fx.FimasuPreferences;
import de.perdian.apps.fimasu4.fx.support.ComponentFactory;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

public class FilesListPane extends GridPane {

    private ObservableList<File> files = null;

    public FilesListPane(FimasuPreferences preferences) {

        ObservableList<File> files = FXCollections.observableArrayList();
        this.setFiles(files);

        ComponentFactory componentFactory = new ComponentFactory();

        StringProperty directoryProperty = preferences.getStringProperty(this.getClass().getSimpleName() + ".directory");
        TextField directoryfield = componentFactory.createTextField(directoryProperty);
        directoryfield.setDisable(true);
        HBox.setHgrow(directoryfield, Priority.ALWAYS);
        Button selectionButton = componentFactory.createButton(MaterialDesignF.FOLDER, event -> this.selectDirectory(directoryProperty, files));
        Button refreshButton = componentFactory.createButton(MaterialDesignR.RELOAD, event -> this.refreshFilesFromDirectory(directoryProperty, files));
        refreshButton.setTooltip(new Tooltip("Reload files in directory"));
        refreshButton.disableProperty().bind(directoryProperty.isEmpty());
        HBox selectionBar = new HBox(5, directoryfield, selectionButton, refreshButton);
        GridPane.setHgrow(selectionBar, Priority.ALWAYS);

        if (StringUtils.isNotEmpty(directoryProperty.getValue())) {
            this.refreshFilesFromDirectory(directoryProperty, files);
        }

        ListView<File> filesView = new ListView<>(files);
        filesView.setCellFactory(TextFieldListCell.forListView(new FileStringConverter()));
        GridPane.setHgrow(filesView, Priority.ALWAYS);
        GridPane.setVgrow(filesView, Priority.ALWAYS);

        this.add(selectionBar, 0, 0, 1, 1);
        this.add(filesView, 0, 1, 1, 1);
        this.setVgap(5);
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setMinHeight(150);

    }

    private void selectDirectory(StringProperty directoryProperty, ObservableList<File> filesInDirectory) {
        File currentDirectory = StringUtils.isEmpty(directoryProperty.getValue()) ? null : new File(directoryProperty.getValue());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select source directory");
        if (currentDirectory != null && currentDirectory.getParentFile().exists()) {
            directoryChooser.setInitialDirectory(currentDirectory.getParentFile());;
        }
        File selectedDirectory = directoryChooser.showDialog(this.getScene().getWindow());
        if (selectedDirectory != null) {
            directoryProperty.setValue(selectedDirectory.getAbsolutePath());
        }
    }

    private void refreshFilesFromDirectory(StringProperty directoryProperty, ObservableList<File> filesInDirectory) {
        File directory = StringUtils.isEmpty(directoryProperty.getValue()) ? null : new File(directoryProperty.getValue());
        if (directory == null || !directory.exists()) {
            filesInDirectory.clear();
        } else {
            File[] directoryChildren = directory.listFiles(file -> file.isFile() && !file.isHidden());
            List<File> directoryChildrenList = new ArrayList<>(directoryChildren == null ? Collections.emptyList() : Arrays.asList(directoryChildren));
            directoryChildrenList.sort((f1, f2) -> f2.getName().compareToIgnoreCase(f2.getName()));
            filesInDirectory.setAll(directoryChildrenList);
        }
    }

    private static class FileStringConverter extends StringConverter<File> {

        @Override
        public String toString(File object) {
            return object.getName();
        }

        @Override
        public File fromString(String string) {
            throw new UnsupportedOperationException();
        }

    }

    public ObservableList<File> getFiles() {
        return this.files;
    }
    private void setFiles(ObservableList<File> files) {
        this.files = files;
    }

}
