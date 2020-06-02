package io.jdBib.Parsing;

import io.jdBib.Entry.BibEntry;

import java.util.List;
import java.util.Map;

public class BibResult {
    private final List<String> comments;
    private final Map<String, String> strings;
    private final List<BibEntry> entries;

    public List<String> getComments() {
        return comments;
    }

    public Map<String, String> getStrings() {
        return strings;
    }

    public List<BibEntry> getEntries() {
        return entries;
    }

    public BibResult(List<String> comments, Map<String, String> strings, List<BibEntry> entries) {
        this.comments = comments;
        this.strings = strings;
        this.entries = entries;
    }
}
