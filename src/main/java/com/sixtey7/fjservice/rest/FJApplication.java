package com.sixtey7.fjservice.rest;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.helidon.common.CollectionsHelper;

/**
 * Application hosting the services.
 */
@ApplicationScoped
@ApplicationPath("/")
public class FJApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(FJResource.class);
    }
}
