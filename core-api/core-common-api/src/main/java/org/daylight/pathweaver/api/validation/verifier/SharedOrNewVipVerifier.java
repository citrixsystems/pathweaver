package org.daylight.pathweaver.api.validation.verifier;


import org.daylight.pathweaver.core.api.v1.VirtualIp;
import org.daylight.pathweaver.core.api.v1.VirtualIps;
import org.daylight.pathweaver.api.validation.expectation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class SharedOrNewVipVerifier implements Verifier<ArrayList<VirtualIp>> {

    @Override
    public VerifierResult verify(ArrayList<VirtualIp> virtualIps) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

        if (virtualIps == null || virtualIps.size() > 1) {
            validationResults.add(new ValidationResult(false, "Must have exactly one virtual ip"));
            return new VerifierResult(false, validationResults);
        }

        for (VirtualIp virtualIp : virtualIps) {
            if (virtualIp.getType() != null) {
                if (virtualIp.getId() != null) {
                    validationResults.add(new ValidationResult(false, "Must specify either a shared or new virtual ip."));
                    return new VerifierResult(false, validationResults);
                }
            } else {
                if (virtualIp.getId() == null) {
                    validationResults.add(new ValidationResult(false, "Must specify either a shared or new virtual ip."));
                    return new VerifierResult(false, validationResults);
                }
            }
        }

        return new VerifierResult(true);
    }
}
