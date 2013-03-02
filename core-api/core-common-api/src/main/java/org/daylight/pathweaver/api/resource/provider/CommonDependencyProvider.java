package org.daylight.pathweaver.api.resource.provider;

import org.dozer.DozerBeanMapper;
import org.daylight.pathweaver.api.faults.HttpResponseBuilder;
import org.daylight.pathweaver.api.integration.AsyncService;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.core.api.v1.exceptions.BadRequest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CommonDependencyProvider {
    protected final static String NOBODY = "Undefined User";
    protected final static String USERHEADERNAME = "X-PP-User";
    protected final static String VFAIL = "Validation Failure";

    @Autowired
    private DozerBeanMapper dozerMapper;
    @Autowired
    private AsyncService asyncService;

    public String getUserName(HttpHeaders headers){
        if(headers == null || headers.getRequestHeader(USERHEADERNAME).size()<1){
            return NOBODY;
        }
        String userName = headers.getRequestHeader(USERHEADERNAME).get(0);
        if(userName == null){
            return NOBODY;
        }
        return userName;
    }

     public Response getValidationFaultResponse(ValidatorResult result) {
        List<String> vmessages = result.getValidationErrorMessages();
        int status = 400;
        BadRequest badreq = HttpResponseBuilder.buildBadRequestResponse(VFAIL, vmessages);
        return Response.status(status).entity(badreq).build();
    }

    public Response getValidationFaultResponse(List<String> errorStrs) {
        BadRequest badreq;
        int status = 400;
        badreq = HttpResponseBuilder.buildBadRequestResponse(VFAIL, errorStrs);
        return Response.status(status).entity(badreq).build();
    }

    protected DozerBeanMapper getDozerMapper() {
        return this.dozerMapper;
    }

    protected AsyncService getAsyncService() {
        return asyncService;
    }

}
