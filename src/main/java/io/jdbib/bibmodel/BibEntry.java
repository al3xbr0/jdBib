package io.jdbib.bibmodel;

import io.jdbib.bibmodel.types.EntryType;
import io.jdbib.bibmodel.types.FieldType;

import java.util.EnumMap;
import java.util.Map;

public class BibEntry {
    private final EntryType type;
    private String citeKey;
    private final Map<FieldType, String> fields = new EnumMap<>(FieldType.class);

    public EntryType getType() {
        return type;
    }

    public String getCiteKey() {
        return citeKey;
    }

    public void setCiteKey(String citeKey) {
        if (citeKey == null || citeKey.isBlank()) {
            throw new IllegalArgumentException("Empty key");
        }
        this.citeKey = citeKey;
    }

    public Map<FieldType, String> getFields() {
        return new EnumMap<>(fields);
    }

    public BibEntry(EntryType type, String citeKey) {
        this.type = type;
        setCiteKey(citeKey);
    }

    public BibEntry(String type, String citeKey) {
        this(EntryType.valueOf(type.toUpperCase()), citeKey);
    }

    public BibEntry addField(FieldWrapper field) {
        return addField(field.key(), field.value());
    }

    public BibEntry addField(String type, String value) {
        return addField(FieldType.valueOf(type.toUpperCase()), value);
    }

    public BibEntry addField(FieldType type, String value) {
        fields.put(type, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("@" + type.getName() + "{" + citeKey);
        fields.forEach((key, value) ->
                sb.append(",\n").append(key.getName()).append(" = {").append(value).append("}"));
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BibEntry bibEntry = (BibEntry) o;

        if (type != bibEntry.type) return false;
        if (!citeKey.equals(bibEntry.citeKey)) return false;
        return fields.equals(bibEntry.fields);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + citeKey.hashCode();
        result = 31 * result + fields.hashCode();
        return result;
    }
}