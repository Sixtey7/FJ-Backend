package com.sixtey7.fjservice.utils;

import com.sixtey7.fjservice.model.Transaction;

import javax.enterprise.context.Dependent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Helper class for transaction transformations
 */
@Dependent
public class TransHelper {

    /**
     * Fixes the date on the provided {@link Transaction} from {@link String} to {@link LocalDate}
     * @param transToMassage the transaction to fix
     * @return {@link Transaction} with a fixed date
     */
    public Transaction fixDateForTrans(Transaction transToMassage) {
        //TODO: There might be a cleaner way to do this, without creating an Instant
        //transToMassage.setDate(
         //       LocalDate.ofInstant(Instant.parse(transToMassage.getDate()), ZoneId.of(ZoneOffset.UTC.getId())).toString());

        return transToMassage;
    }

}
