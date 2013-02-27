package org.daylight.pathweaver.common.config.osgi.cfg;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(
                new String[]{"org.daylight.pathweaver.cfg.Configuration"},
                new ConfigurationServiceFactory(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //Nothing to see here...
    }
}
