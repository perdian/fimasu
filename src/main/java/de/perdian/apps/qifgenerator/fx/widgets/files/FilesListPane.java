package de.perdian.apps.qifgenerator.fx.widgets.files;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.qifgenerator.fx.support.components.ComponentBuilder;
import de.perdian.apps.qifgenerator.fx.support.converters.FileStringConverter;
import de.perdian.commons.fx.preferences.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

class FilesListPane extends GridPane {

    private static final Logger log = LoggerFactory.getLogger(FilesListPane.class);

    FilesListPane(ObservableList<File> files, ObjectProperty<File> selectedFile, Preferences preferences) {

        ComponentBuilder componentBuilder = new ComponentBuilder();

        StringProperty directoryValueProperty = preferences.getStringProperty("previews.directory", System.getProperty("user.home"));
        String directoryValue = directoryValueProperty.getValue();
        ObjectProperty<File> directoryProperty = new SimpleObjectProperty<>(StringUtils.isEmpty(directoryValue) ? null : new File(directoryValue));

        directoryValueProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                directoryProperty.setValue(StringUtils.isEmpty(newValue) ? null : new File(newValue));
            }
        });
        directoryProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && newValue != null) {
                directoryValueProperty.setValue(newValue.getAbsolutePath());
            }
        });

        TextField directoryField = new TextField();
        directoryField.setPrefWidth(400);
        directoryField.setText(directoryProperty.getValue() == null ? "" : directoryProperty.getValue().getAbsolutePath());
        directoryField.textProperty().bindBidirectional(directoryProperty, new FileStringConverter());
        GridPane.setHgrow(directoryField, Priority.ALWAYS);

        Label filesLabel = componentBuilder.createLabel("Files");
        GridPane.setMargin(filesLabel, new Insets(8, 0, 0, 0));

        this.loadFiles(directoryProperty.getValue(), files);
        directoryProperty.addListener((o, oldValue, newValue) -> this.loadFiles(newValue, files));

        TableColumn<File, Boolean> typeColumn = new TableColumn<>("");
        typeColumn.setCellValueFactory(in -> new SimpleBooleanProperty(in.getValue().isDirectory()));
        typeColumn.setCellFactory(item -> {
            TableCell<File, Boolean> tableCell = new TableCell<>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        this.setGraphic(null);
                    } else if (Boolean.TRUE.equals(item)) {
                        this.setGraphic(new Label("", new FontAwesomeIconView(FontAwesomeIcon.FOLDER)));
                    } else {
                        this.setGraphic(new Label("", new FontAwesomeIconView(FontAwesomeIcon.FILE)));
                    }
                }
            };
            return tableCell;
        });
        typeColumn.setMinWidth(24);
        typeColumn.setMaxWidth(24);
        TableColumn<File, String> fileTitleColumn = new TableColumn<>("Name");
        fileTitleColumn.setMaxWidth(Double.MAX_VALUE);
        fileTitleColumn.setCellValueFactory(in -> new SimpleStringProperty(in.getValue().getName()));
        TableView<File> filesTableView = new TableView<>(files);
        filesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        filesTableView.setPrefHeight(150);
        filesTableView.getColumns().addAll(List.of(typeColumn, fileTitleColumn));
        GridPane.setHgrow(filesTableView, Priority.ALWAYS);
        GridPane.setVgrow(filesTableView, Priority.ALWAYS);
        selectedFile.addListener((o, oldValue, newValue) -> this.scrollToFile(filesTableView, newValue));
        filesTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filesTableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                selectedFile.setValue(null);
            } else if (newValue.isFile()) {
                selectedFile.setValue(newValue);
            }
        });
        filesTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() > 1) {
                File selectedDirectory = filesTableView.getSelectionModel().getSelectedItem();
                if (selectedDirectory.isDirectory()) {
                    directoryProperty.setValue(selectedDirectory);
                }
            }
        });
        selectedFile.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && !Objects.equals(newValue, filesTableView.getSelectionModel().getSelectedItem())) {
                filesTableView.getSelectionModel().select(newValue);
            }
        });

        Button selectDirectoryButton = new Button("Select", new FontAwesomeIconView(FontAwesomeIcon.HAND_POINTER_ALT));
        selectDirectoryButton.setTooltip(new Tooltip("Select directory"));
        selectDirectoryButton.setOnAction(event -> this.selectDirectory(directoryProperty));
        GridPane.setVgrow(selectDirectoryButton, Priority.SOMETIMES);
        Button goUpButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.ARROW_UP));
        goUpButton.setTooltip(new Tooltip("Go up"));
        goUpButton.setOnAction(event -> {
            if (directoryProperty.getValue() != null) {
                File parentFile = directoryProperty.getValue().getParentFile();
                if (parentFile != null) {
                    directoryProperty.setValue(parentFile);
                }
            }
        });
        goUpButton.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(goUpButton, Priority.SOMETIMES);
        Button reloadButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        reloadButton.setTooltip(new Tooltip("Reload"));
        reloadButton.setOnAction(event -> this.loadFiles(directoryProperty.getValue(), files));
        reloadButton.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(reloadButton, Priority.SOMETIMES);

        this.setVgap(2);
        this.setHgap(2);
        this.add(componentBuilder.createLabel("Directory"), 0, 0, 1, 1);
        this.add(directoryField, 0, 1, 1, 1);
        this.add(selectDirectoryButton, 1, 1, 1, 1);
        this.add(goUpButton, 2, 1, 1, 1);
        this.add(reloadButton, 3, 1, 1, 1);
        this.add(filesLabel, 0, 2, 4, 1);
        this.add(filesTableView, 0, 3, 4, 1);

    }

    private void loadFiles(File directory, ObservableList<File> targetList) {
        if (directory != null) {

            log.debug("Loading files from directory: {}", directory.getAbsolutePath());
            File[] directoryChildren = directory.listFiles(new FileFilterImpl());
            if (directoryChildren == null || directoryChildren.length == 0) {
                targetList.clear();
            } else {

                Comparator<File> fileComparator = (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                };

                targetList.setAll(Arrays.stream(directoryChildren).filter(file -> !file.isHidden()).sorted(fileComparator).collect(Collectors.toList()));
            }

        } else {
            targetList.clear();
        }
    }

    private void scrollToFile(TableView<File> files, File file) {
        if (file != null) {
            TableViewSkin<?> tableViewSkin = (TableViewSkin<?>)files.getSkin();
            VirtualFlow<?> virtualFlow = (VirtualFlow<?>)tableViewSkin.getChildren().get(1);
            int firstCellIndex = virtualFlow.getFirstVisibleCell().getIndex();
            int lastCellIndex = virtualFlow.getLastVisibleCell().getIndex();
            for (int i=0; i < files.getItems().size(); i++) {
                if (files.getItems().get(i).equals(file)) {
                    if (i <= firstCellIndex) {
                        files.scrollTo(Math.max(0, i - 1));
                    } else if (i > 0 && i >= lastCellIndex) {
                        files.scrollTo(i);
                    }
                }
            }
        }
    }

    private void selectDirectory(ObjectProperty<File> directoryProperty) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(directoryProperty.getValue() == null ? new File(System.getProperty("user.home")) : directoryProperty.getValue());
        directoryChooser.setTitle("Select directory");
        File directory = directoryChooser.showDialog(this.getScene().getWindow());
        if (directory != null && !Objects.equals(directory, directoryProperty.getValue())) {
            directoryProperty.setValue(directory);
        }
    }

    private static class FileFilterImpl implements FileFilter {

        @Override
        public boolean accept(File file) {
            return !file.isHidden() && !file.getName().startsWith(".") && !file.getName().startsWith("$");
        }

    }

}
