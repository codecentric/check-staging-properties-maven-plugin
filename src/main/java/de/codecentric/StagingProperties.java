package de.codecentric;

/*
 * #%L
 * check-staging-properties-maven-plugin
 * %%
 * Copyright (C) 2016 codecentric AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.*;

final class StagingProperties {

    static boolean sizesEqual(Collection<Properties> props) {
        HashSet<Integer> sizes = new HashSet<Integer>(props.size());
        for (Properties prop : props) {
            sizes.add(prop.size());
        }
        return sizes.size() == 1;
    }

    static boolean valuesPresent(Collection<Properties> props) {
        for (Properties prop : props) {
            for (Object value : prop.values()) {
                if (((String) value).length() == 0) {
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
