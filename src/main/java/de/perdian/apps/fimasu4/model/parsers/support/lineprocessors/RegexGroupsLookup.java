package de.perdian.apps.fimasu4.model.parsers.support.lineprocessors;

import java.util.Objects;
import java.util.regex.Matcher;

public interface RegexGroupsLookup {

    String resolveGroup(Matcher matcher);

    static RegexGroupsLookup byIndex(int index) {
        return new RegexGroupsLookupByIndex(index);
    }

    static RegexGroupsLookup byName(String name) {
        return new RegexGroupsLookupByName(name);
    }

    class RegexGroupsLookupByIndex implements RegexGroupsLookup {

        private int index = 0;

        private RegexGroupsLookupByIndex(int index) {
            this.setIndex(index);
        }

        @Override
        public String resolveGroup(Matcher matcher) {
            return matcher.group(this.getIndex());
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            } else if (that instanceof RegexGroupsLookup.RegexGroupsLookupByIndex) {
                return this.getIndex() == ((RegexGroupsLookup.RegexGroupsLookupByIndex)that).getIndex();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.getIndex();
        }

        private int getIndex() {
            return this.index;
        }
        private void setIndex(int index) {
            this.index = index;
        }

    }

    class RegexGroupsLookupByName implements RegexGroupsLookup {

        private String name = null;

        private RegexGroupsLookupByName(String name) {
            this.setName(name);
        }

        @Override
        public String resolveGroup(Matcher matcher) {
            return matcher.group(this.getName());
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            } else if (that instanceof RegexGroupsLookup.RegexGroupsLookupByName) {
                return Objects.equals(this.getName(), ((RegexGroupsLookup.RegexGroupsLookupByName)that).getName());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.getName().hashCode();
        }

        private String getName() {
            return this.name;
        }
        private void setName(String name) {
            this.name = name;
        }

    }

}