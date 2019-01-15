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

    /**
     * Override the list of classes to add in our resources
     * @return {@link Set} of {@link Class} containing the resource classes to load
     */
    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(AccountResource.class, TransactionResource.class);
    }
}
