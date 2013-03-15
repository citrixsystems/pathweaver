package org.daylight.pathweaver.common.config;

import org.daylight.pathweaver.common.config.osgi.cfg.commons.ApacheCommonsConfiguration;

public class LbConfiguration extends ApacheCommonsConfiguration {
    public static final String defaultConfigurationLocation = "/etc/daylight/pathweaver/public-api.conf";

    public LbConfiguration() {
        super(defaultConfigurationLocation);
    }
}