package io.jdbib.parsing;

import io.jdbib.bibmodel.BibEntry;
import io.jdbib.bibmodel.BibFile;
import io.jdbib.bibmodel.FieldWrapper;
import io.jdbib.bibmodel.types.EntryType;
import io.jdbib.bibmodel.types.FieldType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BibParserTest {
    @Test
    void parseCorrectInput() {
        BibEntry expected = new BibEntry(EntryType.BOOK, "Kundur-1994")
                .addField(FieldType.TITLE, "Power System Stability and Control")
                .addField(new FieldWrapper("author", "Kundur, P."))
                .addField(new FieldWrapper("year", "1994"))
                .addField("publisher", "McGraw-Hill Education");
        try {
            String in = "@BOOK{Kundur-1994,\n" +
                    "  title={Power System Stability and Control " +
                    "  " +
                    "  },\n" +
                    "  Author={Kundur, P.},\n" +
                    "  year=1994,\n" +
                    "  publisher={McGraw-Hill Education},\n" +
                    "}";
            BibParser parser = new BibParser(in);
            BibFile result = parser.parse();
            var actual = result.getEntries().get(0);

            assertEquals(expected, actual);
            assertThrows(BibParserException.class, parser::parse,
                    "Parser error:\nFile is already parsed");
        } catch (BibParserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void parseIncorrectInput() {
        String in = "@BOOK{Kundur-1994,\n" +
                "  title=\"Power System Stability and Control\n" +
                "}";
        BibParser parser = new BibParser(in);
        assertThrows(BibParserException.class, parser::parse);
    }

    @Test
    void parseIllegalFieldInput() {
        String in = "@BOOK{Kundur-1994,\n" +
                "  testfield={testvalue}\n" +
                "}";
        BibParser parser = new BibParser(in);
        assertThrows(BibParserException.class, parser::parse);
    }
}