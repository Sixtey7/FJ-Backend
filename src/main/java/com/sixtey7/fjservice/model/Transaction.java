package com.sixtey7.fjservice.model;

import com.sixtey7.fjservice.model.converter.UUIDAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.time.Instant;
import java.util.UUID;

public class Transaction {

    private UUID transId;
    @JsonbTypeAdapter(UUIDAdapter.class)
    private UUID accountId;
    private String name;
    private Instant date;
    private Float amount;
    private TransType type;
    private String notes;

    public Transaction() { }

    public Transaction(final String name, final Instant date, final float amount, final UUID accountId) {
        this.transId = UUID.randomUUID();
        this.date = date;
        this.amount = amount;
        this.accountId = accountId;

        this.type = TransType.FUTURE;
    }

    public UUID getTransId() {
        return transId;
    }

    public void setTransId(UUID transId) {
        this.transId = transId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public TransType getType() {
        return type;
    }

    public void setType(TransType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Enumeration for the different states a transaction can be in
     */
    public enum TransType {
        PLANNED,
        ESTIMATE,
        PENDING,
        CONFIRMED,
        FUTURE
    };

}
