package com.sixtey7.fjservice.rest;

import io.helidon.common.CollectionsHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * Application hosting the services.
 */
@ApplicationScoped
@ApplicationPath("/")
public class FJApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(FJResource.class, AccountResource.class, TransactionResource.class);
    }
}
