package fr.eyzox.bsc.config.loader;

import fr.eyzox.bsc.exception.ConfigException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public interface IErrorManager {
    void error(ConfigException exception);

    boolean hasErrors();

    void output(final PrintWriter out) throws IOException;

    Collection<ConfigException> getErrors();
}
