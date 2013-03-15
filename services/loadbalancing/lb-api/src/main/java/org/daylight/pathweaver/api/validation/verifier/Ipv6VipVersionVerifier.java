package org.daylight.pathweaver.api.validation.verifier;

import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.api.validation.expectation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class Ipv6VipVersionVerifier implements Verifier<IpVersion> {

    @Override
    public VerifierResult verify(IpVersion version) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (version != IpVersion.IPV6) {
            validationResults.add(new ValidationResult(false, "Ip version must be IPv6"));
            return new VerifierResult(false, validationResults);
        }

        return new VerifierResult(true);
    }

}
