package com.sixtey7.fjservice.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;


/**
 * Converter class used to convert {@link Account} objects to and from Strings to be stored in the database
 */
@Converter
public class AccountConverter implements AttributeConverter<Account, Object> {

    /**
     * Object Mapper to be used for the conversion
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LogManager.getLogger(AccountConverter.class);

    /**
     * Converts the provided {@link Account} to a JSON String for the database
     * @param account the Account object to convert
     * @return String containing the JSON for the provided object
     */
    @Override
    public String convertToDatabaseColumn(Account account) {
        try {
            return mapper.writeValueAsString(account);
        }
        catch (JsonProcessingException jpe) {
            LOGGER.error("Failed to convert to database column", jpe);
            throw new RuntimeException(jpe.getMessage());
        }
    }

    /**
     * Converts the provided {@link PGobject} to an {@link Account}
     * @param obj The PGobject to convert
     * @return the Account object based on the provided data
     */
    @Override
    public Account convertToEntityAttribute(Object obj ) {
        if (obj instanceof PGobject) {
            PGobject pgObj = (PGobject) obj;
            String objAsString = pgObj.toString();

            if (objAsString == null) {
                return null;
            }

            try {
                return mapper.readValue(objAsString, Account.class);
            }
            catch(IOException ioe) {
                LOGGER.error("Failed to convert to entity attribute", ioe);
                throw new RuntimeException(ioe.getMessage());
            }
            catch (Exception ex) {
                LOGGER.error("Failed to convert to entity attribute", ex);
                throw new RuntimeException(ex.getMessage());
            }
        }
        else {
            return null;
        }
    }
}
