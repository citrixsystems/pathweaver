package org.daylight.pathweaver.service.domain.entity;

import org.daylight.pathweaver.datamodel.PathweaverTypeHelper;
import org.daylight.pathweaver.datamodel.CoreAlgorithmType;
import org.daylight.pathweaver.datamodel.CoreProtocolType;
import org.daylight.pathweaver.service.domain.pojo.VirtualIpDozerWrapper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "vendor",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("CORE")
@Table(name = "load_balancer")
public class LoadBalancer extends Entity implements Serializable {
    private final static long serialVersionUID = 532512316L;

    @Column(name = "account_id", nullable = false, length = 32)
    private Integer accountId;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "algorithm", nullable = false)
    private String algorithm = CoreAlgorithmType.ROUND_ROBIN;

    @Column(name = "protocol", nullable = false)
    private String protocol = CoreProtocolType.HTTP;

    @Column(name = "port", nullable = false)
    private Integer port = 80;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar created;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updated;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_on_plugin", nullable = true)
    private Boolean created_on_plugin;

    @OneToMany(mappedBy = "loadBalancer", fetch = FetchType.EAGER)
    private Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();

    @OrderBy("id")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loadBalancer", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Node> nodes = new HashSet<Node>();

    @OrderBy("id")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loadBalancer", fetch = FetchType.LAZY)
    private Set<UsageRecord> usage = new HashSet<UsageRecord>();

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "loadBalancer")
    private SessionPersistence sessionPersistence;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "loadBalancer")
    private ConnectionThrottle connectionThrottle;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "loadBalancer")
    private HealthMonitor healthMonitor;

    @Transient
    private VirtualIpDozerWrapper virtualIpDozerWrapper;

    public LoadBalancer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<LoadBalancerJoinVip> getLoadBalancerJoinVipSet() {
        if (loadBalancerJoinVipSet == null) {
            loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();
        }
        return loadBalancerJoinVipSet;
    }

    public void setLoadBalancerJoinVipSet(Set<LoadBalancerJoinVip> loadBalancerJoinVipSet) {
        this.loadBalancerJoinVipSet = loadBalancerJoinVipSet;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public Set<UsageRecord> getUsageRecordSet() {
        return usage;
    }

    public void setUsage(Set<UsageRecord> usage) {
        this.usage = usage;
    }

    public HealthMonitor getHealthMonitor() {
        return healthMonitor;
    }

    public void setHealthMonitor(HealthMonitor healthMonitor) {
        this.healthMonitor = healthMonitor;
    }

    public ConnectionThrottle getConnectionThrottle() {
        return connectionThrottle;
    }

    public void setConnectionThrottle(ConnectionThrottle connectionThrottle) {
        this.connectionThrottle = connectionThrottle;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!PathweaverTypeHelper.isValidLoadBalancerStatus(status)) {
            throw new RuntimeException("Load balancer status not supported.");
        }
        this.status = status;
    }

    public Boolean isCreatedOnPlugin() {
        return created_on_plugin;
    }

    public void setCreatedOnPlugin(Boolean createdOnPlugin) {
        if (!PathweaverTypeHelper.isValidLoadBalancerStatus(status)) {
            throw new RuntimeException("Load balancer status not supported.");
        }
        this.created_on_plugin = createdOnPlugin;
    }




    public VirtualIpDozerWrapper getVirtualIpDozerWrapper() {
        return new VirtualIpDozerWrapper(loadBalancerJoinVipSet);
    }


    public void setVirtualIpDozerWrapper(VirtualIpDozerWrapper virtualIpDozerWrapper) {
        this.virtualIpDozerWrapper = virtualIpDozerWrapper;
        this.setLoadBalancerJoinVipSet(this.virtualIpDozerWrapper.getLoadBalancerJoinVipSet());
    }

    public SessionPersistence getSessionPersistence() {
        return sessionPersistence;
    }

    public void setSessionPersistence(SessionPersistence sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }
}
