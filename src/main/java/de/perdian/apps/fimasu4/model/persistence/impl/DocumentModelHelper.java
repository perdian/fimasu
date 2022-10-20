package de.perdian.apps.fimasu4.model.persistence.impl;

class DocumentModelHelper {

    static <T extends Enum<T>> T resolveEnumValue(Class<T> enumClass, String enumString, T defaultValue) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(enumString)) {
                return enumValue;
            }
        }
        return defaultValue;
    }

}
