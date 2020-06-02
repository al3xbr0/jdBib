package io.jdBib.Parsing;

import io.jdBib.Entry.BibEntry;
import io.jdBib.Entry.FieldWrapper;
import io.jdBib.Entry.Types.EntryType;
import io.jdBib.Entry.Types.FieldType;
import org.junit.jupiter.api.Assertions;
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
                    "  title={Power System Stability and Control" +
                    "  " +
                    "  },\n" +
                    "  Author={Kundur, P.},\n" +
                    "  year=1994,\n" +
                    "  publisher={McGraw-Hill Education},\n" +
                    "}";
            BibParser parser = new BibParser(in);
            BibResult result = parser.parse();
            var actual = result.getEntries().get(0);

            Assertions.assertEquals(expected, actual);
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