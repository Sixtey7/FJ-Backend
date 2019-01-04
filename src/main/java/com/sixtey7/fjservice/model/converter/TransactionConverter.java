package com.sixtey7.fjservice.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Transaction;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class TransactionConverter implements AttributeConverter<Transaction, String> {
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
    public Transaction convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }

        try {
            return mapper.readValue(s, Transaction.class);
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        }
    }
}