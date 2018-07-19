package de.perdian.apps.qifgenerator.fxnew;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;

public class QifGeneratorHelper {

    public static TitledPane wrapInTitledPane(String title, Node node) {

        BorderPane titledPaneContent = new BorderPane(node);
        titledPaneContent.setPadding(new Insets(8, 8, 8, 8));

        TitledPane titledPane = new TitledPane(title, titledPaneContent);
        titledPane.setCollapsible(false);
        titledPane.setMaxHeight(Double.MAX_VALUE);
        return titledPane;

    }

}
