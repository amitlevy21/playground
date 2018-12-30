package com.sheena.playground.plugins;

public class BoardMessage {

	private String text;
	private String publisherEmail;
	private String publisherPlayground;
	
	public BoardMessage() {
	}
	
	public BoardMessage(String text) {
		super();
		this.text = text;
	}
	
	public BoardMessage(String text, String publisherEmail, String publisherPlayground) {
		super();
		this.text = text;
		this.publisherEmail = publisherEmail;
		this.publisherPlayground = publisherPlayground;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPublisherEmail() {
		return publisherEmail;
	}

	public void setPublisherEmail(String publisherEmail) {
		this.publisherEmail = publisherEmail;
	}

	public String getPublisherPlayground() {
		return publisherPlayground;
	}

	public void setPublisherPlayground(String publisherPlayground) {
		this.publisherPlayground = publisherPlayground;
	}

	@Override
	public String toString() {
		return "BoardMessage [text=" + text + ", publisherEmail=" + publisherEmail + ", publisherPlayground="
				+ publisherPlayground + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((publisherEmail == null) ? 0 : publisherEmail.hashCode());
		result = prime * result + ((publisherPlayground == null) ? 0 : publisherPlayground.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		BoardMessage other = (BoardMessage) obj;
		if (publisherEmail == null) {
			if (other.publisherEmail != null)
				return false;
		} else if (!publisherEmail.equals(other.publisherEmail))
			return false;
		if (publisherPlayground == null) {
			if (other.publisherPlayground != null)
				return false;
		} else if (!publisherPlayground.equals(other.publisherPlayground))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
}
