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

    public List<DuplicatesPair> search() {
        List<DuplicatesPair> duplicates = new ArrayList<>();
        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                entry1 = entries.get(i);
                entry2 = entries.get(j);

                if (currentEntriesAreDuplicates()) {
                    duplicates.add(new DuplicatesPair(entry1, entry2));
                }
            }
        }
        return duplicates;
    }

    private boolean currentEntriesAreDuplicates() {
        fields1 = entry1.getFields();
        fields2 = entry1.getFields();
        if (fields1.isEmpty() || fields2.isEmpty()) {
            return false;
        }

        if (entry1.getType() == entry2.getType()
                && fields1.equals(fields2)) {
            return true;
        }

        for (FieldType type : ID_TYPES) {
            if (fieldsAreEqual(type)) {
                return true;
            }
        }

        //special checks
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

        switch (type) {
            //here we check strings for "equality"
            case DOI, EPRINT, ISBN: //TODO: add more identificator types
                return field1 != null
                        && field1.equals(field2);
            case EDITION:
                //special check whether editions are different or not
                return field1 == null || field2 == null
                        || field1.equals(field2);
            case AUTHOR:
                //special check for author
                if (field1 == null || field2 == null) {
                    return false;
                }
                field1 = convertAuthorField(field1);
                field2 = convertAuthorField(field2);
            case TITLE:
                //special check for title
                if (field1 == null || field2 == null) {
                    return false;
                }
            case PAGES:
                //special check for pages
                if (field1 == null || field2 == null) {
                    return true;
                }
                String pages1 = field1.strip().replaceAll("[- ]+", "-");
                String pages2 = field2.strip().replaceAll("[- ]+", "-");
                return pages1.equals(pages2);
            case CHAPTER:
                //special check for chapters
                if (field1 == null || field2 == null) {
                    return true;
                }

            default:
                //here we check strings for "similarity"
                double similarity = Similarity.compareWordByWord(field1, field2);
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
        return switch (type) {
            case TITLE -> 3.;
            case AUTHOR, EDITOR -> 2.5;
            case JOURNAL -> 2.;
            default -> 1.;
        };
    }

    private static String convertAuthorField(String authors) {
        boolean moreThanOne = authors.contains(" and ");
        if (moreThanOne) {
            List<String> authorsList = Arrays.asList(authors.toLowerCase().split("\\sand\\s"));
            authorsList.replaceAll(String::strip);
            Collections.sort(authorsList);
            return String.join(" ", authorsList);
        }
        return authors;
    }
}