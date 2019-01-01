package com.sheena.playground.plugins;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;

public class RegisterShiftPlugin implements PlaygroundPlugin {
	private String REGISTER_SHIFT_TYPE = "registerShift";
	private String SUCCESS_REGISTER_MESSAGE = "You successfully registered to the shift";


	private ObjectMapper jackson;
	private ActivityDao activities;
	private ElementService elementService;
	private WorkingDay helper;
	private WorkingDayResponse workingDayResponse;

	public RegisterShiftPlugin() {
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
		ElementEntity elementEtity = this.elementService.getElementById(activityEntity.getElementId()); 
		String elementType = elementEtity.getType();
		if (!elementType.equalsIgnoreCase(REGISTER_SHIFT_TYPE)) {
			throw new ElementDoesNotMatchActivityException(
					"activity register shift requires element of type: " + REGISTER_SHIFT_TYPE);
		}

		ShiftDetails shiftDetails = jackson.readValue(
				this.jackson.writeValueAsString(
						elementEtity.getAttributes()), ShiftDetails.class);

		RegisterCancelShiftForm form = jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()),
				RegisterCancelShiftForm.class);

		boolean isSameDate = 
				(this.helper.getDatePart(shiftDetails.getShiftDate()) - this.helper.getDatePart(form.getWantedShiftDate()) == 0);

		if (!isSameDate) {
			throw new RgisterCancelShiftException("Sorry, there is no shift in this date!");
		} else if (shiftDetails.getCurrentWorkersInShift() >= shiftDetails.getMaxWorkersInShift()) {
			throw new RgisterCancelShiftException("Sorry, shift is full!");
		}else {
			shiftDetails.addWorker(activityEntity.getPlayerEmail());

			Map updateAttributes = jackson.readValue(
					this.jackson.writeValueAsString(
							shiftDetails), Map.class);

			elementEtity.setAttributes(updateAttributes);

			this.elementService.updateElement(elementEtity.getId(), elementEtity);
			
			this.workingDayResponse.setMessage(SUCCESS_REGISTER_MESSAGE);
			this.workingDayResponse.setTimeStamp(shiftDetails.getShiftDate());
			this.workingDayResponse.setWorkerEmail(activityEntity.getPlayerEmail());
			this.workingDayResponse.setWorkerPlayground(activityEntity.getPlayerPlayground());

		}
		return this.workingDayResponse;

	}
}
