package org.daylight.pathweaver.api.config;

import org.daylight.pathweaver.common.config.osgi.cfg.commons.ApacheCommonsConfiguration;
import org.springframework.stereotype.Component;

@Component
public class RestApiConfiguration extends ApacheCommonsConfiguration {
    public static final String defaultConfigurationLocation = "/etc/daylight/pathweaver/public-api.conf";

    public RestApiConfiguration() {
        super(defaultConfigurationLocation);
    }
}
