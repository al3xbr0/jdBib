package io.jdbib.bibmodel;

import io.jdbib.bibmodel.types.EntryType;
import io.jdbib.bibmodel.types.FieldType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class BibEntry {
    private final EntryType type;
    private String key;
    private final Map<FieldType, String> fields = new EnumMap<>(FieldType.class);
    /*{
        @Override
        public String get(Object key) {
            if (key.getClass() == FieldType.class)
            return super.get(FieldType.valueOf((String) key));
        }

        @Override
        public String put(FieldType key, String value) {
            return super.put(key, value);
        }


        public String getV(String key) {
            return super.get(FieldType.valueOf( key));
        }
    };
    public static class EFields extends EnumMap<FieldType, String>{
        public EFields() {
            super(FieldType.class);
        }
        public String getValue(String key){return ""; }

    }
    private final EFields fields = new EFields();*/

    public EntryType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Empty key");
        }
        this.key = key;
    }

    public Map<FieldType, String> getFields() {
        return new EnumMap<>(fields);
    }

    public BibEntry(EntryType type, String key) {
        this.type = type;
        setKey(key);
    }

    public BibEntry(String type, String key) {
        this(EntryType.valueOf(type.toUpperCase()), key);
    }

    public BibEntry addField(FieldWrapper field) {
        return addField(field.getKey(), field.getValue());
    }

    public BibEntry addField(String type, String value) {
        return addField(FieldType.valueOf(type.toUpperCase()), value);
    }

    public BibEntry addField(FieldType type, String value) {
        fields.put(type, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BibEntry bibEntry = (BibEntry) o;
        return type == bibEntry.type &&
                key.equals(bibEntry.key) &&
                fields.equals(bibEntry.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key, fields);
    }
}