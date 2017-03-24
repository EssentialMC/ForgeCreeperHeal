package fr.eyzox.bsc.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {

    private final Map<String, ConfigOptionGroup> groups = new LinkedHashMap<String, ConfigOptionGroup>();

    public Config() {
    }

    public void addOptionGroup(final ConfigOptionGroup optionGroup) {
        this.groups.put(optionGroup.getName(), optionGroup);
    }

    public ConfigOptionGroup getOptionGroup(final String name) {
        return groups.get(name);
    }

    public Collection<ConfigOptionGroup> getOptionGroups() {
        return groups.values();
    }

}
