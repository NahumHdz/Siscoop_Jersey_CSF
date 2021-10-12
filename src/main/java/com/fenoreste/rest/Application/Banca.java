package com.fenoreste.rest.Application;

import com.fenoreste.rest.Util.TimerBeepClock;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("gateway")
public class Banca extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        //hora();
        return resources;
    }

  public void horaEjecutaAlerta() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new TimerBeepClock();
        int initialDelay = 1;
        int periodicDelay = 1;
        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay, TimeUnit.SECONDS);

    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);
        resources.add(com.fenoreste.rest.services.AccountsResources.class);
        resources.add(com.fenoreste.rest.services.AlertsResources.class);
        resources.add(com.fenoreste.rest.services.BalancesResources.class);
        resources.add(com.fenoreste.rest.services.CardsResources.class);
        resources.add(com.fenoreste.rest.services.CountriesResources.class);
        resources.add(com.fenoreste.rest.services.CurrentBusinessDate.class);
        resources.add(com.fenoreste.rest.services.CustomerResources.class);
        resources.add(com.fenoreste.rest.services.ExchangeResources.class);
        resources.add(com.fenoreste.rest.services.FileResources.class);
        resources.add(com.fenoreste.rest.services.InstructionsResources.class);
        resources.add(com.fenoreste.rest.services.ProductsResources.class);
        resources.add(com.fenoreste.rest.services.SPEI.ResourcesSPEI.class);
        resources.add(com.fenoreste.rest.services.TimeResources.class);
        resources.add(com.fenoreste.rest.services.ValidateBeneficiary.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
    }
}
