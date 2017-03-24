package fr.eyzox.bsc.config;

public interface IConfigProvider {
    Config getConfig();

    void addConfigListener(final IConfigListener listener);

    void fireConfigChanged();

    void loadConfig();

    void unloadConfig();
}
