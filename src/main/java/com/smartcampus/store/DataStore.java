package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static final List<Room> roomList = new ArrayList<>();
    public static final List<Sensor> systemSensors = new ArrayList<>();

    // Map of sensorId -> list of readings for that sensor
    public static final Map<String, List<SensorReading>> sensorHistoryMap = new HashMap<>();

    static {
        Room r1 = new Room("LEC-101", "Main Lecture Theatre", 150);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        roomList.add(r1);
        roomList.add(r2);

        Sensor s1 = new Sensor("CO2-999", "Carbon Dioxide", "ACTIVE", 22.5, "LEC-101");
        systemSensors.add(s1);
        r1.getSensorIds().add("CO2-999");
    }
}