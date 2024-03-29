package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.api.integration.ReverseProxyLoadBalancerService;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.pojo.MessageDataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public abstract class BaseListener implements MessageListener {
    private Log logger = LogFactory.getLog(BaseListener.class);
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ReverseProxyLoadBalancerService reverseProxyLoadBalancerService;

    public final void onMessage(Message message) {
        try {
            doOnMessage(message);
        } catch (Exception e) {
            logger.error("Error processing JMS message", e);
        }
    }

    public abstract void doOnMessage(Message message) throws Exception;

    protected MessageDataContainer getDataContainerFromMessage(Message message) throws JMSException {
        ObjectMessage object = (ObjectMessage) message;
        return (MessageDataContainer) object.getObject();
    }

    protected void notifyUsageProcessor(final Message message, final LoadBalancer loadBalancer, final String event) throws JMSException {
        logger.debug("Sending notification to usage processor...");
        final String finalDestination = "USAGE_EVENT";
        jmsTemplate.send(finalDestination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                ObjectMessage response = session.createObjectMessage(loadBalancer);
                response.setJMSCorrelationID(message.getJMSCorrelationID());
                response.setObjectProperty("usageEvent", event);
                return response;
            }
        });
    }

    public ReverseProxyLoadBalancerService getReverseProxyLoadBalancerService() {
        return reverseProxyLoadBalancerService;
    }
}
