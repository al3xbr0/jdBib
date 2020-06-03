package io.jdbib.bibmodel.types;

public enum EntryType {
    ARTICLE("article"),
    BOOK("book"),
    BOOKLET("booklet"),
    CONFERENCE("conference"),
    INBOOK("inbook"),
    INCOLLECTION("incollection"),
    INPROCEEDINGS("inproceedings"),
    MANUAL("manual"),
    MASTERSTHESIS("mastersthesis"),
    MISC("misc"),
    PHDTHESIS("phdthesis"),
    PROCEEDINGS("proceedings"),
    TECHREPORT("techreport"),
    UNPUBLISHED("unpublished");

    private final String name;

    public String getName() {
        return name;
    }

    EntryType(String name) {
        this.name = name;
    }
}