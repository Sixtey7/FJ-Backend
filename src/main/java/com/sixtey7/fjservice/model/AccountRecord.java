package com.sixtey7.fjservice.model;

import com.sixtey7.fjservice.model.converter.AccountConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Helper class to be used to interact with the database
 */
@Entity
@Table(name = "Accounts")
public class AccountRecord {

    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "data")
    @Convert(converter = AccountConverter.class)
    private Account data;

    /**
     * Don't want anyone hitting the default constructor
     */
    private AccountRecord() { }

    public AccountRecord(UUID id, Account data) {
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
     * Returns the data of the record
     * @return the json of the body
     */
    public Account getData() {
        return data;
    }

    /**
     * Sets the data for the record
     * @param data the data object (as an Account object)
     */
    public void setData(Account data) {
        this.data = data;
    }
}