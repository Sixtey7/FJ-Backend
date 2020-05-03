package com.sixtey7.fjservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sixtey7.fjservice.model.converter.LocalDateDeserializer;
import com.sixtey7.fjservice.model.converter.LocalDateSerializer;
import org.eclipse.yasson.internal.serializer.LocalDateTypeDeserializer;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="transactions")
public class Transaction implements Comparable<Transaction> {
    @Id
    @NotNull
    @Column(name="id")
    private UUID id;

    @NotNull
    @Column(name="account_id")
    private UUID accountId;

    @NotNull
    @Column(name="name")
    private String name;

    @Column(name="date")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    @Column(name="amount")
    private Float amount;

    @Column(name="type")
    private TransType type;

    @Column(name="notes")
    private String notes;

    public Transaction() { }

    public Transaction(final String name, final LocalDate date, final float amount, final UUID accountId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.accountId = accountId;

        this.type = TransType.FUTURE;
    }

    public Transaction(final String name, final LocalDate date, final float amount, final UUID accountId, final String notes) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.accountId = accountId;
        this.notes = notes;

        this.type = TransType.FUTURE;
    }

    public Transaction(final String name, final LocalDate date, final float amount, final UUID accountId, final String notes, TransType type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.accountId = accountId;
        this.notes = notes;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /*@Transient
    @JsonIgnore
    public LocalDate getDateAsLocalDT() {
        return LocalDate.parse(date);
    }
    */

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

    @Override
    public int compareTo(Transaction o) {
        return date.compareTo(o.getDate());
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

    /**
     * Override the default toString to provide a cleaner log message
     * @return {@link String} for the {@link Transaction}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //Trans Id
        sb.append("Trans Id: ");
        sb.append(this.id.toString());
        sb.append("\n");

        //Account Id
        sb.append("Account Id: ");
        sb.append(this.accountId.toString());
        sb.append("\n");

        //Name
        sb.append("Name: ");
        sb.append(this.name);
        sb.append("\n");

        //Date
        sb.append("Date: ");
        sb.append(this.date);
        sb.append("\n");

        //Amount
        sb.append("Amount: ");
        sb.append(this.amount);
        sb.append("\n");

        //Type
        sb.append("Type: ");
        sb.append(this.type.toString());
        sb.append("\n");

        //Notes
        sb.append("Notes: ");
        sb.append(this.notes);

        return sb.toString();
    }

}
