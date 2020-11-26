package de.perdian.apps.fimasu.model.support.lineprocessors;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.value.WritableValue;

public class RegexGroupsLineProcessor extends RegexLineProcessor {

    private Map<RegexGroupsLookup, List<Consumer<String>>> consumers = null;

    public RegexGroupsLineProcessor(String pattern) {
        super(pattern);
        this.setConsumers(new HashMap<>());
    }

    @Override
    protected void processLineWithMatcher(String line, Matcher lineMatcher) {
        this.getConsumers().forEach((groupLookup, consumers) -> {
            String groupValue = groupLookup.resolveGroup(lineMatcher);
            consumers.forEach(consumer -> consumer.accept(groupValue));
        });
    }

    public RegexGroupsLineProcessor addDate(RegexGroupsLookup groupLookup, WritableValue<LocalDate> groupProperty, DateTimeFormatter dateFormatter) {
        return this.add(groupLookup, groupProperty, value -> StringUtils.isEmpty(value) ? null : LocalDate.parse(value, dateFormatter));
    }

    public RegexGroupsLineProcessor addString(RegexGroupsLookup groupLookup, WritableValue<String> groupProperty) {
        return this.add(groupLookup, groupProperty, Function.identity());
    }

    public RegexGroupsLineProcessor addNumber(RegexGroupsLookup groupLookup, WritableValue<Number> groupProperty, NumberFormat numberFormat) {
        return this.add(groupLookup, groupProperty, value -> {
            try {
                return StringUtils.isEmpty(value) ? null : numberFormat.parse(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid numberic value: " + value, e);
            }
        });
    }

    public <T> RegexGroupsLineProcessor add(RegexGroupsLookup groupLookup, WritableValue<T> groupProperty, Function<String, T> stringToObjectFunction) {
        this.getConsumers().compute(groupLookup, (k, v) -> v == null ? new ArrayList<>() : v).add(value -> groupProperty.setValue(stringToObjectFunction.apply(value)));
        return this;
    }

    private Map<RegexGroupsLookup, List<Consumer<String>>> getConsumers() {
        return this.consumers;
    }
    private void setConsumers(Map<RegexGroupsLookup, List<Consumer<String>>> consumers) {
        this.consumers = consumers;
    }

}