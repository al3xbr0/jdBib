package io.jdbib.parsing;

public class BibParserException extends Exception {

    public BibParserException(String message, int line) {
        super("Parser error:\n" + message + " at line " + line);
    }

    public BibParserException(String message) {
        super("Parser error:\n" + message);
    }
}