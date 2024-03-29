package org.daylight.pathweaver.api.resource;

import org.apache.log4j.Logger;
import org.daylight.pathweaver.api.resource.provider.CommonDependencyProvider;
import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.core.api.v1.VipType;
import org.daylight.pathweaver.core.api.v1.VirtualIps;
import org.daylight.pathweaver.service.domain.entity.VirtualIp;

import org.daylight.pathweaver.service.domain.repository.VirtualIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.*;

@Controller
@Scope("request")
public class VirtualIpsResource extends CommonDependencyProvider {
    private final Logger logger = Logger.getLogger(VirtualIpsResource.class);
    private Integer accountId;
    private Integer loadBalancerId;

    @Autowired
    private VirtualIpRepository virtualIpRepository;

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response retrieveVirtualIps() {
        try {

            final List<VirtualIp> ipVips = virtualIpRepository.getVipsByLoadBalancerId(loadBalancerId);

            VirtualIps virtualIps = new VirtualIps();

            for (VirtualIp ipVip : ipVips) {
                org.daylight.pathweaver.core.api.v1.VirtualIp virtualIp = new org.daylight.pathweaver.core.api.v1.VirtualIp();
                virtualIp.setId(ipVip.getId());
                virtualIp.setAddress(ipVip.getAddress());
                virtualIp.setType(VipType.valueOf(ipVip.getVipType().name()));
                virtualIp.setIpVersion(IpVersion.fromValue(ipVip.getIpVersion().name()));
                virtualIps.getVirtualIps().add(virtualIp);
            }

            return Response.status(Response.Status.OK).entity(virtualIps).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setLoadBalancerId(Integer loadBalancerId) {
        this.loadBalancerId = loadBalancerId;
    }
}
