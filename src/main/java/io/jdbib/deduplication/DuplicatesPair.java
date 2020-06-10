package io.jdbib.deduplication;

import io.jdbib.bibmodel.BibEntry;

public record DuplicatesPair(BibEntry entry1, BibEntry entry2) {
}
