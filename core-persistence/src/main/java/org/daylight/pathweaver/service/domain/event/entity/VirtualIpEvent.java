package org.daylight.pathweaver.service.domain.event.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@javax.persistence.Entity
@Table(name = "virtual_ip_event")
public class VirtualIpEvent extends Event implements Serializable {

    private final static long serialVersionUID = 532512316L;
    @Column(name = "virtual_ip_id", nullable = false)
    private Integer virtualIpId;

    public Integer getVirtualIpId() {
        return virtualIpId;
    }

    public void setVirtualIpId(Integer virtualIpId) {
        this.virtualIpId = virtualIpId;
    }

    @Override
    public String getAttributesAsString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getAttributesAsString());
        sb.append(String.format("vip=%s", vorn(getVirtualIpId())));
        return sb.toString();
    }
}
