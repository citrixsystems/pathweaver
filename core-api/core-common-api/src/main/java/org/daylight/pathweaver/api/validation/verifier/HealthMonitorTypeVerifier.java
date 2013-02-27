package org.daylight.pathweaver.api.validation.verifier;

import org.daylight.pathweaver.core.api.v1.HealthMonitor;
import org.daylight.pathweaver.datamodel.CoreHealthMonitorType;

public class HealthMonitorTypeVerifier implements Verifier<HealthMonitor> {
    private final CoreHealthMonitorType healthMonitorType;

    public HealthMonitorTypeVerifier(CoreHealthMonitorType healthMonitorType) {
        this.healthMonitorType = healthMonitorType;
    }

    @Override
    public VerifierResult verify(HealthMonitor monitor) {
        return new VerifierResult(monitor != null && healthMonitorType.getType().equals(monitor.getType()));
    }
}
