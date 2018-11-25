package com.sheena.playground.logic.elements;

import java.util.Date;
import java.util.Map;

import com.sheena.playground.api.Location;

/**
 * ElementEntity
 */
public class ElementEntity {

    private String playground;
    private String id;
    private Location location;
    private String name;
    private Date creationDate;
    private Date expirationDate;
    private String type;
    private Map<String, Object> attributes;
    private String creatorPlayground;
    private String creatorEmail;

    public ElementEntity(String playground, String id, Location location, String name, Date creationDate,
            Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
            String creatorEmail) {
        this.playground = playground;
        this.id = id;
        this.location = location;
        this.name = name;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.type = type;
        this.attributes = attributes;
        this.creatorPlayground = creatorPlayground;
        this.creatorEmail = creatorEmail;
    }

    protected ElementEntity() {
    }

    public String getPlayground() {
        return this.playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAtrributes(Map<String, Object> attributes) {
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
        return playground.equals(elementEntity.playground) && id.equals(elementEntity.id)
                && location.equals(elementEntity.location) && name.equals(elementEntity.name)
                && creationDate.equals(elementEntity.creationDate)
                && expirationDate.equals(elementEntity.expirationDate) && type.equals(elementEntity.type)
                && attributes.equals(elementEntity.attributes)
                && creatorPlayground.equals(elementEntity.creatorPlayground)
                && creatorEmail.equals(elementEntity.creatorEmail);
    }

    @Override
    public String toString() {
        return "{" + " playground='" + getPlayground() + "'" + ", id='" + getId() + "'" + ", location='" + getLocation()
                + "'" + ", name='" + getName() + "'" + ", creationDate='" + getCreationDate() + "'"
                + ", expirationDate='" + getExpirationDate() + "'" + ", type='" + getType() + "'" + ", attributes='"
                + getAttributes() + "'" + ", creatorPlayground='" + getCreatorPlayground() + "'" + ", creatorEmail='"
                + getCreatorEmail() + "'" + "}";
    }

}