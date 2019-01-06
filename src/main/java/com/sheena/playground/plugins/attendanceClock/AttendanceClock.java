package com.sheena.playground.plugins.attendanceClock;

import java.util.Date;

public class AttendanceClock {
	private Date workDate;
	
	public AttendanceClock() {
		super();
		this.workDate = new Date();
	}

	public AttendanceClock(Date workDate) {
		super();
		this.workDate = workDate;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	@Override
	public String toString() {
		return "AttendanceClock [workDate=" + workDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((workDate == null) ? 0 : workDate.hashCode());
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
		AttendanceClock other = (AttendanceClock) obj;
		if (workDate == null) {
			if (other.workDate != null)
				return false;
		} else if (!workDate.equals(other.workDate))
			return false;
		return true;
	}
}
