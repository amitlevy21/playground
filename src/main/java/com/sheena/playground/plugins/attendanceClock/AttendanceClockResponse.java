package com.sheena.playground.plugins.attendanceClock;

import java.util.Date;

public class AttendanceClockResponse {
	
	private Date timeStamp;
	private String workerEmail;
	private String workerPlayground;
	
	public AttendanceClockResponse() {
	}
	
	public AttendanceClockResponse(Date timeStamp, String workerEmail, String workerPlayground) {
		super();
		this.timeStamp = timeStamp;
		this.workerEmail = workerEmail;
		this.workerPlayground = workerPlayground;
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

	@Override
	public String toString() {
		return "AttendanceClockResponse [timeStamp=" + timeStamp + ", workerEmail=" + workerEmail
				+ ", workerPlayground=" + workerPlayground + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		result = prime * result + ((workerEmail == null) ? 0 : workerEmail.hashCode());
		result = prime * result + ((workerPlayground == null) ? 0 : workerPlayground.hashCode());
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
		AttendanceClockResponse other = (AttendanceClockResponse) obj;
		if (timeStamp == null) {
			if (other.timeStamp != null)
				return false;
		} else if (!timeStamp.equals(other.timeStamp))
			return false;
		if (workerEmail == null) {
			if (other.workerEmail != null)
				return false;
		} else if (!workerEmail.equals(other.workerEmail))
			return false;
		if (workerPlayground == null) {
			if (other.workerPlayground != null)
				return false;
		} else if (!workerPlayground.equals(other.workerPlayground))
			return false;
		return true;
	}
}
