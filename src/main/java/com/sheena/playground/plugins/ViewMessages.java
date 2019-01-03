package com.sheena.playground.plugins;

import java.util.List;

public class ViewMessages {
	
	private List<String> messages;
	
	public ViewMessages() {
	}
		
	public ViewMessages(List<String> messages) {
		super();
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
