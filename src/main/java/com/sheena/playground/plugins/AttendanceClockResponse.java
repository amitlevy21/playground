package com.sheena.playground.plugins;

import java.util.Date;

public class AttendanceClockResponse {
	private String message;
	private Date timeStamp;
	private String workerEmail;
	private String workerPlayground;
	
	public AttendanceClockResponse() {
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getWorkerEmail() {
		return workerEmail;
	}

	public void setWorkerEmail(String workerEmail) {
		this.workerEmail = workerEmail;
	}

	public String getWorkerPlayground() {
		return workerPlayground;
	}

	public void setWorkerPlayground(String workerPlayground) {
		this.workerPlayground = workerPlayground;
	}
	
	
}
