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
@Table(name = "Accounts")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class AccountRecord {

    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "data", columnDefinition = "jsonb")
    @Type(type = "jsonb")
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