package org.daylight.pathweaver.api.mapper.dozer.factory;

import org.dozer.BeanFactory;
import org.daylight.pathweaver.core.api.v1.LoadBalancer;

public class CoreLoadBalancerFactory implements BeanFactory {

    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        if (sourceClass.equals(org.daylight.pathweaver.service.domain.entity.LoadBalancer.class)) {
            LoadBalancer lb = new LoadBalancer();
            lb.setNodes(null);
            lb.setVirtualIps(null);
            return lb;
        }

        if (sourceClass.equals(LoadBalancer.class)) {
            return new org.daylight.pathweaver.service.domain.entity.LoadBalancer();
        }

        return null;
    }
}
