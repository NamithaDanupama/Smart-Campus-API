package com.smartcampus.exception.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                409,
                "https://api.university.edu/troubleshooting"
        );
        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}