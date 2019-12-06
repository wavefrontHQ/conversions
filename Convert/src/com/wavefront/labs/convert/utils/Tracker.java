package com.wavefront.labs.convert.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tracker {
    public static Map map = new HashMap();

    public static void addToList(String key, String obj) {
        System.out.println(obj);
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList());
        }
        ((List) map.get(key)).add(obj);
    }

    public static void increment(String key) {
        if (!map.containsKey(key)) {
            map.put(key, 1);
        } else {
            map.put(key, ((Integer) map.get(key)) + 1);
        }
    }
}
