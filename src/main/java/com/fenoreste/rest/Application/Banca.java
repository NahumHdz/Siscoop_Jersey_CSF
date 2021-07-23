package com.fenoreste.rest.Application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

@ApplicationPath("gateway")
public class Banca extends ResourceConfig {
    public Banca() {
        packages("com.fenoreste.rest.services");
        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
    }
}
