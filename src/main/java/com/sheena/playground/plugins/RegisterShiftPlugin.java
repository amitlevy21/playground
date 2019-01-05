package com.sheena.playground.plugins;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;

@Component
public class RegisterShiftPlugin implements PlaygroundPlugin {
	private String SHIFT_TYPE = "Shift";
	private String SUCCESS_REGISTER_MESSAGE = "You successfully registered to the shift";

	private ObjectMapper jackson;
	private ActivityDao activities;
	private ElementService elementService;
	private WorkingDay helper;
	private WorkingDayResponse workingDayResponse;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		this.helper = new WorkingDay();
		this.workingDayResponse = new WorkingDayResponse();
	}

	@Autowired
	public void setActivities(ActivityDao activities) {
		this.activities = activities;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Override
	public Object invokeOperation(ActivityEntity activityEntity) throws Exception {
		ElementEntity elementEtity =
				this.elementService.getElementById(activityEntity.getElementId());
		String elementType = elementEtity.getType();
		if (!elementType.equalsIgnoreCase(SHIFT_TYPE)) {
			throw new ElementDoesNotMatchActivityException(
					"activity register shift requires element of type: " + SHIFT_TYPE);
		}

		RegisterCancelShiftForm form = jackson.readValue(
				this.jackson.writeValueAsString(activityEntity.getAttributes()), RegisterCancelShiftForm.class);

		ShiftDetails shiftDetails = jackson.readValue(this.jackson.writeValueAsString(elementEtity.getAttributes()),
				ShiftDetails.class);

		boolean isSameDate = (this.helper.getDatePart(shiftDetails.getShiftDate())
				- this.helper.getDatePart(form.getWantedShiftDate()) == 0);

		if (!isSameDate) {
			throw new RegisterCancelShiftException("Sorry, there is no shift in this date!");
		}

		if (shiftDetails.getCurrentWorkersInShift() >= shiftDetails.getMaxWorkersInShift()) {
			throw new RegisterCancelShiftException("Sorry, shift is full!");
		}
		shiftDetails.addWorker(activityEntity.getPlayerEmail());

		Map updateAttributes = jackson.readValue(this.jackson.writeValueAsString(shiftDetails), Map.class);

		elementEtity.setAttributes(updateAttributes);

		this.elementService.updateElement(activityEntity.getPlayerEmail(), elementEtity.getId(), elementEtity);

		this.workingDayResponse.setMessage(SUCCESS_REGISTER_MESSAGE);
		this.workingDayResponse.setTimeStamp(shiftDetails.getShiftDate());
		this.workingDayResponse.setWorkerEmail(activityEntity.getPlayerEmail());
		this.workingDayResponse.setWorkerPlayground(activityEntity.getPlayerPlayground());

		return this.workingDayResponse;

	}
}
