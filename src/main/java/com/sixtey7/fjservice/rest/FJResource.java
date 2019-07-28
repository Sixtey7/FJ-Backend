package com.sixtey7.fjservice.rest;

import com.sixtey7.fjservice.model.converter.CSVGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RESTful Service used to create and obtain FJ Service infromation
 */
@Path("/fjservice")
@RequestScoped
public class FJResource {
    // crate a logger for the class
    public static final Logger LOGGER = LogManager.getLogger(FJResource.class);

    /**
     * Helper used to generate csv data
     */
    @Inject
    private CSVGenerator csvGenerator;

    @Path("/healthz")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject healthz() {
        LOGGER.info("Returning Health Check");

        return Json.createObjectBuilder()
                .add("message", "FJService Up and Running!")
                .build();
    }


    @Path("/csvFile")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateCSVFile() {
        LOGGER.info("Generating CSV File for all objects in database");

        String returnData = csvGenerator.generateStringForAllData();

        return Response.status(200).entity(returnData).build();
    }

}
