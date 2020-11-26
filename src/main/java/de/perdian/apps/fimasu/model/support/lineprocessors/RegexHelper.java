package de.perdian.apps.fimasu.model.support.lineprocessors;

import java.util.regex.Matcher;

class RegexHelper {

    static String extractGroupForName(Matcher matcher, String groupName) {
        try {
            return matcher.group(groupName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
