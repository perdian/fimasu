package de.perdian.apps.fimasu4.model.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu4.model.persistence.Values;
import de.perdian.apps.fimasu4.model.persistence.ValuesStore;

public class XmlBackedValuesStore implements ValuesStore {

    private Document document = null;

    public XmlBackedValuesStore(Document document) {
        this.setDocument(document);
    }

    @Override
    public Values createValues() {
        return this.createValuesFromElement(this.getDocument().getDocumentElement());
    }

    private Values createValuesFromElement(Element sourceElement) {

        Values values = new Values();

        NamedNodeMap attributeMap = sourceElement.getAttributes();
        for (int i=0; i < attributeMap.getLength(); i++) {
            Attr attributeNode = (Attr)attributeMap.item(i);
            String attributeValue = attributeNode.getNodeValue();
            if (StringUtils.isNotEmpty(attributeValue)) {
                values.setAttribute(attributeNode.getName(), attributeValue);
            }
        }

        Map<String, List<Values>> elementChildrenValues = new HashMap<>();
        NodeList elementChildren = sourceElement.getChildNodes();
        for (int i=0; i < elementChildren.getLength(); i++) {
            Node elementChild = elementChildren.item(i);
            if (elementChild.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)elementChild;
                List<Values> targetList = elementChildrenValues.computeIfAbsent(element.getNodeName(), key -> new ArrayList<>());
                targetList.add(this.createValuesFromElement(element));
            }
        }
        for (Map.Entry<String, List<Values>> childEntry : elementChildrenValues.entrySet()) {
            values.addChildren(childEntry.getKey(), childEntry.getValue());
        }

        return values;

    }

    @Override
    public void storeValues(Values values) {
        this.storeValuesIntoElement(values, this.getDocument().getDocumentElement());
    }

    private void storeValuesIntoElement(Values values, Element targetElement) {
        for (Map.Entry<String, String> attributeEntry : values.getAttributes().entrySet()) {
            targetElement.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
        }
        for (Map.Entry<String, List<Values>> childEntry : values.getChildren().entrySet()) {
            for (Values childValues : childEntry.getValue()) {
                Element childElement = this.getDocument().createElement(childEntry.getKey());
                this.storeValuesIntoElement(childValues, childElement);
                targetElement.appendChild(childElement);
            }
        }
    }

    private Document getDocument() {
        return this.document;
    }
    private void setDocument(Document document) {
        this.document = document;
    }

}
