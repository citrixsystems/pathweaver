package org.daylight.pathweaver.plugin;

import org.daylight.pathweaver.plugin.exception.PluginException;


import java.util.Set;

public interface FirewallPlugin {

    void createFirewall(Object fw) throws PluginException;

    void updateFirewall(Object fw) throws PluginException;

    void deleteFirewall(Object fw) throws PluginException;

}
