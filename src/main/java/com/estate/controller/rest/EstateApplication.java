package com.estate.controller.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * The rest api initiator. It adds to the url application path
 * Base path of the app is: <a href="http://localhost:8080/SampleProject-1.0-SNAPSHOT/api/estate">...</a>
 */
@ApplicationPath("/api/estate")
public class EstateApplication extends Application {
}
