package com.sixtey7.fjservice.model.converter;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.UUID;

/**
 * Adapter class used to translate UUIDs to and from the JSON
 */
public class UUIDAdapter implements JsonbAdapter<UUID, String> {


    /**
     * Method used to translate a UUID object to a String for JSON
     * @param uuid {@link UUID} representation of the UUID
     * @return String representation of the UUID
     */
    @Override
    public String adaptToJson(UUID uuid) {
        return uuid.toString();
    }

    /**
     * Method used to translate a String from the JSON to a UUID Object
     * @param s String representation of the UUID
     * @return {@link UUID} representation of the UUID
     */
    @Override
    public UUID adaptFromJson(String s) {
        return UUID.fromString(s);
    }
}
