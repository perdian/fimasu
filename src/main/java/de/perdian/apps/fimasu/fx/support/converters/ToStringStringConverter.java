package de.perdian.apps.fimasu.fx.support.converters;

import javafx.util.StringConverter;

public class ToStringStringConverter<T> extends StringConverter<T> {

    @Override
    public String toString(T object) {
        return object == null ? "" : object.toString();
    }

    @Override
    public T fromString(String string) {
        throw new UnsupportedOperationException();
    }

}
