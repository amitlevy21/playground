package com.sheena.playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;

@Component
public class CheckOutPlugin implements PlaygroundPlugin {
	private final int MINS_IN_HOUR = 60;
	private final String CHECK_OUT_TYPE = "checkInOut";
	private final String FAIL_CHECK_OUT_MESSAGE = "Your check-out date is invalid!";
	private final String SUCCESS_CHECK_OUT_MESSAGE = "Thank You, Goodbye!";
	
	private ObjectMapper jackson;
	private AttendanceClockResponse attendanceClockResponse;
	private ActivityDao activities;
	private ElementService elementService;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		this.attendanceClockResponse = new AttendanceClockResponse();
	}

	@Autowired
	public void setActivityDao(ActivityDao actvities) {
		this.activities = actvities;
	}
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Override
	public Object invokeOperation(ActivityEntity activityEntity) throws Exception {
		ElementEntity elementEtity = this.elementService.getElementById(activityEntity.getElementId()); 
		String elementType = elementEtity.getType();
		if (!elementType.equalsIgnoreCase(CHECK_OUT_TYPE)) {
			throw new ElementDoesNotMatchActivityException(
					"activity CheckOut requires element of type: " + CHECK_OUT_TYPE);
		}
		AttendanceClock attendanceClock = jackson.readValue(
				this.jackson.writeValueAsString(
						elementEtity.getAttributes()), AttendanceClock.class);
		
		CheckInOutForm form = jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()),
						CheckInOutForm.class);
		
		// getTime() returns the number of milliseconds since January 1, 1970, 00:00:00
		// GMT represented by this Date object
		long diff = 
				attendanceClock.getServerCurrentDate().getTime() - form.getCurrentDate().getTime();

		int diffmin = (int) (diff / (60 * 1000));
		
		if (diffmin < 0 || diffmin > MINS_IN_HOUR) {
			throw new CheckInOutInvalidDateException(this.FAIL_CHECK_OUT_MESSAGE);
		}
		
		this.attendanceClockResponse.setMessage(SUCCESS_CHECK_OUT_MESSAGE);
		this.attendanceClockResponse.setTimeStamp(attendanceClock.getServerCurrentDate());
		this.attendanceClockResponse.setWorkerEmail(activityEntity.getPlayerEmail());
		this.attendanceClockResponse.setWorkerPlayground(activityEntity.getPlayerPlayground());
		
		return this.attendanceClockResponse;

	}

}