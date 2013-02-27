package org.daylight.pathweaver.service.domain.pojo;

import org.daylight.pathweaver.service.domain.entity.LoadBalancer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {

    private final static long serialVersionUID = 532512316L;
    private Integer accountId;
    private List<LoadBalancer> loadBalancers;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public List<LoadBalancer> getLoadBalancers() {
        if (loadBalancers == null) {
            loadBalancers = new ArrayList<LoadBalancer>();
        }
        return loadBalancers;
    }

    public void setLoadBalancers(List<LoadBalancer> loadBalancers) {
        this.loadBalancers = loadBalancers;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
