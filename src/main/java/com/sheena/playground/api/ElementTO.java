package com.sheena.playground.api;

import java.util.Calendar;
import java.util.Map;

import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;

public class ElementTO {
	
	private String playground;
	private Location location;
	private String name;
	private Calendar creationDate;
	private Calendar expirationDate;
	private String type;
	private Map<String, Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	
	
	public ElementTO(String playground, Location location, String name, Calendar creationDate,
			Calendar expireationDate, String type, Map<String, Object> attributes, String creatorPlayground,
			String creatorEmail) {
		this.playground = playground;
		this.location = location;
		this.name = name;
		setCreationDate(creationDate);
		setExpirationDate(expireationDate);
		this.type = type;
		this.attributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
	}

	public ElementTO(ElementEntity et) {
		this(et.getPlayground(), new Location(et.getX(), et.getY()), et.getName(), et.getCreationDate(), et.getExpirationDate(),
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

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
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

	public ElementEntity toEntity() throws InvalidExpirationDateException {
		ElementEntity rv = new ElementEntity();
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
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ElementTO)) {
            return false;
        }
        ElementTO elementTO = (ElementTO) o;
        return playground.equals(elementTO.playground)
                && location.equals(elementTO.location) && name.equals(elementTO.name)
				&& creationDate.get(Calendar.YEAR) == elementTO.creationDate.get(Calendar.YEAR)
				&& creationDate.get(Calendar.MONTH) == elementTO.creationDate.get(Calendar.MONTH)
				&& creationDate.get(Calendar.DAY_OF_MONTH) == elementTO.creationDate.get(Calendar.DAY_OF_MONTH)
                && type.equals(elementTO.type)
                && attributes.equals(elementTO.attributes)
                && creatorPlayground.equals(elementTO.creatorPlayground)
                && creatorEmail.equals(elementTO.creatorEmail);
    }
	
}
