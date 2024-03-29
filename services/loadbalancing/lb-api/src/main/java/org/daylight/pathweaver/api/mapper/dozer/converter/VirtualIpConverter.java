package org.daylight.pathweaver.api.mapper.dozer.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dozer.CustomConverter;
import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.core.api.v1.VipType;
import org.daylight.pathweaver.core.api.v1.VirtualIp;
import org.daylight.pathweaver.core.api.v1.VirtualIps;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.entity.LoadBalancerJoinVip;
import org.daylight.pathweaver.service.domain.exception.EntityNotFoundException;
import org.daylight.pathweaver.service.domain.exception.NoMappableConstantException;
import org.daylight.pathweaver.service.domain.pojo.VirtualIpDozerWrapper;
import org.daylight.pathweaver.common.ip.exception.IPStringConversionException1;



import java.util.*;

import static org.daylight.pathweaver.core.api.v1.VipType.PUBLIC;
import static org.daylight.pathweaver.core.api.v1.VipType.PRIVATE;


public class VirtualIpConverter implements CustomConverter {
    private final Log logger = LogFactory.getLog(VirtualIpConverter.class);



    private VirtualIps getDataModelVirtualIps(Object sourceFieldValue) {
        VirtualIpDozerWrapper dozerWrapper = (VirtualIpDozerWrapper) sourceFieldValue;

        VirtualIps vips = new VirtualIps();

         try {
            for (LoadBalancerJoinVip loadBalancerJoinVip : dozerWrapper.getLoadBalancerJoinVipSet()) {
                VirtualIp vip = new VirtualIp();
                vip.setId(loadBalancerJoinVip.getVirtualIp().getId());
                vip.setAddress(loadBalancerJoinVip.getVirtualIp().getAddress());
                switch (loadBalancerJoinVip.getVirtualIp().getIpVersion()) {
                    case IPV4:
                        vip.setIpVersion(IpVersion.IPV4);
                        break;
                    case IPV6:
                        vip.setIpVersion(IpVersion.IPV6);
                        break;
                    default:
                        throw new RuntimeException(String.format("Unsupported vip Ip version '%s' given while mapping.", loadBalancerJoinVip.getVirtualIp().getIpVersion().name()));
                }

                switch (loadBalancerJoinVip.getVirtualIp().getVipType()) {
                    case PUBLIC:
                        vip.setType(PUBLIC);
                        break;
                    case PRIVATE:
                        vip.setType(PRIVATE);
                        break;
                    default:
                        throw new RuntimeException(String.format("Unsupported vip type '%s' given while mapping.", loadBalancerJoinVip.getVirtualIp().getVipType().name()));
                }

                vips.getVirtualIps().add(vip);

            }
         } catch (NullPointerException e) {
             //Ignore, there is nothing to map
        }

        return vips;
    }


    private VirtualIpDozerWrapper getEntityVirtualIpDozerWrapper(Object sourceFieldValue) {

        if (sourceFieldValue instanceof ArrayList) {
            ArrayList<org.daylight.pathweaver.core.api.v1.VirtualIp> vips = (ArrayList<org.daylight.pathweaver.core.api.v1.VirtualIp>) sourceFieldValue;

            if (vips.size() > 1) {
                throw new RuntimeException("Cannot specify more than one Virtual IP per loadBalancer");
            }
            Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();

            if (vips.size() > 0) {
                VirtualIp vip = vips.get(0);

                if (vip.getId() == null) {

                    if (vip.getIpVersion() == null) {
                        if (vip.getType() != null && vip.getType().equals(VipType.PRIVATE)) {
                            vip.setIpVersion(IpVersion.IPV4);
                        } else {
                            vip.setIpVersion(IpVersion.IPV6);
                            vip.setType(VipType.PUBLIC);
                        }
                    }
                }

                loadBalancerJoinVipSet = buildLoadBalancerJoinVipSet(vip);
            }

            VirtualIpDozerWrapper dozerWrapper = new VirtualIpDozerWrapper();
            dozerWrapper.setLoadBalancerJoinVipSet(loadBalancerJoinVipSet);
            return dozerWrapper;

        } else {

            VirtualIps vips = (VirtualIps) sourceFieldValue;
            Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();


            for (VirtualIp vip : vips.getVirtualIps()) {
                if (vip.getIpVersion() == null)  {
                    vip.setIpVersion(IpVersion.IPV4);
                }

                if (vip.getIpVersion().equals(IpVersion.IPV4)) {
                    LoadBalancerJoinVip loadBalancerJoinVip = new LoadBalancerJoinVip();
                    org.daylight.pathweaver.service.domain.entity.VirtualIp domainVip = new org.daylight.pathweaver.service.domain.entity.VirtualIp();
                    domainVip.setId(vip.getId());
                    domainVip.setAddress(vip.getAddress());

                    switch (vip.getType()) {
                        case PUBLIC:
                            domainVip.setVipType(VirtualIpType.PUBLIC);
                            break;
                        case PRIVATE:
                            domainVip.setVipType(VirtualIpType.PRIVATE);
                            break;
                    }

                    switch (vip.getIpVersion()) {
                        case IPV4:
                            domainVip.setIpVersion(org.daylight.pathweaver.service.domain.entity.IpVersion.IPV4);
                            break;
                        case IPV6:
                            domainVip.setIpVersion(org.daylight.pathweaver.service.domain.entity.IpVersion.IPV6);
                            break;
                    }

                    loadBalancerJoinVip.setVirtualIp(domainVip);
                    loadBalancerJoinVipSet.add(loadBalancerJoinVip);
                }
            }

            VirtualIpDozerWrapper dozerWrapper = new VirtualIpDozerWrapper();
            dozerWrapper.setLoadBalancerJoinVipSet(loadBalancerJoinVipSet);
            return dozerWrapper;
        }
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

        if (sourceFieldValue == null) {
            return null;
        }

        if (sourceFieldValue instanceof VirtualIpDozerWrapper) {
                  return getDataModelVirtualIps(sourceFieldValue) ;
        }

        if (sourceFieldValue instanceof ArrayList || sourceFieldValue instanceof VirtualIps) {
                  return getEntityVirtualIpDozerWrapper(sourceFieldValue);
        }


        throw new NoMappableConstantException("Cannot map source type: " + sourceClass.getName());
    }

    private Set<LoadBalancerJoinVip> buildLoadBalancerJoinVipSet(VirtualIp vip) {
        LoadBalancerJoinVip loadBalancerJoinVip = new LoadBalancerJoinVip();
        Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();

        org.daylight.pathweaver.service.domain.entity.VirtualIp domainVip = new org.daylight.pathweaver.service.domain.entity.VirtualIp();


        domainVip.setId(vip.getId());
        domainVip.setAddress(vip.getAddress());

        if (vip.getType() != null)
        {
            switch (vip.getType()) {
                case PUBLIC:
                    domainVip.setVipType(VirtualIpType.PUBLIC);
                    break;
                case PRIVATE:
                    domainVip.setVipType(VirtualIpType.PRIVATE);
                    break;
            }
        }

        if (vip.getIpVersion() != null)
        {
            switch (vip.getIpVersion()) {
                case IPV4:

                    domainVip.setIpVersion(org.daylight.pathweaver.service.domain.entity.IpVersion.IPV4);
                    break;
                case IPV6:
                    domainVip.setIpVersion(org.daylight.pathweaver.service.domain.entity.IpVersion.IPV6);
                    break;
            }
        }


        loadBalancerJoinVip.setVirtualIp(domainVip);
        loadBalancerJoinVipSet.add(loadBalancerJoinVip);

        return loadBalancerJoinVipSet;
    }
}
