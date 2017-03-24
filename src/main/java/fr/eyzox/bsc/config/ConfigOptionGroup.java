package fr.eyzox.bsc.config;

import fr.eyzox.bsc.config.option.IConfigOption;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigOptionGroup {

    private final String name;
    private final Map<String, IConfigOption> options = new LinkedHashMap<String, IConfigOption>();

    public ConfigOptionGroup(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addOption(final IConfigOption option) {
        this.options.put(option.getName(), option);
    }

    public IConfigOption getOption(final String name) {
        return options.get(name);
    }

    public Collection<IConfigOption> getOptions() {
        return options.values();
    }

}
