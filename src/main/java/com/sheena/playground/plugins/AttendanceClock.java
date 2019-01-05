package com.sheena.playground.plugins;

import java.util.Date;

public class AttendanceClock {
	private Date serverCurrentDate;
	
	public AttendanceClock() {
	}

	public Date getServerCurrentDate() {
		return serverCurrentDate;
	}

	public void setServerCurrentDate(Date serverCurrentDate) {
		this.serverCurrentDate = serverCurrentDate;
	}
	
}
