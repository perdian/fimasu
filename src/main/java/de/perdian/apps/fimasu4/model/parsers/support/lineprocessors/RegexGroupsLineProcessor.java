package de.perdian.apps.fimasu4.model.parsers.support.lineprocessors;

import java.math.BigDecimal;
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
            consumers.forEach(consumer -> consumer.accept(groupValue == null ? null : groupValue.strip()));
        });
    }

    public <T> RegexGroupsLineProcessor set(RegexGroupsLookup groupLookup, WritableValue<T> groupProperty, Function<String, T> stringToObjectFunction) {
        this.getConsumers().compute(groupLookup, (k, v) -> v == null ? new ArrayList<>() : v).add(value -> groupProperty.setValue(stringToObjectFunction.apply(value)));
        return this;
    }

    public RegexGroupsLineProcessor setString(RegexGroupsLookup groupLookup, WritableValue<String> groupProperty) {
        return this.set(groupLookup, groupProperty, Function.identity());
    }

    public RegexGroupsLineProcessor setDate(RegexGroupsLookup groupLookup, WritableValue<LocalDate> groupProperty, DateTimeFormatter dateFormatter) {
        return this.set(groupLookup, groupProperty, value -> StringUtils.isEmpty(value) ? null : LocalDate.parse(value, dateFormatter));
    }

    public RegexGroupsLineProcessor setNumber(RegexGroupsLookup groupLookup, WritableValue<BigDecimal> groupProperty, NumberFormat numberFormat) {
        return this.setNumber(groupLookup, groupProperty, numberFormat, 1d);
    }

    public RegexGroupsLineProcessor setNumber(RegexGroupsLookup groupLookup, WritableValue<BigDecimal> groupProperty, NumberFormat numberFormat, double numberSign) {
        return this.set(groupLookup, groupProperty, value -> {
            try {
                return StringUtils.isEmpty(value) ? null : BigDecimal.valueOf(numberFormat.parse(value).doubleValue() * numberSign);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid numberic value: " + value, e);
            }
        });
    }

    public RegexGroupsLineProcessor addNumber(RegexGroupsLookup groupLookup, WritableValue<BigDecimal> groupProperty, NumberFormat numberFormat) {
        return this.addNumber(groupLookup, groupProperty, numberFormat, 1);
    }

    public RegexGroupsLineProcessor addNumber(RegexGroupsLookup groupLookup, WritableValue<BigDecimal> groupProperty, NumberFormat numberFormat, double numberSign) {
        return this.set(groupLookup, groupProperty, value -> {
            try {
                Number existingValue = groupProperty.getValue();
                Number newValue = StringUtils.isEmpty(value) ? null : numberFormat.parse(value);
                if (existingValue == null && newValue == null) {
                    return null;
                } else {
                    return BigDecimal.valueOf((existingValue == null ? 0d : existingValue.doubleValue()) + (newValue == null ? 0 : newValue.doubleValue() * numberSign));
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid numberic value: " + value, e);
            }
        });
    }

    private Map<RegexGroupsLookup, List<Consumer<String>>> getConsumers() {
        return this.consumers;
    }
    private void setConsumers(Map<RegexGroupsLookup, List<Consumer<String>>> consumers) {
        this.consumers = consumers;
    }

}
