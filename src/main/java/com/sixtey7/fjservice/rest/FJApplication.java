package com.sixtey7.fjservice.rest;

import io.helidon.common.CollectionsHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    //Logger for the class
    public static final Logger LOGGER = LogManager.getLogger(FJApplication.class);
    /**
     * Override the list of classes to add in our resources
     * @return {@link Set} of {@link Class} containing the resource classes to load
     */
    @Override
    public Set<Class<?>> getClasses() {
        LOGGER.debug("Setting up the Application...");
        return CollectionsHelper.setOf(AccountResource.class, TransactionResource.class);
    }
}
