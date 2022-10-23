package de.perdian.apps.fimasu.model.parsers.support;

@FunctionalInterface
public interface LineProcessor {

    boolean processLine(String line);

}
