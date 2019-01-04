package com.sheena.playground.plugins;

import java.util.Calendar;
import java.util.Date;

public class WorkingDay {

	public WorkingDay() {
	}
	
	public long getDatePart(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);

	    return cal.getTimeInMillis();
	}
}
