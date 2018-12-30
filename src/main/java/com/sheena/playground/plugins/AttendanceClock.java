package com.sheena.playground.plugins;

import java.util.Date;

public class AttendanceClock {
	private Date serverCurrentDate;
	
	public AttendanceClock() {
		this.serverCurrentDate = new Date();
	}

	public Date getServerCurrentDate() {
		return serverCurrentDate;
	}

	public void setServerCurrentDate(Date serverCurrentDate) {
		this.serverCurrentDate = serverCurrentDate;
	}
	
}
