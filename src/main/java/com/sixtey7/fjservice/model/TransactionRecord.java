package com.sixtey7.fjservice.model;


import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Helper class to be used to interact with the database
 */
@Entity
@Table(name = "Transactions")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class TransactionRecord {

    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    @Type(type = "jsonb")
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
