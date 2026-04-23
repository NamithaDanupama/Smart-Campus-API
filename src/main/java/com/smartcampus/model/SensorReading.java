package com.smartcampus.model;

import java.util.UUID;

public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    // Default constructor (required by Jackson)
    public SensorReading() {}

    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString(); // auto-generate unique ID
        this.timestamp = System.currentTimeMillis(); // current time in ms
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public long getTimestamp() { return timestamp; }
    public double getValue() { return value; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setValue(double value) { this.value = value; }
}