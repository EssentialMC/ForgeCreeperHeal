package fr.eyzox.bsc.config.loader;

import fr.eyzox.bsc.config.Config;
import fr.eyzox.bsc.exception.InvalidValueException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

public interface IConfigLoader {
    void load(Config config) throws AccessDeniedException, IOException, InvalidValueException;

    void save(final Config config) throws Exception;

    IErrorManager getErrorManager();
}
