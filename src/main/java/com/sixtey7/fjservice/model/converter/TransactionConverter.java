package com.sixtey7.fjservice.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class TransactionConverter implements AttributeConverter<Transaction, Object> {

    /**
     * Object Mapper to be used for the conversion
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LogManager.getLogger(AccountConverter.class);

    /**
     * Converts the provided {@link Transaction} to a JSON String for the database
     * @param transaction the Transaction to convert
     * @return String containing the JSON for the provided object
     */
    @Override
    public String convertToDatabaseColumn(Transaction transaction) {
        try {
            return mapper.writeValueAsString(transaction);
        }
        catch (JsonProcessingException jpe) {
            LOGGER.error("Failed to convert to database column", jpe);
            throw new RuntimeException(jpe.getMessage());
        }
    }

    /**
     * Converts the provided {@link PGobject} to an {@link Transaction}
     * @param obj The PGobject to convert
     * @return the Transaction based on the provided data
     */
    @Override
    public Transaction convertToEntityAttribute(Object obj) {
        if (obj instanceof PGobject) {
            PGobject pgObj = (PGobject) obj;
            String objAsString = pgObj.toString();
            if (objAsString == null) {
                return null;
            }

            try {
                return mapper.readValue(objAsString, Transaction.class);
            } catch (IOException ioe) {
                LOGGER.error("Failed to convert to entity attribute", ioe);
                throw new RuntimeException(ioe.getMessage());
            }
        }
        else {
            return null;
        }
    }
}