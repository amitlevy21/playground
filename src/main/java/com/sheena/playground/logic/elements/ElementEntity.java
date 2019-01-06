package com.sheena.playground.logic.elements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="elements")
public class ElementEntity {

    private String id;
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
    	super();
        this.x = 0.0;
        this.y = 0.0;
        this.creationDate = new Date();
        this.attributes = new HashMap<>();
    }

	public ElementEntity(String playground, Double x, Double y, String name, Date creationDate, Date expirationDate,
			String type, Map<String, Object> attributes, String creatorPlayground, String creatorEmail) {
		super();
		this.playground = playground;
		this.x = x;
		this.y = y;
		this.name = name;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.attributes = attributes;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
	}

	public ElementEntity(ElementEntity other) {
		super();
		setPlayground(other.getPlayground());
		setAttributes(other.getAttributes());
		setCreationDate(other.getCreationDate());
		setCreatorEmail(other.getCreatorEmail());
		setCreatorPlayground(other.getCreatorPlayground());
		setExpirationDate(other.getExpirationDate());
		setId(other.getId());
		setName(other.getName());
		setType(other.getType());
		setX(other.getX());
		setY(other.getY());
	}
	
	public String getPlayground() {
        return this.playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }
    
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
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
    
//    @Override
//    public String toString() {
//        return "{" + " dummyId='" + getId() + "'" + ", playground='" + getPlayground() + "'" + ", x='" + getX()
//                + "'" + ", y='" + getY() + "'" + ", name='" + getName() + "'" + ", creationDate='" + getCreationDate()
//                + "'" + ", expirationDate='" + getExpirationDate() + "'" + ", type='" + getType() + "'"
//                + ", attributes='" + getAttributes() + "'" + ", creatorPlayground='" + getCreatorPlayground() + "'"
//                + ", creatorEmail='" + getCreatorEmail() + "'" + "}";
//    }
    
    @Override
	public String toString() {
		return "ElementEntity [id=" + id + ", playground=" + playground + ", x=" + x + ", y=" + y + ", name=" + name
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		ElementEntity other = (ElementEntity) obj;
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
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
}