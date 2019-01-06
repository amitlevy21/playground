package com.sheena.playground.plugins.attendanceClock;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.plugins.ElementDoesNotMatchActivityException;
import com.sheena.playground.plugins.PlaygroundPlugin;

@Component
public class ClockPlugin implements PlaygroundPlugin {
	private final String ATTENDANCE_CLOCK_TYPE = "attendanceClock";

	private ObjectMapper jackson;
	private ElementService elementService;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Override
	public Object invokeOperation(ActivityEntity activityEntity) throws Exception {
		ElementEntity elementEntity = this.elementService.getElementById(activityEntity.getElementId()); 
		
		String elementType = elementEntity.getType();
		
		if (!elementType.equalsIgnoreCase(ATTENDANCE_CLOCK_TYPE))
			throw new ElementDoesNotMatchActivityException(
					"activity requires element of type: " + ATTENDANCE_CLOCK_TYPE);
		
		AttendanceClock attendanceClock = jackson.readValue(
				this.jackson.writeValueAsString(
						elementEntity.getAttributes()), AttendanceClock.class);
		
		ClockingForm form = jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()),
						ClockingForm.class);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(!dateFormat.format(attendanceClock.getWorkDate()).equals(dateFormat.format(form.getCurrentDate())))
			throw new ClockingDateMismatchException("Cannot clock date: " + form.getCurrentDate() + " to another work date");
		
		return new AttendanceClockResponse(form.getCurrentDate(), activityEntity.getPlayerEmail(), activityEntity.getPlayerPlayground());
	}

}
