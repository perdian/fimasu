package de.perdian.apps.fimasu.model.transactions;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javafx.beans.property.Property;

public class TransactionGroupSerializer {

    private static final Logger log = LoggerFactory.getLogger(TransactionGroupSerializer.class);

    private static <T> void extractAttribute(Element sourceElement, String attributeName, Property<T> property, Function<String, T> stringConverterFunction) {
        String stringValue = sourceElement.getAttribute(attributeName);
        if (!StringUtils.isEmpty(stringValue)) {
            property.setValue(stringConverterFunction.apply(stringValue));
        }
    }

}
