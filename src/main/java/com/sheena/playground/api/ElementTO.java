package com.sheena.playground.api;

import java.util.Date;
import java.util.Map;

import com.sheena.playground.logic.entity.ElementEntity;

public class ElementTO {
	
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
	
	public ElementTO(String playground, String id, Location location, String name, Date creationDate,
			Date expirationDate, String type, Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		this.playground = playground;
		this.id = id;
		this.location = location;
		this.name = name;
		setCreationDate(creationDate);
		setExpirationDate(expirationDate);
		this.type = type;
		this.attributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
	}

	public ElementTO(ElementEntity et) {
		this(et.getPlayground(), et.getId(), et.getLocation(), et.getName(), et.getCreationDate(), et.getExpirationDate(),
			et.getType(), et.getAttributes(), et.getCreatorPlayground(), et.getCreatorEmail());
	}

	public ElementTO() {
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getCreatorPlayground() {
		return creatorPlayground;
	}

	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public ElementEntity toEntity() {
		ElementEntity rv = new ElementEntity(this.playground, this.id, this.location, this.name, this.creationDate, this.expirationDate, this.type, this.attributes, this.creatorPlayground, this.creatorEmail);
		return rv;
	}
	
	@Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ElementTO)) {
            return false;
        }
        ElementTO elementTO = (ElementTO) o;
        return playground.equals(elementTO.playground) && id.equals(elementTO.id)
                && location.equals(elementTO.location) && name.equals(elementTO.name)
                && creationDate.equals(elementTO.creationDate)
                && expirationDate.equals(elementTO.expirationDate) && type.equals(elementTO.type)
                && attributes.equals(elementTO.attributes)
                && creatorPlayground.equals(elementTO.creatorPlayground)
                && creatorEmail.equals(elementTO.creatorEmail);
    }
	
}
