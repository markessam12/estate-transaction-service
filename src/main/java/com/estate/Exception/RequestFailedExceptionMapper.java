package com.estate.Exception;

import com.estate.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestFailedExceptionMapper implements ExceptionMapper<RequestFailedException> {

    @Override
    public Response toResponse(RequestFailedException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 412);
        return Response.status(Response.Status.PRECONDITION_FAILED)
                .entity(errorMessage)
                .build();
    }
}
