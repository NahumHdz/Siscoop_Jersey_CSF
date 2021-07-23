/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Application;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 *
 * @author wilmer
 */
@ApplicationPath("v1.0/")
public class SPEI extends ResourceConfig {
    public SPEI() {
        packages("com.fenoreste.rest.services");
        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
    }
}