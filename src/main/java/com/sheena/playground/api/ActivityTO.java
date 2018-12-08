package com.sheena.playground.api;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;

public class ActivityTO {
	
	final AtomicLong orderIdGenerator = new AtomicLong(0);
	
	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String, Object> attributes;
	
	public ActivityTO() {
		this.id = String.valueOf(orderIdGenerator.incrementAndGet());
	}
	
	public ActivityTO(String playground, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes) {
		super();
		this.playground = playground;
		this.id = String.valueOf(orderIdGenerator.incrementAndGet());
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerPlayground = playerPlayground;
		this.playerEmail = playerEmail;
		this.attributes = attributes;
	}

	public ActivityTO(ActivityEntity entity) throws ActivityNotFoundException {
		this(entity.getPlayground(), 
				entity.getElementPlayground(),
				entity.getElementId(),
				entity.getType(),
				entity.getPlayerPlayground(),
				entity.getPlayerEmail(),
				entity.getAttributes());
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

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public ActivityEntity toActivityEntity() {
		ActivityEntity entity = new ActivityEntity(
				this.playground,
				this.id,
				this.elementPlayground,
				this.elementId,
				this.type,
				this.playerPlayground,
				this.playerEmail,
				this.attributes);
		return entity;
	}

	@Override
	public String toString() {
		return "ActivityTO [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";
	}
}
