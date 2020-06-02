package io.jdBib.Entry.Types;

public enum FieldType {
    ADDRESS("address"),
    ANNOTE("annote"),
    AUTHOR("Author"),
    BOOKTITLE("booktitle"),
    CHAPTER("chapter"),
    CROSSREF("crossref"),
    EDITION("edition"),
    EDITOR("editor"),
    HOWPUBLISHED("howpublished"),
    INSTITUTION("institution"),
    JOURNAL("journal"),
    KEY("key"),
    MONTH("month"),
    NOTE("note"),
    NUMBER("number"),
    ORGANIZATION("organization"),
    PAGES("pages"),
    PUBLISHER("publisher"),
    SCHOOL("school"),
    SERIES("series"),
    TITLE("title"),
    TYPE("type"),
    VOLUME("volume"),
    YEAR("year");

    private final String name;

    public String getName() {
        return name;
    }

    FieldType(String name) {
        this.name = name;
    }

/*    public static FieldType get(String s){
        for (FieldType type : values()){
            if (type.getName().equalsIgnoreCase(s))
                return type;
        }
        throw new IllegalArgumentException();
    }*/
}