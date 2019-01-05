package com.sixtey7.fjservice.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;


@Converter
public class AccountConverter implements AttributeConverter<Account, Object> {
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
                System.out.println("ERROR!!" + ioe.getMessage());
                throw new RuntimeException(ioe.getMessage());
            }
            catch (Exception ex) {
                System.out.println("ERROR!!" + ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }
        else {
            return null;
        }
    }
}
