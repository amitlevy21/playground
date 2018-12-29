package com.sheena.playground.plugins;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;


public class RegisterShiftPlugin implements Plugin {
	private final int minTimeToRegiser = 2;
	private final String type = "RegisterShift";

	private ObjectMapper jackson;
	private WorkingDay workingDay;
	private ActivityDao activities;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		this.workingDay = new WorkingDay();
	}

	@Autowired
	public void setActivities(ActivityDao actvities) {
		this.activities = actvities;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		boolean isSuccesRegister = false;
		String rvMessage = "";

		/*RegisterShiftForm shiftReq =
				jackson.readValue(command.getJsonAttributes(), RegisterShiftForm.class);
		
		// setting the date of working day
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date dateWithoutTime = sdf.parse(sdf.format(shiftReq.getShiftDate()));
//		this.workingDay.setWorkingDate(dateWithoutTime);
		
		this.activities.getActivitiesByType(rvMessage, size, page)
		
		if (shiftReq.getHours() > this.workingDay.getMaxWorkingHours()) {
			rvMessage = "Max. hours for working day is: " 
						+ this.workingDay.getMaxWorkingHours()
						+ "- NOT VALID!";
		} else if (this.workingDay.getNumOfWorkers() >= this.workingDay.getMaxWorkers()) {
			rvMessage = "Shift is FULL - NOT VALID!";
		} else {
			rvMessage = "You are successfully register to the shift";
			isSuccesRegister = true;
			this.workingDay.setNumOfWorkers(this.workingDay.getNumOfWorkers() + 1);
		}

		// we can add here if condtion on if we want to save a worng check-In request
		// use isValidDate therefore.

		command.getAttributes().put("register", isSuccesRegister);
		this.activities.save(command);*/

		return new PlayerActivityResponse(rvMessage);
	}

	
}
