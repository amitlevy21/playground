package com.sheena.playground.logic.elements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ElementEntity
 */
@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {

    private String dummyId;
    private String playground;
    private Double x;
    private Double y;
    private String name;
    private Date creationDate;
    private Date expirationDate;
    private String type;
    private Map<String, Object> attributes;
    private String creatorPlayground;
    private String creatorEmail;

    public ElementEntity() {
        this.x = 0.0;
        this.y = 0.0;
        this.creationDate = new Date();
        this.attributes = new HashMap<>();
    }

    public ElementEntity(String name) {
        this();
        this.name = name;
    }

    public String getPlayground() {
        return this.playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    public String getDummyId() {
        return dummyId;
    }

    public void setDummyId(String dummyId) {
        this.dummyId = dummyId;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Id
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Date expirationDate) throws InvalidExpirationDateException {
        if (this.creationDate.after(expirationDate))
            throw new InvalidExpirationDateException();
        this.expirationDate = expirationDate;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Transient
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getCreatorPlayground() {
        return this.creatorPlayground;
    }

    public void setCreatorPlayground(String creatorPlayground) {
        this.creatorPlayground = creatorPlayground;
    }

    public String getCreatorEmail() {
        return this.creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    @Lob
    public String getJsonAttributes() {
        try {
            return new ObjectMapper().writeValueAsString(this.attributes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setJsonAttributes(String jsonAttributes) {
        try {
            this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ElementEntity)) {
            return false;
        }
        ElementEntity elementEntity = (ElementEntity) o;
        return playground.equals(elementEntity.playground) && x.equals(elementEntity.x) && y.equals(elementEntity.y)
                && name.equals(elementEntity.name) && creationDate.equals(elementEntity.creationDate)
                && expirationDate.equals(elementEntity.expirationDate) && type.equals(elementEntity.type)
                && attributes.equals(elementEntity.attributes)
                && creatorPlayground.equals(elementEntity.creatorPlayground)
                && creatorEmail.equals(elementEntity.creatorEmail);
    }


    @Override
    public String toString() {
        return "{" + " dummyId='" + getDummyId() + "'" + ", playground='" + getPlayground() + "'" + ", x='" + getX()
                + "'" + ", y='" + getY() + "'" + ", name='" + getName() + "'" + ", creationDate='" + getCreationDate()
                + "'" + ", expirationDate='" + getExpirationDate() + "'" + ", type='" + getType() + "'"
                + ", attributes='" + getAttributes() + "'" + ", creatorPlayground='" + getCreatorPlayground() + "'"
                + ", creatorEmail='" + getCreatorEmail() + "'" + "}";
    }

}