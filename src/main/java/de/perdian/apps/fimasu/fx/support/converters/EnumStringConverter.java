package de.perdian.apps.fimasu.fx.support.converters;

import javafx.util.StringConverter;

public class EnumStringConverter<E extends Enum<E>> extends StringConverter<E> {

    private Class<E> enumClass = null;

    public EnumStringConverter(Class<E> enumClass) {
        this.setEnumClass(enumClass);
    }

    @Override
    public String toString(E object) {
        return object == null ? null : object.name();
    }

    @Override
    public E fromString(String string) {
        for (E enumValue : this.getEnumClass().getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(string)) {
                return enumValue;
            }
        }
        return null;
    }

    public Class<E> getEnumClass() {
        return this.enumClass;
    }
    private void setEnumClass(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

}
