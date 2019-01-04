package com.sheena.playground.plugins;

import java.util.Date;
import java.util.Map;

public class ShiftDetails {
	private Date shiftDate;
	private int shiftHours;
	private int maxWorkersInShift;
	private int currentWorkersInShift;
	private Map<String, Object> workers;
	
	public ShiftDetails() {
	}

	public Date getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(Date shiftDate) {
		this.shiftDate = shiftDate;
	}

	public int getShiftHours() {
		return shiftHours;
	}

	public void setShiftHours(int shiftHours) {
		this.shiftHours = shiftHours;
	}

	public int getMaxWorkersInShift() {
		return maxWorkersInShift;
	}

	public void setMaxWorkersInShift(int maxWorkersInShift) {
		this.maxWorkersInShift = maxWorkersInShift;
	}

	public int getCurrentWorkersInShift() {
		return currentWorkersInShift;
	}

	public void setCurrentWorkersInShift(int currentWorkersInShift) {
		this.currentWorkersInShift = currentWorkersInShift;
	}

	public Map<String, Object> getWorkers() {
		return workers;
	}

	public void setWorkers(Map<String, Object> workers) {
		this.workers = workers;
	}
	
	public void addWorker(String playerEmail) {
		this.setCurrentWorkersInShift(this.getCurrentWorkersInShift() + 1);
		this.workers.put("worker #" + getCurrentWorkersInShift(), playerEmail);
	}

	public void removeWorker(String playerEmail) {
		this.workers.values().remove(playerEmail);
		this.setCurrentWorkersInShift(this.getCurrentWorkersInShift() - 1);		
	}
	
}
