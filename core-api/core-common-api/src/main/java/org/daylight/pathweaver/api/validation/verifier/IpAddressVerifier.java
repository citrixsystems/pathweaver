package org.daylight.pathweaver.api.validation.verifier;

import org.daylight.pathweaver.api.validation.util.IPString.IPUtils;

public class IpAddressVerifier implements Verifier<String> {

    @Override
    public VerifierResult verify(String ipAddress) {
        return new VerifierResult(IPUtils.isValidIpv4String(ipAddress) || IPUtils.isValidIpv6String(ipAddress));
    }
}