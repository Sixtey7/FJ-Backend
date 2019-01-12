package com.sixtey7.fjservice.model;

import com.sixtey7.fjservice.model.converter.TransactionConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Helper class to be used to interact with the database
 */
@Entity
@Table(name = "Transactions")
public class TransactionRecord {

    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "data")
    @Convert(converter = TransactionConverter.class)
    private Transaction data;

    /**
     * Don't want anyone hitting the default constructor
     */
    private TransactionRecord() { }

    public TransactionRecord(UUID id, Transaction data) {
        this.id = id.toString();
        this.data = data;
    }

    /**
     * Returns the id of the record
     * @return the uuid of the record
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the body of the record
     * @return the json of the body
     */
    public Transaction getData() {
        return data;
    }

    /**
     * Sets the body for the record
     * @param data the data object (as a Transaction object)
     */
    public void setData(Transaction data) {
        this.data = data;
    }
}
