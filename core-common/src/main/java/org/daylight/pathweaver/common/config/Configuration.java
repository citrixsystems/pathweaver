package org.daylight.pathweaver.common.config;

public interface Configuration {

    String getString(ConfigurationKey key) throws ConfigurationInitializationException;

    boolean hasKeys(ConfigurationKey... keys) throws ConfigurationInitializationException;
}
