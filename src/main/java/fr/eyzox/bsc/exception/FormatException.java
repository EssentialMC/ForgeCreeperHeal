package fr.eyzox.bsc.exception;

public class FormatException extends ConfigException {

    public FormatException(final String type, final String expected) {
        this(type, expected, null);
    }

    private FormatException(final String type, final String expected, final Throwable cause) {
        super("Invalid data type : \"" + type + "\" expected : \"" + expected + "\"", cause);
    }

}
