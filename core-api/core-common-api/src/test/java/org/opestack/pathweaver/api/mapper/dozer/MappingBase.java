package org.opestack.pathweaver.api.mapper.dozer;

import org.dozer.DozerBeanMapper;
import org.junit.BeforeClass;
import org.daylight.pathweaver.api.mapper.dozer.MapperBuilder;
import org.daylight.pathweaver.datamodel.*;

import java.util.ArrayList;
import java.util.List;

public class MappingBase {
    protected static final String configDozerConfigFile = "dozer-config-map.xml";
    protected static final String connectionThrottleDozerConfigFile = "dozer-connection-throttle-map.xml";
    protected static final String healthMonitorDozerConfigFile = "dozer-health-monitor-map.xml";
    protected static final String loadBalancerDozerConfigFile = "dozer-load-balancer-map.xml";
    protected static final String nodeDozerConfigFile = "dozer-node-map.xml";
    protected static final String sessionPersistenceDozerConfigFile = "dozer-session-persistence-map.xml";
    protected static final String usageDozerConfigFile = "dozer-usage-map.xml";
    protected static final String virtualIpDozerConfigFile = "dozer-virtual-ip-map.xml";
    protected static DozerBeanMapper mapper;

    @BeforeClass
    public static void setupMapper() {
        List<String> mappingFiles = new ArrayList<String>();
        mappingFiles.add(configDozerConfigFile);
        mappingFiles.add(connectionThrottleDozerConfigFile);
        mappingFiles.add(healthMonitorDozerConfigFile);
        mappingFiles.add(loadBalancerDozerConfigFile);
        mappingFiles.add(nodeDozerConfigFile);
        mappingFiles.add(sessionPersistenceDozerConfigFile);
        mappingFiles.add(usageDozerConfigFile);
        mappingFiles.add(virtualIpDozerConfigFile);
        mapper = MapperBuilder.getConfiguredMapper(mappingFiles);
    }

    @BeforeClass
    public static void setupAutoWiredDependencies() {
        /* TODO: Figure out how to get rid of this hack */
        PathweaverTypeHelper pathweaverTypeHelper = new PathweaverTypeHelper();
        pathweaverTypeHelper.setAlgorithmType(new CoreAlgorithmType());
        pathweaverTypeHelper.setProtocolType(new CoreProtocolType());
        pathweaverTypeHelper.setLoadBalancerStatus(new CoreLoadBalancerStatus());
        pathweaverTypeHelper.setNodeStatus(new CoreNodeStatus());
        pathweaverTypeHelper.setPersistenceType(new CorePersistenceType());
        pathweaverTypeHelper.setHealthMonitorType(new CoreHealthMonitorType());
    }
}
