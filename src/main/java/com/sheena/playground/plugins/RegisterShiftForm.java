package com.sheena.playground.plugins;

import java.util.Date;

public class RegisterShiftForm {
	private Date shiftDate;
	private int hours;
	
	public RegisterShiftForm() {
	}

	public Date getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Date shiftDate) {
		this.shiftDate = shiftDate;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}
	
}
