package com.sheena.playground.plugins.attendanceClock;

import java.util.Date;

public class ClockingForm {
	private Date clockingDate;

	public ClockingForm() {
		super();
	}

	public Date getClockingDate() {
		return clockingDate;
	}

	public void setClockingDate(Date clockingDate) {
		this.clockingDate = clockingDate;
	}

	@Override
	public String toString() {
		return "ClockingForm [clockingDate=" + clockingDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clockingDate == null) ? 0 : clockingDate.hashCode());
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
		ClockingForm other = (ClockingForm) obj;
		if (clockingDate == null) {
			if (other.clockingDate != null)
				return false;
		} else if (!clockingDate.equals(other.clockingDate))
			return false;
		return true;
	}
}
