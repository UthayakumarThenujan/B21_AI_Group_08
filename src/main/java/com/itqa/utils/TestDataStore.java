package com.itqa.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared in-memory store for passing data between Cucumber steps.
 * (e.g., created IDs, tokens, response bodies)
 */
public class TestDataStore {

    private static final Map<String, Object> store = new HashMap<>();

    public static void put(String key, Object value) {
        store.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) store.get(key);
    }

    public static String getString(String key) {
        Object val = store.get(key);
        return val != null ? val.toString() : null;
    }

    public static int getInt(String key) {
        Object val = store.get(key);
        if (val instanceof Integer i) return i;
        if (val instanceof String s)  return Integer.parseInt(s);
        throw new IllegalStateException("No int value for key: " + key);
    }

    public static void clear() {
        store.clear();
    }
}
