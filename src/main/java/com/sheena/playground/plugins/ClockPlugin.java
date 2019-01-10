package com.sheena.playground.plugins;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.plugins.attendanceClock.AttendanceClock;
import com.sheena.playground.plugins.attendanceClock.AttendanceClockResponse;
import com.sheena.playground.plugins.attendanceClock.ClockingDateMismatchException;
import com.sheena.playground.plugins.attendanceClock.ClockingForm;

@Component
public class ClockPlugin implements PlaygroundPlugin {
	public static final String ATTENDANCE_CLOCK_ELEMENT_TYPE = "attendanceClock";
	public static final String CLOCK_ACTIVITY_TYPE = "Clock";
	
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
		
		if (!elementType.equalsIgnoreCase(ATTENDANCE_CLOCK_ELEMENT_TYPE))
			throw new ElementDoesNotMatchActivityException(
					"activity requires element of type: " + ATTENDANCE_CLOCK_ELEMENT_TYPE);
		
		AttendanceClock attendanceClock = jackson.readValue(
				this.jackson.writeValueAsString(
						elementEntity.getAttributes()), AttendanceClock.class);
		
		ClockingForm form = jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()),
						ClockingForm.class);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(!dateFormat.format(attendanceClock.getWorkDate()).equals(dateFormat.format(form.getClockingDate())))
			throw new ClockingDateMismatchException("Cannot clock date: " + form.getClockingDate() + " to another work date");
		
		return new AttendanceClockResponse(form.getClockingDate(), activityEntity.getPlayerEmail(), activityEntity.getPlayerPlayground());
	}

}
