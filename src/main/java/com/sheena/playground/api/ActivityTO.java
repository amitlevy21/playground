package com.sheena.playground.api;

import java.util.Map;

import com.sheena.playground.logic.activities.ActivityEntity;

public class ActivityTO {

	private String id;
	private String playground;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String, Object> attributes;

	public ActivityTO() {
	}

	public ActivityTO(String elementPlayground, String elementId, String type, String playerPlayground,
			String playerEmail, Map<String, Object> attributes) {
		super();
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerPlayground = playerPlayground;
		this.playerEmail = playerEmail;
		this.attributes = attributes;
	}

	public ActivityTO(ActivityEntity entity) {
		this(entity.getElementPlayground(), entity.getElementId(), entity.getType(), entity.getPlayerPlayground(),
				entity.getPlayerEmail(), entity.getAttributes());
		setId(entity.getId());
		setPlayground(entity.getPlayground());
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

	public ActivityEntity toEntity() {
		ActivityEntity entity = new ActivityEntity(this.elementPlayground, this.elementId, this.type,
				this.playerPlayground, this.playerEmail, this.attributes);
		entity.setId(this.id);
		entity.setPlayground(this.playground);
		return entity;
	}

	@Override
	public String toString() {
		return "ActivityTO [playground=" + playground + ", id=" + id + ", elementPlayground=" + elementPlayground
				+ ", elementId=" + elementId + ", type=" + type + ", playerPlayground=" + playerPlayground
				+ ", playerEmail=" + playerEmail + ", attributes=" + attributes + "]";
	}
}
