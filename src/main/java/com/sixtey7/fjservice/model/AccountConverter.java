package com.sixtey7.fjservice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;


@Converter
public class AccountConverter implements AttributeConverter<Account, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Account account) {
        try {
            return mapper.writeValueAsString(account);
        }
        catch (JsonProcessingException jpe) {
            System.out.println(jpe.getMessage());
            throw new RuntimeException(jpe.getMessage());
        }
    }

    @Override
    public Account convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }

        try {
            return mapper.readValue(s, Account.class);
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException(ioe.getMessage());
        }
    }
}
