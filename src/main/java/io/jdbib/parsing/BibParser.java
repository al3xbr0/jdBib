package io.jdbib.parsing;

import io.jdbib.bibmodel.BibEntry;
import io.jdbib.bibmodel.BibFile;
import io.jdbib.bibmodel.FieldWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BibParser {
    private final String input;

    private int position;
    private BibEntry currentEntry;

    private final List<String> comments = new ArrayList<>();
    private final Map<String, String> strings = new HashMap<>();
    private final List<BibEntry> entries = new ArrayList<>();

    public BibParser(String input) {
        position = 0;
        this.input = input != null ? input + "\n" : "";
    }

    public BibFile parse() throws BibParserException {
        if (position != 0) {
            throw new BibParserException("File is already parsed");
        }
        while (tryMatch('@')) {
            String d = directive().toUpperCase();
            match('{');
            switch (d) {
                case "@COMMENT":
                    comment();
                    break;
                case "@STRING":
                    string();
                    break;
                case "@PREAMBLE":
                    valueBuild();
                    break;
                default:
                    entryBody(d);
                    break;
            }
            match('}');
        }
        return new BibFile(comments, strings, entries);
    }

    private int getErrLine() {
        String s = input.substring(0, position);
        return s.length() - s.replace("\n", "").length() + 1;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(input.charAt(position))) {
            if (++position >= input.length()) {
                return;
            }
        }
        if (input.charAt(position) == '%') {
            while (input.charAt(position) != '\n') {
                position++;
            }
            skipWhitespace();
        }
    }

    private void match(char sym) throws BibParserException {
        skipWhitespace();
        if (input.charAt(position) == sym) {
            position++;
        } else {
            throw new BibParserException("Token mismatch", getErrLine());
        }
        skipWhitespace();
    }

    private boolean tryMatch(char sym) {
        if (position >= input.length()) {
            return false;
        }
        skipWhitespace();
        return input.charAt(position) == sym;
    }

    private String valueInBraces() throws BibParserException {
        int bracecount = 0;
        match('{');
        int start = position;
        while (true) {
            if (input.charAt(position) == '}' && input.charAt(position - 1) != '\\') {
                if (bracecount > 0) {
                    bracecount--;
                } else {
                    int end = position;
                    match('}');
                    return input.substring(start, end);
                }
            } else if (input.charAt(position) == '{' && input.charAt(position - 1) != '\\') {
                bracecount++;
            } else if (position == input.length() - 1) {
                throw new BibParserException("Unterminated value", getErrLine());
            }
            position++;
        }
    }

    private String valueInQuotes() throws BibParserException {
        match('"');
        int start = position;
        while (true) {
            if (input.charAt(position) == '"' && input.charAt(position - 1) != '\\') {
                int end = position;
                match('"');
                return input.substring(start, end);
            } else if (position == input.length() - 1) {
                throw new BibParserException("Unterminated value", getErrLine());
            }
            position++;
        }
    }

    private String singleValue() throws BibParserException {
        if (tryMatch('{')) {
            return valueInBraces();
        } else if (tryMatch('"')) {
            return valueInQuotes();
        } else {
            String k = key();
            if (strings.containsKey(k)) {
                return strings.get(k);
            } else if (k.matches("^[0-9]+$")) {
                return k;
            } else {
                throw new BibParserException("Value expected:", getErrLine());
            }
        }
    }

    private String key() throws BibParserException {
        int start = position;
        while (true) {
            if (position == input.length()) {
                throw new BibParserException("Runaway key", getErrLine());
            }
            if (input.substring(position, position + 1).matches("[a-zA-Z0-9_:\\\\./-]")) {
                position++;
            } else {
                return input.substring(start, position);
            }
        }
    }

    private String valueBuild() throws BibParserException {
        List<String> values = new ArrayList<>();
        values.add(singleValue());
        while (tryMatch('#')) {
            match('#');
            values.add(singleValue());
        }
        return String.join("", values);
    }

    private FieldWrapper fieldBuilder() throws BibParserException {
        String k = key();
        if (tryMatch('=')) {
            match('=');
            String v = valueBuild();
            v = v.replaceAll("\\s*\\n+\\s*", " ").strip();
            return new FieldWrapper(k, v);
        } else {
            throw new BibParserException("'key = value' expected, equals sign missing", getErrLine());
        }
    }

    private void fieldsList() throws BibParserException {
        FieldWrapper field = fieldBuilder();
        try {
            currentEntry.addField(field);
            while (tryMatch(',')) {
                match(',');
                if (tryMatch('}')) {
                    break;
                }
                field = fieldBuilder();
                currentEntry.addField(field);
            }
        } catch (IllegalArgumentException e) {
            throw new BibParserException("Illegal field type", getErrLine());
        }
        entries.add(currentEntry);
    }

    private void entryBody(String d) throws BibParserException {
        try {
            currentEntry = new BibEntry(d.substring(1), key());
        } catch (IllegalArgumentException e) {
            throw new BibParserException("Illegal entry type", getErrLine());
        }
        match(',');
        fieldsList();
    }

    private String directive() throws BibParserException {
        match('@');
        return "@" + key();
    }

    private void string() throws BibParserException {
        FieldWrapper kv = fieldBuilder();
        strings.put(kv.getKey(), kv.getValue());
    }

    private void comment() throws BibParserException {
        int start = position;
        while (true) {
            if (position == input.length()) {
                throw new BibParserException("Runaway comment", getErrLine());
            }
            if (input.charAt(position) != '}') {
                position++;
            } else {
                comments.add(input.substring(start, position));
                return;
            }
        }
    }
}