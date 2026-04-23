package com.smartcampus.exception.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                422,
                "https://api.university.edu/troubleshooting"
        );
        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}