package io.jdbib.bibmodel.types;

public enum FieldType {
    ADDRESS("address"),
    ANNOTE("annote"),
    AUTHOR("Author"),
    BOOKTITLE("booktitle"),
    CHAPTER("chapter"),
    CROSSREF("crossref"),
    DOI("doi"),
    EDITION("edition"),
    EDITOR("editor"),
    EPRINT("eprint"),
    HOWPUBLISHED("howpublished"),
    INSTITUTION("institution"),
    ISBN("ISBN"),
    ISSUE("issue"),
    JOURNAL("journal"),
    KEY("key"),
    KEYWORDS("keywords"),
    LANGUAGE("language"),
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