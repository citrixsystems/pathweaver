package org.daylight.pathweaver.datamodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class PathweaverTypeHelper {
    private static AlgorithmType algorithmType = new CoreAlgorithmType();
    private static ProtocolType protocolType = new CoreProtocolType();
    private static LoadBalancerStatus loadBalancerStatus = new CoreLoadBalancerStatus();
    private static NodeStatus nodeStatus = new CoreNodeStatus();
    private static PersistenceType persistenceType = new CorePersistenceType();
    private static HealthMonitorType healthMonitorType = new CoreHealthMonitorType();

    @Autowired(required = true)
    public void setAlgorithmType(AlgorithmType algorithmType) {
        PathweaverTypeHelper.algorithmType = algorithmType;
    }

    @Autowired(required = true)
    public void setProtocolType(ProtocolType protocolType) {
        PathweaverTypeHelper.protocolType = protocolType;
    }

    @Autowired(required = true)
    public void setLoadBalancerStatus(LoadBalancerStatus loadBalancerStatus) {
        PathweaverTypeHelper.loadBalancerStatus = loadBalancerStatus;
    }

    @Autowired(required = true)
    public void setNodeStatus(NodeStatus nodeStatus) {
        PathweaverTypeHelper.nodeStatus = nodeStatus;
    }

    @Autowired(required = true)
    public void setPersistenceType(PersistenceType persistenceType) {
        PathweaverTypeHelper.persistenceType = persistenceType;
    }

    @Autowired(required = true)
    public void setHealthMonitorType(HealthMonitorType healthMonitorType) {
        PathweaverTypeHelper.healthMonitorType = healthMonitorType;
    }

    public static boolean isValidAlgorithm(String algorithm) {
        return isValidPathweaverType(algorithm, algorithmType);
    }

    public static boolean isValidProtocol(String protocol) {
        return isValidPathweaverType(protocol, protocolType);
    }

    public static boolean isValidLoadBalancerStatus(String status) {
        return isValidPathweaverType(status, loadBalancerStatus);
    }

    public static boolean isValidNodeStatus(String status) {
        return isValidPathweaverType(status, nodeStatus);
    }

    public static boolean isValidPersistenceType(String type) {
        return isValidPathweaverType(type, persistenceType);
    }

    public static boolean isValidHealthMonitorType(String type) {
        return isValidPathweaverType(type, healthMonitorType);
    }

    private static boolean isValidPathweaverType(String string, PathweaverType pathweaverType) {
        boolean isValidString = false;
        for (int i = 0; i < pathweaverType.toList().length; i++) {
            if (pathweaverType.toList()[i].equals(string)) {
                isValidString = true;
                break;
            }
        }

        return isValidString;
    }
}
