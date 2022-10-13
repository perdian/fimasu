package de.perdian.apps.fimasu4.model.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.perdian.apps.fimasu4.model.persistence.Values;
import de.perdian.apps.fimasu4.model.persistence.ValuesStore;

public class XmlBackedValuesStore implements ValuesStore {

    private Element element = null;

    public XmlBackedValuesStore(Element element) {
        this.setElement(element);
    }

    public Values createValues() {
        return this.createValuesFromElement(this.getElement());
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
            values.setChildren(childEntry.getKey(), childEntry.getValue());
        }

        return values;

    }

    public void storeValues(Values values) {
    }

    private Element getElement() {
        return this.element;
    }
    private void setElement(Element element) {
        this.element = element;
    }

}
