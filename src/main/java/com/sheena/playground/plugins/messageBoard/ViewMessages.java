package com.sheena.playground.plugins.messageBoard;

import java.util.List;

import com.sheena.playground.api.ActivityTO;

public class ViewMessages {
	
	private List<ActivityTO> messages;
	
	public ViewMessages() {
	}
		
	public ViewMessages(List<ActivityTO> messages) {
		super();
		this.messages = messages;
	}

	public List<ActivityTO> getMessages() {
		return messages;
	}

	public void setMessages(List<ActivityTO> messages) {
		this.messages = messages;
	}

	

}
