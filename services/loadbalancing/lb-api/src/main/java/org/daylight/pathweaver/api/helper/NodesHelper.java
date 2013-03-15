package org.daylight.pathweaver.api.helper;

import org.daylight.pathweaver.datamodel.CoreNodeStatus;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.Node;

import java.util.HashSet;
import java.util.Set;

public final class NodesHelper {

    public static void setNodesToStatus(LoadBalancer lb, String status) {
        for (Node node : lb.getNodes()) {
            setNodeToStatus(status, node);
        }
    }

    /*
        Given a super-set of nodes and a subset of nodes this method will update the intersection on the loadbalancer
        with the super-set
     */
    public static void setNodesToStatus(LoadBalancer lbWithSubsetToChange, LoadBalancer lbWithSuperSet, String status) {
        Set<String> ipAddressAndPorts = new HashSet<String>();

        for (Node node : lbWithSubsetToChange.getNodes()) {
            ipAddressAndPorts.add(ipAndPortToString(node.getAddress(), node.getPort()));
        }

        for (Node node : lbWithSuperSet.getNodes()) {
            String ipAddressAndPort = ipAndPortToString(node.getAddress(), node.getPort());

            if(ipAddressAndPorts.contains(ipAddressAndPort)) {
                setNodeToStatus(status, node);
            }
        }
    }

    private static void setNodeToStatus(String status, Node node) {
        if (!node.isEnabled()) {
            node.setStatus(CoreNodeStatus.OFFLINE);
        } else {
            node.setStatus(status);
        }
    }

    private static String ipAndPortToString(String ipAddress, Integer port) {
        return ipAddress + ":" + port;
    }
}
