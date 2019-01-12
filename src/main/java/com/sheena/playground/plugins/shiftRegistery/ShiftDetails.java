package com.sheena.playground.plugins.shiftRegistery;

import java.util.Date;

public class ShiftDetails {
	
	private Date shiftDate;
	private int maxWorkersInShift;
	
	
	public ShiftDetails() {
	}

	public Date getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Date shiftDate) {
		this.shiftDate = shiftDate;
	}

	public int getMaxWorkersInShift() {
		return maxWorkersInShift;
	}

	public void setMaxWorkersInShift(int maxWorkersInShift) {
		this.maxWorkersInShift = maxWorkersInShift;
	}

}
