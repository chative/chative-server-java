package com.github.difftim.security.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {

    public static List<String> listToLowerCase(List<String> list) {
        for (int i=0; i < list.size(); i++) {
            list.set(i, list.get(i).toLowerCase(Locale.ROOT));
        }
        return list;
    }

    public static Map<String, String> mapToLowerCase(Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }

        return result;
    }
}
