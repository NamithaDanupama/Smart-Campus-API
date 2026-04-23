package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings — get all readings for a sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        // Check sensor exists
        Sensor sensor = findSensor();
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No sensor exists with that ID.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        List<SensorReading> readings = DataStore.sensorHistoryMap.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings — add a new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        // Check sensor exists
        Sensor sensor = findSensor();
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No sensor exists with that ID.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Block readings if sensor is in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new com.smartcampus.exception.SensorUnavailableException(
                    "Readings blocked: Sensor '" + sensorId + "' is currently in MAINTENANCE mode."
            );
        }

        // Auto-generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Store the reading
        DataStore.sensorHistoryMap.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // Update the parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }

    // Helper method to find the sensor
    private Sensor findSensor() {
        for (Sensor sensor : DataStore.systemSensors) {
            if (sensor.getId().equals(sensorId)) {
                return sensor;
            }
        }
        return null;
    }
}