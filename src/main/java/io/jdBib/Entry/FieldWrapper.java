package io.jdBib.Entry;

import java.util.AbstractMap;

public class FieldWrapper extends AbstractMap.SimpleEntry<String, String> {
    public FieldWrapper(String key, String value) {
        super(key, value);
    }
}