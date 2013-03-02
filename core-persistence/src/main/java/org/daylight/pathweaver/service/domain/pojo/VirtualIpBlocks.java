package org.daylight.pathweaver.service.domain.pojo;

import org.daylight.pathweaver.service.domain.entity.VirtualIpType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VirtualIpBlocks implements Serializable {
    private final static long serialVersionUID = 532512316L;
    private List<VirtualIpBlock> virtualIpBlocks;
    private VirtualIpType type;

    public List<VirtualIpBlock> getVirtualIpBlocks() {
        if (virtualIpBlocks == null) {
            virtualIpBlocks = new ArrayList<VirtualIpBlock>();
        }
        return this.virtualIpBlocks;
    }

    public VirtualIpType getType() {
        return type;
    }

    public void setType(VirtualIpType value) {
        this.type = value;
    }

    public void setVirtualIpBlocks(List<VirtualIpBlock> virtualIpBlocks) {
        this.virtualIpBlocks = virtualIpBlocks;
    }

}
