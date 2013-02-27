package org.daylight.pathweaver.api.resource;

import org.daylight.pathweaver.api.response.ResponseFactory;
import org.daylight.pathweaver.core.api.v1.Algorithm;
import org.daylight.pathweaver.core.api.v1.Algorithms;
import org.daylight.pathweaver.datamodel.AlgorithmType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

@Controller
@Scope("request")
public class AlgorithmsResource {

    @Autowired
    AlgorithmType algorithmType;

    @GET
    public Response retrieveLoadBalancingAlgorithms() {
        try {
            Algorithms algorithms = new Algorithms();

            for (String algorithmName : algorithmType.toList()) {
                Algorithm algorithm = new Algorithm();
                algorithm.setName(algorithmName);
                algorithms.getAlgorithms().add(algorithm);
            }

            return Response.status(Response.Status.OK).entity(algorithms).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e);
        }
    }
}
