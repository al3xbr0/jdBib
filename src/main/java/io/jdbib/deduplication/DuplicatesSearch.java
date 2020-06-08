package io.jdbib.deduplication;

import io.jdbib.bibmodel.BibEntry;
import io.jdbib.bibmodel.BibFile;
import io.jdbib.bibmodel.types.FieldType;

import java.util.*;

import static io.jdbib.bibmodel.types.FieldType.*;

public class DuplicatesSearch {
    private static final FieldType[] ID_TYPES = {DOI, EPRINT, ISBN};

    private final BibFile file;
    private final List<BibEntry> entries;

    private BibEntry entry1, entry2;
    private Map<FieldType, String> fields1, fields2;

    public DuplicatesSearch(BibFile file) {
        this.file = file;
        this.entries = file.getEntries();
    }

    public Queue<List<BibEntry>> search() {
        Queue<List<BibEntry>> duplicates = new LinkedList<>();
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                entry1 = entries.get(i);
                entry2 = entries.get(j);

                if (currentEntriesAreDuplicates()) {
                    LinkedList<BibEntry> duplicatesPair = new LinkedList<>();
                    duplicatesPair.addFirst(entry1);
                    duplicatesPair.addLast(entry2);
                    duplicates.offer(duplicatesPair);
                }
            }
        }
        return duplicates;
    }

    private boolean currentEntriesAreDuplicates() {
        if (entry1.equals(entry2)) {
            return true;
        }

        fields1 = entry1.getFields();
        fields2 = entry1.getFields();

        for (FieldType type : ID_TYPES) {
            if (fieldsAreEqual(type)) {
                return true;
            }
        }

        boolean haveDifferentChaptersOrPagesInOneEntry
                = fieldsAreEqual(AUTHOR) && fieldsAreEqual(TITLE)
                && (!fieldsAreEqual(PAGES) || !fieldsAreEqual(CHAPTER));
        if (entry1.getType() != entry2.getType()
                || !fieldsAreEqual(EDITION)
                || haveDifferentChaptersOrPagesInOneEntry) {
            return false;
        }

        Set<FieldType> commonFields = EnumSet.copyOf(fields1.keySet());
        commonFields.retainAll(fields2.keySet());
        return fieldsSetsAreEqual(commonFields);
    }

    private boolean fieldsAreEqual(FieldType type) {
        String field1 = fields1.get(type);
        String field2 = fields2.get(type);
        double similarity;
        switch (type) {
            case DOI:
            case EPRINT:
            case ISBN: //TODO
                return field1 != null
                        && field1.equals(field2);
            case EDITION:
                if (field1 == null || field2 == null) {
                    return true;
                }
                return field1.equals(field2);
            case AUTHOR:
                if (field1 == null || field2 == null) {
                    return false;
                }
                String authors1 = convertAuthorField(field1);
                String authors2 = convertAuthorField(field2);
                similarity = Similarity.compareWordByWord(authors1, authors2);
                return similarity > 0.8;
            case TITLE:
                if (field1 == null || field2 == null) {
                    return false;
                }
            case PAGES:
                if (field1 == null || field2 == null) {
                    return true;
                }
                String pages1 = field1.strip().replaceAll("[- ]+", "-");
                String pages2 = field2.strip().replaceAll("[- ]+", "-");
                return pages1.equals(pages2);
            case CHAPTER:
                if (field1 == null || field2 == null) {
                    return true;
                }
                String chapter1 = field1.strip().toLowerCase().replaceAll("\\schapter", "");
                String chapter2 = field2.strip().toLowerCase().replaceAll("\\schapter", "");
                similarity = Similarity.compareWordByWord(chapter1, chapter2);
                return similarity > 0.8;
            default:
                similarity = Similarity.compareWordByWord(field1.trim(), field2.trim());
                return similarity > 0.8;
        }
    }

    private boolean fieldsSetsAreEqual(Set<FieldType> types) {
        double equals = 0.;
        double weights = 0.;
        for (FieldType type : types) {
            double fieldWeight = fieldWeight(type);
            weights += fieldWeight;
            if (fieldsAreEqual(type)) {
                equals += fieldWeight;
            }
        }
        if (weights > 0.) {
            return equals / weights >= 0.75;
        }
        return false;
    }

    private static double fieldWeight(FieldType type) {
        switch (type) {
            case TITLE:
                return 3.;
            case AUTHOR:
            case EDITOR:
                return 2.5;
            case JOURNAL:
                return 2.;
            default:
                return 1.;
        }
    }

    private static String convertAuthorField(String authors) {
        boolean moreThanOne = authors.contains(" and ");
        if (moreThanOne) {
            List<String> authorsList = new ArrayList<>(Arrays.asList(authors.toLowerCase().split(" and ")));
            authorsList.replaceAll(String::strip);
            Collections.sort(authorsList);
            return String.join(" ", authorsList);
        }
        return authors.strip();
    }
}