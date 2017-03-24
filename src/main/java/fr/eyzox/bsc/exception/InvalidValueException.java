package fr.eyzox.bsc.exception;

import fr.eyzox.bsc.config.option.IConfigOption;

public class InvalidValueException extends ConfigException {

    private final String expected;
    private final String value;
    private IConfigOption configOption;

    public InvalidValueException(final String value) {
        this(value, null, null);
    }

    public InvalidValueException(final String newValue, final String expected) {
        this(newValue, expected, null);
    }

    public InvalidValueException(final String value, final String expected, final Throwable cause) {
        super(cause);
        this.expected = expected;
        this.value = value;
    }

    private IConfigOption getConfigOption() {
        return configOption;
    }

    public void setConfigOption(IConfigOption configOption) {
        this.configOption = configOption;
    }

    private String getExpected() {
        return expected;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        final StringBuilder s = new StringBuilder();
        if (getConfigOption() != null) {
            s.append(getConfigOption().getName());
            s.append(" : ");
        }
        s.append("Unvalid value : ");
        s.append(getValue());
        if (getExpected() != null) {
            s.append(" expected : ");
            s.append(getExpected());
        }
        return s.toString();
    }

}
