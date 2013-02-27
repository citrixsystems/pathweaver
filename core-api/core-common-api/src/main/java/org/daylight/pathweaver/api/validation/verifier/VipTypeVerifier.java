package org.daylight.pathweaver.api.validation.verifier;

import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.core.api.v1.VipType;
import org.daylight.pathweaver.core.api.v1.VirtualIp;
import org.daylight.pathweaver.api.validation.expectation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class VipTypeVerifier implements Verifier<VirtualIp> {

    @Override
    public VerifierResult verify(VirtualIp vip) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (vip.getType() != null && vip.getIpVersion() == IpVersion.IPV6) {
            if (vip.getType() != VipType.PUBLIC) {
                validationResults.add(new ValidationResult(false, "Ip must be of PUBLIC type for IPV6 version."));
                return new VerifierResult(false, validationResults);
            }
        }
        
        return new VerifierResult(true);
    }
}
