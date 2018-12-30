package com.sheena.playground.plugins;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;

@Component
public class CheckInPlugin implements Plugin {
	private final int minInHour = 60;

	private ObjectMapper jackson;
	private AttendanceClock attendanceClock;
	private ActivityDao activities;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		this.attendanceClock = new AttendanceClock();
	}

	@Autowired
	public void setActivities(ActivityDao actvities) {
		this.activities = actvities;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		boolean isValidDate = false;
		String rvMessage = "";
		StartWorkingForm startForm = jackson
				.readValue(this.jackson.writeValueAsString(command.getAttributes()),StartWorkingForm.class);
//		Map playerStartDate 
//			= jackson.readValue(command.getAttributes(), Map.class);

		// getTime() returns the number of milliseconds since January 1, 1970, 00:00:00
		// GMT represented by this Date object
//		long diff = this.attendanceClock.getCurrentTime().getTime() - command.getStart().getTime();
//
//		// if diff < 0 --> playerStartDate is at the near future
//		// if diff = 0 --> playerStartDate is right now
//		// if diff > 0 --> playerStartDate is in the past so we limit it to 1 hour (60 min.)
//		int diffmin = (int) (diff / (60 * 1000));
//
//		if (diffmin < 0) {
//			rvMessage = "Your start date is in the future - NOT VALID!";
//		} else if (diffmin > minInHour) {
//			rvMessage = "It's has been passed more than hour since your start date - NOT VALID!";
//		} else {
//			rvMessage = "Welcome, have a nice day!";
//			isValidDate = true;
//		}
//
//		command.getAttributes().put("validDate", isValidDate);

		return new PlayerActivityResponse(rvMessage);
	}

}
