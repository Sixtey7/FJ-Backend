package com.sixtey7.fjservice.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Transaction;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class TransactionConverter implements AttributeConverter<Transaction, Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Transaction account) {
        try {
            return mapper.writeValueAsString(account);
        }
        catch (JsonProcessingException jpe) {
            System.out.println(jpe.getMessage());
            throw new RuntimeException(jpe.getMessage());
        }
    }

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
                System.out.println(ioe.getMessage());
                throw new RuntimeException(ioe.getMessage());
            }
        }
        else {
            return null;
        }
    }
}