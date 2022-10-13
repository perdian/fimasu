package de.perdian.apps.fimasu4.model.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Values {

    private Map<String, String> attributes = null;
    private Map<String, List<Values>> children = null;

    public Values() {
        this.setAttributes(new HashMap<>());
        this.setChildren(new HashMap<>());
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        toStringBuilder.append("attributes", this.getAttributes());
        return toStringBuilder.toString();
    }

    public String getAttribute(String name, String defaultValue) {
        return StringUtils.defaultIfEmpty(this.getAttributes().get(name), defaultValue);
    }

    public void setAttribute(String name, String value) {
        if (StringUtils.isEmpty(value)) {
            this.getAttributes().remove(name);
        } else {
            this.getAttributes().put(name, value);
        }
    }

    public Values getFirstChild(String key) {
        List<Values> children = this.getChildren().get(key);
        return children == null || children.isEmpty() ? null : children.get(0);
    }

    public List<Values> getChildren(String key) {
        return this.getChildren().get(key);
    }

    public void addChildren(String key, List<Values> children) {
        this.getChildren().computeIfAbsent(key, k -> new ArrayList<>()).addAll(children);
    }

    public void addChildren(String key, Values children) {
        this.getChildren().computeIfAbsent(key, k -> new ArrayList<>()).add(children);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }
    private void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, List<Values>> getChildren() {
        return this.children;
    }
    private void setChildren(Map<String, List<Values>> children) {
        this.children = children;
    }

}
