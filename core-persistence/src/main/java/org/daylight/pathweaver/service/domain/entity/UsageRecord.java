package org.daylight.pathweaver.service.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@javax.persistence.Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "vendor",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("CORE")
@Table(name = "load_balancer_usage")
public class UsageRecord extends org.daylight.pathweaver.service.domain.entity.Entity implements Serializable {
    private final static long serialVersionUID = 532512316L;

    @ManyToOne
    @JoinColumn(name="load_balancer_id", nullable = false)
    private LoadBalancer loadBalancer;

    @Column(name = "event", nullable = true)
    private String event;

    @Column(name = "transfer_bytes_in", nullable = false)
    private Long transferBytesIn;

    @Column(name = "transfer_bytes_out", nullable = false)
    private Long transferBytesOut;

    @Column(name = "last_bytes_in_count", nullable = false)
    private Long lastBytesInCount;

    @Column(name = "last_bytes_out_count", nullable = false)
    private Long lastBytesOutCount;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endTime;

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Long getTransferBytesIn() {
        return transferBytesIn;
    }

    public void setTransferBytesIn(Long transferBytesIn) {
        this.transferBytesIn = transferBytesIn;
    }

    public Long getTransferBytesOut() {
        return transferBytesOut;
    }

    public void setTransferBytesOut(Long transferBytesOut) {
        this.transferBytesOut = transferBytesOut;
    }

    public Long getLastBytesInCount() {
        return lastBytesInCount;
    }

    public void setLastBytesInCount(Long lastBytesInCount) {
        this.lastBytesInCount = lastBytesInCount;
    }

    public Long getLastBytesOutCount() {
        return lastBytesOutCount;
    }

    public void setLastBytesOutCount(Long lastBytesOutCount) {
        this.lastBytesOutCount = lastBytesOutCount;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "UsageRecord{" +
                "endTime=" + endTime +
                ", startTime=" + startTime +
                ", lastBytesOutCount=" + lastBytesOutCount +
                ", lastBytesInCount=" + lastBytesInCount +
                ", transferBytesOut=" + transferBytesOut +
                ", transferBytesIn=" + transferBytesIn +
                ", event='" + event + '\'' +
                '}';
    }
}
