package com.sheena.playground.logic.elements;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * ElementEntity
 */
@Document(collection="elements")
public class ElementEntity {

    private String dummyId;
    private String playground;
    private Double x;
    private Double y;
    private String name;
    private Calendar creationDate;
    private Calendar expirationDate;
    private String type;
    private Map<String, Object> attributes;
    private String creatorPlayground;
    private String creatorEmail;

    public ElementEntity() {
        this.x = 0.0;
        this.y = 0.0;
        this.creationDate = GregorianCalendar.getInstance();
        this.creationDate.set(1970, 1, 1);
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

    public Calendar getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) throws InvalidExpirationDateException {
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ElementEntity)) {
            return false;
        }
        ElementEntity elementEntity = (ElementEntity) o;
        return playground.equals(elementEntity.playground) && x.equals(elementEntity.x) && y.equals(elementEntity.y)
                && name.equals(elementEntity.name) && creationDate.getTimeZone().equals(elementEntity.creationDate.getTimeZone())
                && creationDate.get(Calendar.YEAR) == elementEntity.creationDate.get(Calendar.YEAR)
				&& creationDate.get(Calendar.MONTH) == elementEntity.creationDate.get(Calendar.MONTH)
				&& creationDate.get(Calendar.DAY_OF_MONTH) == elementEntity.creationDate.get(Calendar.DAY_OF_MONTH)
                && type.equals(elementEntity.type)
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