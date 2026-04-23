package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Bootstraps the JAX-RS application and sets the versioned API base path.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    
    @Override
    public java.util.Set<Class<?>> getClasses() {
        java.util.Set<Class<?>> classes = new java.util.HashSet<>();
        // Register resources
        classes.add(com.smartcampus.resource.DiscoveryResource.class);
        classes.add(com.smartcampus.resource.SensorResource.class);
        classes.add(com.smartcampus.resource.SensorReadingResource.class);
        classes.add(com.smartcampus.resource.SensorRoom.class);
        
        // Register exception mappers
        classes.add(com.smartcampus.exception.mapper.GlobalExceptionMapper.class);
        classes.add(com.smartcampus.exception.mapper.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.exception.mapper.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.exception.mapper.SensorUnavailableExceptionMapper.class);
        
        return classes;
    }
}
