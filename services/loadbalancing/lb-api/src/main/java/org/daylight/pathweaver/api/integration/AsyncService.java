package org.daylight.pathweaver.api.integration;

import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;

import javax.jms.JMSException;

public interface AsyncService {
    public void callAsyncLoadBalancingOperation(final String operation, final MessageDataContainer dataContainer) throws JMSException;
}
