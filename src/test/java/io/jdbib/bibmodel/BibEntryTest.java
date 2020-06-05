package io.jdbib.bibmodel;

import io.jdbib.bibmodel.types.EntryType;
import io.jdbib.bibmodel.types.FieldType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BibEntryTest {
    @Test
    void addField() {
        BibEntry entry = new BibEntry(EntryType.BOOK, "Kundur-1994")
                .addField(FieldType.TITLE, "Power System Stability and Control")
                .addField(new FieldWrapper("author", "Kundur, P."))
                .addField(new FieldWrapper("year", "1994"));
        assertThrows(IllegalArgumentException.class, () -> entry.addField("testfield", "testvalue"));

        var fields = entry.getFields();
        assertEquals(EntryType.BOOK, entry.getType());
        assertEquals("Kundur-1994", entry.getKey());
        assertEquals("Power System Stability and Control", fields.get(FieldType.TITLE));
        assertEquals("Kundur, P.", fields.get(FieldType.AUTHOR));
        assertEquals("1994", fields.get(FieldType.YEAR));
    }

    @Test
    void setKey() {
        assertThrows(IllegalArgumentException.class, () -> new BibEntry(EntryType.ARTICLE, null));
        assertThrows(IllegalArgumentException.class, () -> new BibEntry(EntryType.ARTICLE, "  "));

        BibEntry entry = new BibEntry(EntryType.BOOK, "key");
        assertEquals("key", entry.getKey());
    }
}