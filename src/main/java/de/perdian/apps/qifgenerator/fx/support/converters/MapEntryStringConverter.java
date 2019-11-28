package de.perdian.apps.qifgenerator.fx.support.converters;

import java.util.function.Function;

import javafx.util.StringConverter;

public class MapEntryStringConverter<T> extends StringConverter<T> {

    private Function<T, String> valueToStringFunction = null;

    public MapEntryStringConverter(Function<T, String> valueToStringFunction) {
        this.setValueToStringFunction(valueToStringFunction);
    }

    @Override
    public String toString(T object) {
        return this.getValueToStringFunction().apply(object);
    }

    @Override
    public T fromString(String string) {
        throw new UnsupportedOperationException();
    }

    private Function<T, String> getValueToStringFunction() {
        return this.valueToStringFunction;
    }
    private void setValueToStringFunction(Function<T, String> valueToStringFunction) {
        this.valueToStringFunction = valueToStringFunction;
    }

}