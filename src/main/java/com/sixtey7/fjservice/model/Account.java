package com.sixtey7.fjservice.model;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

/**
 * Class representing an account that may contain one or more transactions
 */
public class Account {

    /**
     * the id assigned to the account
     */
    private UUID id;

    /**
     * The name of the accoubnt
     */
    private String name;

    /**
     * The current balance of the account
     */
    private Float amount;

    /**
     * Notes that are tied to the account
     */
    private String notes;

    /**
     * Flag that indicates if this account's amount is calculated based on transactions or set by the user
     */
    private Boolean dynamic;

    /**
     * Masking the default constructor because we don't want the required fields to not be set
     */
    private Account() { }

    public Account(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }
}