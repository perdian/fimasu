package de.perdian.apps.qifgenerator.fxnew.modules.documents.files;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.qifgenerator.fxnew.QifGeneratorPreferences;
import de.perdian.apps.qifgenerator.fxnew.support.properties.FileToStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

public class FilesPane extends GridPane {

    private static final Logger log = LoggerFactory.getLogger(FilesPane.class);

    private ObjectProperty<File> selectedFileProperty = null;
    private TableView<File> files = null;

    public FilesPane(QifGeneratorPreferences preferences) {

        ObjectProperty<File> selectedFileProperty = new SimpleObjectProperty<>();
        StringProperty directoryValueProperty = preferences.getValueProperty("files.directory");
        String directoryValue = directoryValueProperty.getValue();
        ObjectProperty<File> directoryProperty = new SimpleObjectProperty<>(StringUtils.isEmpty(directoryValue) ? null : new File(directoryValue));
        this.setSelectedFileProperty(selectedFileProperty);

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

        Label directoryLabel = new Label("Directory");
        TextField directoryField = new TextField();
        directoryField.setPrefWidth(400);
        directoryField.setText(directoryProperty.getValue() == null ? "" : directoryProperty.getValue().getAbsolutePath());
        directoryField.textProperty().bindBidirectional(directoryProperty, new FileToStringConverter());
        GridPane.setHgrow(directoryField, Priority.ALWAYS);

        ObservableList<File> files = FXCollections.observableArrayList();
        this.loadFiles(directoryProperty.getValue(), files);
        directoryProperty.addListener((o, oldValue, newValue) -> this.loadFiles(newValue, files));

        TableColumn<File, Boolean> typeColumn = new TableColumn<>("");
        typeColumn.setCellValueFactory(in -> new SimpleBooleanProperty(in.getValue().isDirectory()));
        typeColumn.setCellFactory(item -> {
            TableCell<File, Boolean> tableCell = new TableCell<File, Boolean>() {
                @Override protected void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        this.setGraphic(null);
                    } else if (Boolean.TRUE.equals(item)) {
                        this.setGraphic(new Label("", new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/folder-open.png")))));
                    } else {
                        this.setGraphic(new Label("", new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/document.png")))));
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
        Label filesLabel = new Label("Files");
        filesLabel.setPadding(new Insets(8, 0, 0, 0));
        TableView<File> filesTableView = new TableView<>(files);
        filesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        filesTableView.setPrefHeight(150);
        filesTableView.getColumns().addAll(List.of(typeColumn, fileTitleColumn));
        GridPane.setHgrow(filesTableView, Priority.ALWAYS);
        GridPane.setVgrow(filesTableView, Priority.SOMETIMES);
        selectedFileProperty.addListener((o, oldValue, newValue) -> this.scrollToFile(filesTableView, newValue));
        filesTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filesTableView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                selectedFileProperty.setValue(null);
            } else if (newValue.isFile()) {
                selectedFileProperty.setValue(newValue);
            }
        });
        filesTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() > 1) {
                File selectedFile = filesTableView.getSelectionModel().getSelectedItem();
                if (selectedFile.isDirectory()) {
                    directoryProperty.setValue(selectedFile);
                }
            }
        });
        this.setFiles(filesTableView);

        Button selectDirectoryButton = new Button(null, new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/folder-open.png"))));
        selectDirectoryButton.setTooltip(new Tooltip("Select directory"));
        selectDirectoryButton.setOnAction(event -> this.selectDirectory(directoryProperty));
        Button goUpButton = new Button(null, new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/go-up.png"))));
        goUpButton.setTooltip(new Tooltip("Go up"));
        goUpButton.setOnAction(event -> directoryProperty.setValue(directoryProperty.getValue() == null ? null : directoryProperty.getValue().getParentFile()));
        Button reloadButton = new Button(null, new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/16/refresh.png"))));
        reloadButton.setTooltip(new Tooltip("Reload"));
        reloadButton.setOnAction(event -> this.loadFiles(directoryProperty.getValue(), files));

        this.setVgap(2);
        this.setHgap(2);
        this.add(directoryLabel, 0, 0, 1, 1);
        this.add(directoryField, 0, 1, 1, 1);
        this.add(goUpButton, 1, 1, 1, 1);
        this.add(selectDirectoryButton, 2, 1, 1, 1);
        this.add(reloadButton, 3, 1, 1, 1);
        this.add(filesLabel, 0, 2, 4, 1);
        this.add(filesTableView, 0, 3, 4, 1);

    }

    public void moveSelection(int direction) {
        int maxIndex = this.getFiles().getSelectionModel().getSelectedItems().size();
        int currentIndex = this.getFiles().getSelectionModel().getSelectedIndex();
        int newIndex = currentIndex + direction;
        if (newIndex >= 0 && newIndex < maxIndex) {
            this.getFiles().getSelectionModel().select(newIndex);
        }
    }

    private void loadFiles(File directory, ObservableList<File> targetList) {
        if (directory != null) {

            log.debug("Loading files from directory: {}", directory.getAbsolutePath());
            File[] directoryChildren = directory.listFiles();
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

    public ObjectProperty<File> selectedFile() {
        return this.getSelectedFileProperty();
    }
    private ObjectProperty<File> getSelectedFileProperty() {
        return this.selectedFileProperty;
    }
    private void setSelectedFileProperty(ObjectProperty<File> selectedFileProperty) {
        this.selectedFileProperty = selectedFileProperty;
    }

    private TableView<File> getFiles() {
        return this.files;
    }
    private void setFiles(TableView<File> files) {
        this.files = files;
    }

}
