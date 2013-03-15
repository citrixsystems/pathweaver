package org.daylight.pathweaver.api.async;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.entity.UsageEventRecord;
import org.daylight.pathweaver.service.domain.repository.UsageEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class UsageEventListener extends BaseListener {
    private final Log logger = LogFactory.getLog(UsageEventListener.class);

    @Autowired
    private UsageEventRepository usageEventRepository;

    @Override
    public void doOnMessage(final Message message) throws Exception {
        logger.info("Processing usage event...");
        Calendar eventTime = Calendar.getInstance();
        ObjectMessage object = (ObjectMessage) message;
        LoadBalancer loadBalancer = (LoadBalancer) object.getObject();
        String usageEventType = (String) message.getObjectProperty("usageEvent");

        List<UsageEventRecord> newUsages = new ArrayList<UsageEventRecord>();

        UsageEventRecord newUsageEvent = new UsageEventRecord();
        newUsageEvent.setLoadBalancer(loadBalancer);
        newUsageEvent.setStartTime(eventTime);
        newUsageEvent.setEvent(usageEventType);
        newUsages.add(newUsageEvent);

        if (!newUsages.isEmpty()) {
            usageEventRepository.batchCreate(newUsages);
        }

        logger.info(String.format("'%s' usage event processed for load balancer '%d'.", usageEventType, loadBalancer.getId()));
    }
}