package com.sixtey7.fjservice.model;

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
    public Account() { }

    public Account(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public Account(String name, float amount, String notes, boolean dynamic) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.amount = amount;
        this.notes = notes;
        this.dynamic = dynamic;
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

    /**
     * Override the default toString to provide a cleaner log message
     * @return {@link String} for the {@link Account}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //Account Id
        sb.append("Account Id: ");
        sb.append(this.id.toString());
        sb.append("\n");

        //Name
        sb.append("Name: ");
        sb.append(this.name);
        sb.append("\n");

        //Amount
        sb.append("Amount: ");
        sb.append(this.amount);
        sb.append("\n");

        //Dynamic
        sb.append("Dynamic: ");
        sb.append(this.dynamic);
        sb.append("\n");

        //Notes
        sb.append("Notes: ");
        sb.append(this.notes);

        return sb.toString();
    }
}