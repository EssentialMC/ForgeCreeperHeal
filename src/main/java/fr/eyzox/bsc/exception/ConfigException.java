package fr.eyzox.bsc.exception;

public class ConfigException extends RuntimeException {

    ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(String message) {
        super(message);
    }

    ConfigException(Throwable cause) {
        super(cause);
    }


}
