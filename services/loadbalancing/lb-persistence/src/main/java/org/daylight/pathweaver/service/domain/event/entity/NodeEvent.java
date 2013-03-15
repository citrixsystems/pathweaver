package org.daylight.pathweaver.service.domain.event.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity
@Table(name = "node_event")
public class NodeEvent extends Event implements Serializable {

    private final static long serialVersionUID = 532512316L;
    @Column(name = "node_id", nullable = false)
    private Integer nodeId;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getAttributesAsString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getAttributesAsString());
        sb.append(String.format(" nodeId=%s", vorn(getNodeId())));
        return sb.toString();
    }
}
