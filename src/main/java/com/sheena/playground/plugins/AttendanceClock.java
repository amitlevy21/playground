package com.sheena.playground.plugins;

import java.util.Date;

public class AttendanceClock {
	private Date currentTime;
	private long elementId;
	
	public AttendanceClock() {
		this.currentTime = new Date();
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}
	
	
}
