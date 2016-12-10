package de.perdian.personal.stockimporter.fx.panels;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.personal.stockimporter.model.Share;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

class ShareListPane extends BorderPane {

    private VBox sharesBox = null;
    private ObservableList<Share> shares = null;

    ShareListPane(ObservableList<Share> shares) {

        VBox sharesBox = new VBox();
        sharesBox.setSpacing(5);
        sharesBox.getChildren().add(new ShareItemPane(null));
        for (Share share : shares) {
            sharesBox.getChildren().add(new ShareItemPane(share));
        }
        ShareActionPane shareActionPane = new ShareActionPane(shares);
        sharesBox.getChildren().add(shareActionPane);

        this.setPadding(new Insets(10, 10, 10, 10));
        this.setCenter(sharesBox);
        this.setShares(shares);
        this.setSharesBox(sharesBox);

        shares.addListener((ListChangeListener<Share>)event -> {
            while (event.next()) {
                for (Share removedShare : event.getRemoved()) {
                    for (Node shareNode : sharesBox.getChildren()) {
                        if (shareNode instanceof ShareItemPane) {
                            ShareItemPane shareItemPane = ((ShareItemPane)shareNode);
                            if (removedShare.equals(shareItemPane.getShare())) {
                                Platform.runLater(() -> sharesBox.getChildren().remove(shareNode));
                            }
                        }
                    }
                }
                if (!event.getAddedSubList().isEmpty()) {
                    int startIndex = sharesBox.getChildren().indexOf(shareActionPane);
                    for (int i=0; i < event.getAddedSubList().size(); i++) {
                        Share newShare = event.getAddedSubList().get(i);
                        ShareItemPane newShareItemPane = new ShareItemPane(newShare);
                        sharesBox.getChildren().add(startIndex + i, newShareItemPane);
                    }
                }
            }
        });

    }

    class ShareActionPane extends HBox {

        ShareActionPane(ObservableList<Share> shares) {
            Button addButton = new Button("Add new share");
            addButton.setOnAction(event -> shares.add(new Share()));
            this.getChildren().add(addButton);
        }

    }

    class ShareItemPane extends HBox {

        private Share share = null;

        ShareItemPane(Share share) {
            this.setShare(share);
            this.setSpacing(5);
            if (share == null) {
                this.append(new Label(""), 100, 100);
                this.append(new Label("WKN"), 80, 80);
                this.append(new Label("ISIN"), 140, 140);
                this.append(new Label("Title"), 150, null);
                this.append(new Label("Value\n(EUR)"), 70, 70);
                this.append(new Label("Discount\n(%)"), 70, 70);
                this.append(new Label(""), 15, 15);
                this.append(new Label("Market Price\n(EUR)"), 100, 100);
                this.append(new Label("Number"), 80, 80);
                this.append(new Label("Total value\n(EUR)"), 100, 100);
            } else {
                this.appendButton(event -> getShares().remove(share), "Remove", 100);
                this.appendTextField(share.wknProperty(), new DefaultStringConverter(), 80, 80, false);
                this.appendTextField(share.isinProperty(), new DefaultStringConverter(), 140, 140, false);
                this.appendTextField(share.titleProperty(), new DefaultStringConverter(), 150, null, false);
                this.appendTextField(share.valueProperty(), new DoubleConverter("0"), 70, 70, false);
                this.appendTextField(share.discountProperty(), new DoubleConverter("0"), 70, 70, false);
                this.append(new Label(""), 15, 15);
                this.appendTextField(share.marketPriceProperty(), new DoubleConverter("0.00000"), 100, 100, true);
                this.appendTextField(share.numberOfSharesProperty(), new DoubleConverter("0.00000"), 80, 80, true);
                this.appendTextField(share.totalValueProperty(), new DoubleConverter("0.00000"), 100, 100, true);
            }
        }

        private <T> void appendTextField(Property<T> typedProperty, StringConverter<T> converter, Integer minWidth, Integer maxWidth, boolean editFocus) {

            Property<String> stringProperty = new SimpleStringProperty(converter.toString(typedProperty.getValue()));
            typedProperty.addListener((o, oldValue, newValue) -> stringProperty.setValue(converter.toString(newValue)));
            stringProperty.addListener((o, oldValue, newValue) -> {
                if (!Objects.equals(oldValue, newValue)) {
                    T targetValue = converter.fromString(newValue);
                    typedProperty.setValue(targetValue);
                    String newStringValue = converter.toString(targetValue);
                    if (!Objects.equals(newStringValue, newValue)) {
                        stringProperty.setValue(newStringValue);
                    }
                }
            });

            TextField textField = new InternalTextField(editFocus);
            textField.setMinHeight(BASELINE_OFFSET_SAME_AS_HEIGHT);
            textField.textProperty().bindBidirectional(stringProperty);
            textField.setOnKeyPressed(this::handleOnKeyPressed);
            this.append(textField, minWidth, maxWidth);

        }

        private void appendButton(EventHandler<ActionEvent> eventHandler, String text, Integer minWidth) {
            Button button = new Button(text);
            button.setOnAction(eventHandler);
            this.append(button, minWidth, minWidth);
        }

        private void append(Region node, Integer minWidth, Integer maxWidth) {
            if (minWidth != null) {
                node.setMinWidth(minWidth.intValue());
            }
            if (maxWidth != null) {
                node.setMaxWidth(maxWidth.intValue());
            } else {
                node.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(node, Priority.ALWAYS);
            }
            this.getChildren().add(node);
        }

        private void handleOnKeyPressed(KeyEvent e) {
            if (e.getCode() == KeyCode.ENTER) {
                if (e.getSource() instanceof InternalTextField) {
                    InternalTextField nextEditFocusField = this.findNextEditFocusField((InternalTextField)e.getSource());
                    if (nextEditFocusField != null) {
                        nextEditFocusField.requestFocus();
                    }
                }
            }
        }

        private InternalTextField findNextEditFocusField(Node referenceNode) {

            // First check our internal fields
            int currentIndex = referenceNode == null ? -1 : this.getChildren().indexOf(referenceNode);
            for (int i = currentIndex + 1; i < this.getChildren().size(); i++) {
                if (this.getChildren().get(i) instanceof InternalTextField) {
                    InternalTextField nextTextField = (InternalTextField)this.getChildren().get(i);
                    if (nextTextField.isEditFocus()) {
                        return nextTextField;
                    }
                }
            }

            // No more fields available, so lets check the
            ObservableList<Node> parentNodes = ShareListPane.this.getSharesBox().getChildren();
            int currentIndexInParent = parentNodes.indexOf(ShareItemPane.this);
            for (int i=currentIndexInParent + 1; true; i++) {
                int targetIndex = i % parentNodes.size();
                if (parentNodes.get(targetIndex) instanceof ShareItemPane) {
                    return ((ShareItemPane)parentNodes.get(targetIndex)).findNextEditFocusField(null);
                }
            }

        }

        Share getShare() {
            return this.share;
        }
        void setShare(Share share) {
            this.share = share;
        }

    }

    static class InternalTextField extends TextField {

        private boolean editFocus = false;

        InternalTextField(boolean editFocus) {
            this.setEditFocus(editFocus);
        }

        boolean isEditFocus() {
            return this.editFocus;
        }
        private void setEditFocus(boolean editFocus) {
            this.editFocus = editFocus;
        }

    }

    static class DoubleConverter extends StringConverter<Number> {

        private static final Logger log = LoggerFactory.getLogger(DoubleConverter.class);
        private NumberFormat numberFormat = null;

        public DoubleConverter(String format) {
            this.setNumberFormat(new DecimalFormat(format, new DecimalFormatSymbols(Locale.GERMANY)));
        }

        @Override
        public String toString(Number object) {
            return object == null || object.doubleValue() == 0d ? "" : this.getNumberFormat().format(object);
        }

        @Override
        public Double fromString(String string) {
            if (!StringUtils.isEmpty(string)) {
                try {
                    return this.getNumberFormat().parse(string).doubleValue();
                } catch (ParseException e) {
                    log.debug("Invalid string value to convert into Double: {}", string, e);
                }
            }
            return null;
        }

        private NumberFormat getNumberFormat() {
            return this.numberFormat;
        }
        private void setNumberFormat(NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
        }

    }

    VBox getSharesBox() {
        return this.sharesBox;
    }
    void setSharesBox(VBox sharesBox) {
        this.sharesBox = sharesBox;
    }

    ObservableList<Share> getShares() {
        return this.shares;
    }
    void setShares(ObservableList<Share> shares) {
        this.shares = shares;
    }

}
