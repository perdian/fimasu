package de.perdian.apps.qifgenerator.fx.widgets.previews;

import de.perdian.apps.qifgenerator.preferences.Preferences;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PreviewsPane extends VBox {

    public PreviewsPane(Preferences preferences) {

        PreviewFilesPane filesPane = new PreviewFilesPane(preferences);
        filesPane.setPadding(new Insets(8, 8, 8, 8));
        TitledPane filesTitledPane = new TitledPane("Files", filesPane);
        filesTitledPane.setCollapsible(false);

        PreviewContentPane contentPane = new PreviewContentPane();
        TitledPane contentTitledPane = new TitledPane("Content", contentPane);
        contentTitledPane.setCollapsible(false);
        contentTitledPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(contentTitledPane, Priority.ALWAYS);

        this.setSpacing(8);
        this.setPadding(new Insets(8, 8, 8, 4));
        this.getChildren().addAll(filesTitledPane, contentTitledPane);

    }

}
