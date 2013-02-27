package org.daylight.pathweaver.api.validation.verifier;


import org.daylight.pathweaver.core.api.v1.Node;
import org.daylight.pathweaver.core.api.v1.Nodes;

import java.util.ArrayList;

public class ActiveNodeVerifier implements Verifier<ArrayList<Node>> {

    public VerifierResult verify(ArrayList<Node> nodes) {
        if (nodes == null) return new VerifierResult(true);
        for (Node node : nodes) {
            // If node condition is null it is defaulted to ENABLED
            if(node.isEnabled() == null || node.isEnabled()) {
                return new VerifierResult(true);
            }
        }
        return new VerifierResult(false);
    }
}

