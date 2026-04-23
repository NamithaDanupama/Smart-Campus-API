package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> apiDetails = new HashMap<>();
        apiDetails.put("name", "Campus Sensor Network API");
        apiDetails.put("version", "v1");
        apiDetails.put("contact", "it-helpdesk@campus.edu");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");

        apiDetails.put("resources", endpoints);

        return Response.ok(apiDetails).build();
    }
}