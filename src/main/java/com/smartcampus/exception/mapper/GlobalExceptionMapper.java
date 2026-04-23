package com.smartcampus.exception.mapper;

import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // If the exception is already a WebApplicationException (like 404, 400),
        // let it return its own response instead of masking it as a 500 error.
        if (exception instanceof javax.ws.rs.WebApplicationException) {
            return ((javax.ws.rs.WebApplicationException) exception).getResponse();
        }

        // Log the real error on the server side for debugging
        LOGGER.severe("Unexpected error: " + exception.getMessage());
        exception.printStackTrace(); // Useful for debugging locally

        // But never send the real stack trace to the client
        ErrorMessage error = new ErrorMessage(
                "A critical server fault occurred. Contact the administrator.",
                500,
                "https://api.university.edu/troubleshooting"
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}