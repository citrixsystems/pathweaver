package org.daylight.pathweaver.service.domain.entity;

import org.daylight.pathweaver.service.domain.exception.NoMappableConstantException;

import java.io.Serializable;

public enum IpVersion implements Serializable {
    IPV4(org.daylight.pathweaver.core.api.v1.IpVersion.IPV4),
    IPV6(org.daylight.pathweaver.core.api.v1.IpVersion.IPV6);

    private final static long serialVersionUID = 532512316L;
    private final org.daylight.pathweaver.core.api.v1.IpVersion myType;

    IpVersion(org.daylight.pathweaver.core.api.v1.IpVersion myType) {
        this.myType = myType;
    }

    public org.daylight.pathweaver.core.api.v1.IpVersion getDataType() {
        return myType;
    }

    public static IpVersion fromDataType(org.daylight.pathweaver.core.api.v1.IpVersion type) {
        for (IpVersion value : values()) {
            if (type == value.getDataType()) {
                return value;
            }
        }
        
        throw new NoMappableConstantException("Could not map constant: " + type.value() + " for type: " + type.name());
    }
}

