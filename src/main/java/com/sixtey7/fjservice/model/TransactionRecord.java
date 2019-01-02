package com.sixtey7.fjservice.model;

import javax.json.JsonObject;
import java.util.UUID;

/**
 * Helper class to be used to interact with the database
 */
public class TransactionRecord {

    private UUID id;
    private JsonObject body;

    /**
     * Don't want anyone hitting the default constructor
     */
    private TransactionRecord() { }

    /**
     * Returns the id of the record
     * @return the uuid of the record
     */
    public UUID getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the body of the record
     * @return the json of the body
     */
    public JsonObject getBody() {
        return body;
    }

    /**
     * Sets the body for the record
     * @param body the body object (as a JsonObject)
     */
    public void setBody(JsonObject body) {
        this.body = body;
    }
}
