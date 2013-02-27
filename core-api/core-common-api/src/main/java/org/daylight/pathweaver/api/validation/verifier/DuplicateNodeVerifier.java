package org.daylight.pathweaver.api.validation.verifier;


import org.daylight.pathweaver.core.api.v1.Node;
import org.daylight.pathweaver.core.api.v1.Nodes;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;

public class DuplicateNodeVerifier implements Verifier<ArrayList<Node>> {

    public VerifierResult verify(ArrayList<Node> nodes) {
        Set<String> ipAddressesAndPorts = new HashSet<String>();
        if(nodes == null) {
            return new VerifierResult(true); // Don't flag the user for duplicate nodes when the nodes must not be empty verifier already flagged the user.
        }
        for (Node node : nodes) {
            String ipAddressAndPort = node.getAddress() + ":" + node.getPort();
            if (!ipAddressesAndPorts.add(ipAddressAndPort)) {
                return new VerifierResult(false);
            }
        }
        return new VerifierResult(true);
    }
}
