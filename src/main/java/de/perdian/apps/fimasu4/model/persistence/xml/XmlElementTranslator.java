package de.perdian.apps.fimasu4.model.persistence.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu4.fx.support.converters.BooleanStringConverter;
import de.perdian.apps.fimasu4.fx.support.converters.EnumStringConverter;
import de.perdian.apps.fimasu4.fx.support.converters.IdentityStringConverter;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

interface XmlElementTranslator<B> {

    void updateBeanFromElement(B targetBean, Element sourceElement);
    void extractBeanToElement(B sourceBean, Element targetElement, Document owningDocument);

    static class ParentTranslator<B> implements XmlElementTranslator<B> {

        private List<XmlElementTranslator<B>> properties = new ArrayList<>();

        <P> void registerStringProperty(String propertyName, Function<B, Property<String>> propertyFunction) {
            this.getProperties().add(new DirectPropertyTranslator<>(propertyName, propertyFunction, new IdentityStringConverter()));
        }

        <P> void registerBooleanProperty(String propertyName, Function<B, Property<Boolean>> propertyFunction) {
            this.getProperties().add(new DirectPropertyTranslator<>(propertyName, propertyFunction, new BooleanStringConverter()));
        }

        <P extends Enum<P>> void registerEnumProperty(String propertyName, Function<B, Property<P>> propertyFunction, Class<P> enumClass) {
            this.getProperties().add(new DirectPropertyTranslator<>(propertyName, propertyFunction, new EnumStringConverter<>(enumClass)));
        }

        <P> void registerListProperty(String propertyName, Function<B, ObservableList<P>> listFunction, Supplier<P> listItemSupplier, XmlElementTranslator<P> listItemTranslator) {
            this.getProperties().add(new ListPropertyTranslator<>(propertyName, listFunction, listItemSupplier, listItemTranslator, item -> true));
        }

        <P> void registerListProperty(String propertyName, Function<B, ObservableList<P>> listFunction, Supplier<P> listItemSupplier, XmlElementTranslator<P> listItemTranslator, Predicate<P> listItemPredicate) {
            this.getProperties().add(new ListPropertyTranslator<>(propertyName, listFunction, listItemSupplier, listItemTranslator, item -> true));
        }

        <P> void registerEmbeddedProperty(String propertyName, Function<B, P> propertyFunction, XmlElementTranslator<P> propertyTranslator) {
            this.getProperties().add(new EmbeddedPropertyTranslator<>(propertyName, propertyFunction, propertyTranslator));
        }

        @Override
        public void extractBeanToElement(B bean, Element targetElement, Document owningDocument) {
            for (XmlElementTranslator<B> property : this.getProperties()) {
                property.extractBeanToElement(bean, targetElement, owningDocument);
            }
        }

        @Override
        public void updateBeanFromElement(B targetBean, Element element) {
            for (XmlElementTranslator<B> property : this.getProperties()) {
                property.updateBeanFromElement(targetBean, element);
            }
        }

        private List<XmlElementTranslator<B>> getProperties() {
            return this.properties;
        }

    }

    public static class DirectPropertyTranslator<B, P> implements XmlElementTranslator<B> {

        private String propertyName = null;
        private Function<B, Property<P>> propertyFunction = null;
        private StringConverter<P> stringConverter = null;

        DirectPropertyTranslator(String propertyName, Function<B, Property<P>> propertyFunction, StringConverter<P> stringConverter) {
            this.setPropertyName(propertyName);
            this.setPropertyFunction(propertyFunction);
            this.setStringConverter(stringConverter);
        }

        @Override
        public void updateBeanFromElement(B targetBean, Element sourceElement) {
            String propertyStringValue = sourceElement.getAttribute(this.getPropertyName());
            P propertyValue = this.getStringConverter().fromString(propertyStringValue);
            Property<P> property = this.getPropertyFunction().apply(targetBean);
            property.setValue(propertyValue);
        }

        @Override
        public void extractBeanToElement(B sourceBean, Element targetElement, Document owningDocument) {
            Property<P> property = this.getPropertyFunction().apply(sourceBean);
            P propertyValue = property.getValue();
            String propertyStringValue = this.getStringConverter().toString(propertyValue);
            if (StringUtils.isNotEmpty(propertyStringValue)) {
                targetElement.setAttribute(this.getPropertyName(), propertyStringValue);
            }
        }

        private String getPropertyName() {
            return this.propertyName;
        }
        private void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        private Function<B, Property<P>> getPropertyFunction() {
            return this.propertyFunction;
        }
        private void setPropertyFunction(Function<B, Property<P>> propertyFunction) {
            this.propertyFunction = propertyFunction;
        }

        private StringConverter<P> getStringConverter() {
            return this.stringConverter;
        }
        private void setStringConverter(StringConverter<P> stringConverter) {
            this.stringConverter = stringConverter;
        }

    }

    static class EmbeddedPropertyTranslator<B, P> implements XmlElementTranslator<B> {

        private String propertyName = null;
        private Function<B, P> propertyFunction = null;
        private XmlElementTranslator<P> propertyTranslator = null;

        EmbeddedPropertyTranslator(String propertyName, Function<B, P> propertyFunction, XmlElementTranslator<P> propertyTranslator) {
            this.setPropertyName(propertyName);
            this.setPropertyFunction(propertyFunction);
            this.setPropertyTranslator(propertyTranslator);
        }

        @Override
        public void updateBeanFromElement(B targetBean, Element sourceElement) {
            NodeList childElements = sourceElement.getElementsByTagName(this.getPropertyName());
            Element childElement = childElements.getLength() <= 0 ? null : (Element)childElements.item(0);
            if (childElement != null) {
                P targetChild = this.getPropertyFunction().apply(targetBean);
                this.getPropertyTranslator().updateBeanFromElement(targetChild, childElement);
            }
        }

        @Override
        public void extractBeanToElement(B sourceBean, Element targetElement, Document owningDocument) {
            Element childElement = owningDocument.createElement(this.getPropertyName());
            P childBean = this.getPropertyFunction().apply(sourceBean);
            this.getPropertyTranslator().extractBeanToElement(childBean, childElement, owningDocument);
            targetElement.appendChild(childElement);
        }

        private String getPropertyName() {
            return this.propertyName;
        }
        private void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        private Function<B, P> getPropertyFunction() {
            return this.propertyFunction;
        }
        private void setPropertyFunction(Function<B, P> propertyFunction) {
            this.propertyFunction = propertyFunction;
        }

        private XmlElementTranslator<P> getPropertyTranslator() {
            return this.propertyTranslator;
        }
        private void setPropertyTranslator(XmlElementTranslator<P> propertyTranslator) {
            this.propertyTranslator = propertyTranslator;
        }

    }

    public static class ListPropertyTranslator<B, P> implements XmlElementTranslator<B> {

        private String listItemElementName = null;
        private Function<B, ObservableList<P>> propertyListFunction = null;
        private Supplier<P> listItemSupplier = null;
        private XmlElementTranslator<P> listItemTranslator = null;
        private Predicate<P> listItemPredicate = null;

        ListPropertyTranslator(String listItemElementName, Function<B, ObservableList<P>> propertyListFunction, Supplier<P> listItemSupplier, XmlElementTranslator<P> listItemTranslator, Predicate<P> listItemPredicate) {
            this.setListItemElementName(listItemElementName);
            this.setPropertyListFunction(propertyListFunction);
            this.setListItemSupplier(listItemSupplier);
            this.setListItemTranslator(listItemTranslator);
            this.setListItemPredicate(listItemPredicate);
        }

        @Override
        public void updateBeanFromElement(B targetBean, Element sourceElement) {
            NodeList listItemElements = sourceElement.getElementsByTagName(this.getListItemElementName());
            List<P> listItems = new ArrayList<>(listItemElements.getLength());
            for (int i=0; i < listItemElements.getLength(); i++) {
                Element listItemElement = (Element)listItemElements.item(i);
                P listItem = this.getListItemSupplier().get();
                this.getListItemTranslator().updateBeanFromElement(listItem, listItemElement);
                listItems.add(listItem);
            }
            this.getPropertyListFunction().apply(targetBean).setAll(listItems);
        }

        @Override
        public void extractBeanToElement(B sourceBean, Element targetElement, Document owningDocument) {
            List<P> itemList = this.getPropertyListFunction().apply(sourceBean);
            for (P item : itemList) {
                if (this.getListItemPredicate() == null || this.getListItemPredicate().test(item)) {
                    Element itemElement = owningDocument.createElement(this.getListItemElementName());
                    this.getListItemTranslator().extractBeanToElement(item, itemElement, owningDocument);
                    targetElement.appendChild(itemElement);
                }
            }
        }

        private String getListItemElementName() {
            return this.listItemElementName;
        }
        private void setListItemElementName(String listItemElementName) {
            this.listItemElementName = listItemElementName;
        }

        private Function<B, ObservableList<P>> getPropertyListFunction() {
            return this.propertyListFunction;
        }
        private void setPropertyListFunction(Function<B, ObservableList<P>> propertyListFunction) {
            this.propertyListFunction = propertyListFunction;
        }

        private Supplier<P> getListItemSupplier() {
            return this.listItemSupplier;
        }
        private void setListItemSupplier(Supplier<P> listItemSupplier) {
            this.listItemSupplier = listItemSupplier;
        }

        private XmlElementTranslator<P> getListItemTranslator() {
            return this.listItemTranslator;
        }
        private void setListItemTranslator(XmlElementTranslator<P> listItemTranslator) {
            this.listItemTranslator = listItemTranslator;
        }

        private Predicate<P> getListItemPredicate() {
            return this.listItemPredicate;
        }
        private void setListItemPredicate(Predicate<P> listItemPredicate) {
            this.listItemPredicate = listItemPredicate;
        }

    }

}
