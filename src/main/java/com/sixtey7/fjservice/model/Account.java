package com.sixtey7.fjservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Entity class used for account objects
 */
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @NotNull
    @Column(name="id")
    private UUID id;

    @NotNull
    @Column(name="name")
    private String name;

    @Column(name="amount")
    private Float amount;

    @Column(name="notes")
    private String notes;

    @Column(name="dynamic")
    private Boolean dynamic;

    /**
     * Default constructor for hibernate
     */
    public Account() {}

    /**
     * Constructor
     */
    public Account(String name, float amount, String notes, boolean dynamic) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.amount = amount;
        this.notes = notes;
        this.dynamic = dynamic;
    }

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
