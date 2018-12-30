package com.sheena.playground.plugins;

import java.util.Date;

public class WorkingDay {
	private final int maxWorkingHours = 8;
	private final int maxWorkers = 10;
	
	private Date workingDate;
	private int numOfWorkers;
	private long elementId;
	
	public WorkingDay() {
		this.numOfWorkers = 0;
	}
	
	public Date getWorkingDate() {
		return workingDate;
	}

	public void setWorkingDate(Date workingDate) {
		this.workingDate = workingDate;
	}

	public int getNumOfWorkers() {
		return numOfWorkers;
	}

	public void setNumOfWorkers(int numOfWorkers) {
		this.numOfWorkers = numOfWorkers;
	}

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public int getMaxWorkingHours() {
		return maxWorkingHours;
	}

	public int getMaxWorkers() {
		return maxWorkers;
	}
	
}
