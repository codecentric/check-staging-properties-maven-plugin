package de.codecentric;

import java.util.*;

final class StagingProperties {

    static boolean sizesEqual(Collection<Properties> props) {
        HashSet<Integer> sizes = new HashSet<Integer>(props.size());
        for (Properties prop : props) {
            sizes.add(prop.size());
        }
        return sizes.size() == 1;
    }

    static boolean valuesAreEmpty(Collection<Properties> props) {
        for (Properties prop : props) {
            for (Object value : prop.values()) {
                if (((String) value).length() != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean keysEqual(Collection<Properties> props) {
        ArrayList<Set<Object>> keysList = new ArrayList<Set<Object>>(props.size());
        for (Properties prop : props) {
            keysList.add(prop.keySet());
        }

        Set<Object> firstKeys = keysList.get(0);
        List<Set<Object>> keysSubList = keysList.subList(1, keysList.size());
        for (Set<Object> keys : keysSubList) {
            if (!firstKeys.equals(keys)) {
                return false;
            }
        }
        return true;
    }
}
