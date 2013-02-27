package org.daylight.pathweaver.api.mapper.dozer;

import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import org.daylight.pathweaver.core.api.v1.LoadBalancer;

public class EventListener implements DozerEventListener {

    @Override
    public void mappingStarted(DozerEvent dozerEvent) {
        // Not implemented
    }

    @Override
    public void preWritingDestinationValue(DozerEvent dozerEvent) {
        // Not implemented
    }

    @Override
    public void postWritingDestinationValue(DozerEvent dozerEvent) {
        // Not implemented
    }

    @Override
    public void mappingFinished(DozerEvent dozerEvent) {
        if(dozerEvent.getDestinationObject() instanceof LoadBalancer) {
            LoadBalancer lb = (LoadBalancer) dozerEvent.getDestinationObject();

            if(lb.getVirtualIps() != null && lb.getVirtualIps().getVirtualIps().isEmpty()) lb.setVirtualIps(null);
            if(lb.getNodes() != null && lb.getNodes().getNodes().isEmpty()) lb.setNodes(null);
        }
    }
}
