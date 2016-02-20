package com.github.alien11689.messagenbrokers.jms;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HealthCheck {
    @GET
    @Path("/")
    public String ready() {
        return "OK";
    }
}
