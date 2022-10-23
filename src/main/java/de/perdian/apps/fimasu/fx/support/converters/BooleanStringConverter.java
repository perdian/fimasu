package de.perdian.apps.fimasu.fx.support.converters;

import javafx.util.StringConverter;

public class BooleanStringConverter extends StringConverter<Boolean> {

    @Override
    public String toString(Boolean object) {
        return object == null ? null : object.toString();
    }

    @Override
    public Boolean fromString(String string) {
        return Boolean.valueOf(string);
    }

}
