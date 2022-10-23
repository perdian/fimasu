package de.perdian.apps.fimasu4.model.parsers.support;

@FunctionalInterface
public interface LineProcessor {

    boolean processLine(String line);

}
