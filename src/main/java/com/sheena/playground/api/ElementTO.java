package com.sheena.playground.api;

import java.util.Date;
import java.util.Map;

import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;

public class ElementTO {
	
	private String playground;
	private Location location;
	private String name;
	private String id;
	private Date creationDate;
    private Date expirationDate;
    private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;

	public ElementTO() {
	}

	public ElementTO(Location location, String name, Date creationDate, Date expirationDate, String type,
		Map<String, Object> attributes, String creatorPlayground, String creatorEmail) {
	super();
	this.location = location;
	this.name = name;
	this.creationDate = creationDate;
	this.expirationDate = expirationDate;
	this.type = type;
	this.attributes = attributes;
	this.creatorPlayground = creatorPlayground;
	this.creatorEmail = creatorEmail;
	}
	
	public ElementTO(ElementEntity entity) {
		this.attributes = entity.getAttributes();
		this.creationDate = entity.getCreationDate();
		this.creatorEmail = entity.getCreatorEmail();
		this.creatorPlayground = entity.getCreatorPlayground();
		this.expirationDate = entity.getExpirationDate();
		this.location = new Location(entity.getX(), entity.getY());
		this.name = entity.getName();
		this.playground = entity.getPlayground();
		this.type = entity.getType();
		this.id = entity.getId();
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public ElementEntity toEntity() throws InvalidExpirationDateException {
		ElementEntity rv = new ElementEntity();
		
		if(this.id != null) {
			rv.setId(this.id);
		}
		
		rv.setPlayground(this.playground);
		rv.setX(this.location.getX());
		rv.setY(this.location.getY());
		rv.setName(this.name);
		rv.setCreationDate(this.creationDate);
		rv.setExpirationDate(this.expirationDate);
		rv.setType(this.type);
		rv.setAttributes(this.attributes);
		rv.setCreatorPlayground(this.creatorPlayground);
		rv.setCreatorEmail(this.creatorEmail);
		return rv;
	}
	
	

	@Override
	public String toString() {
		return "ElementTO [playground=" + playground + ", location=" + location + ", name=" + name + ", id=" + id
				+ ", creationDate=" + creationDate + ", expirationDate=" + expirationDate + ", type=" + type
				+ ", attributes=" + attributes + ", creatorPlayground=" + creatorPlayground + ", creatorEmail="
				+ creatorEmail + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((creatorEmail == null) ? 0 : creatorEmail.hashCode());
		result = prime * result + ((creatorPlayground == null) ? 0 : creatorPlayground.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementTO other = (ElementTO) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (creatorEmail == null) {
			if (other.creatorEmail != null)
				return false;
		} else if (!creatorEmail.equals(other.creatorEmail))
			return false;
		if (creatorPlayground == null) {
			if (other.creatorPlayground != null)
				return false;
		} else if (!creatorPlayground.equals(other.creatorPlayground))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
