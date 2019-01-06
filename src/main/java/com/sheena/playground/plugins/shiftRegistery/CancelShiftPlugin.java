package com.sheena.playground.plugins.shiftRegistery;

import java.text.SimpleDateFormat;
import java.util.Map;

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
public class CancelShiftPlugin implements PlaygroundPlugin{
	private final String SHIFT_ELEMENT_TYPE = "shift";

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
		ElementEntity elementEtity =
				this.elementService.getElementById(activityEntity.getElementId());
		
		String elementType = elementEtity.getType();
		
		if (!elementType.equalsIgnoreCase(SHIFT_ELEMENT_TYPE)) {
			throw new ElementDoesNotMatchActivityException(
					"activity requires element of type: " + SHIFT_ELEMENT_TYPE);
		}

		ShiftDetails shiftDetails = jackson.readValue(
				this.jackson.writeValueAsString(elementEtity.getAttributes()),
				ShiftDetails.class);
		
		ShiftForm form = jackson.readValue(
				this.jackson.writeValueAsString(activityEntity.getAttributes()),
				ShiftForm.class);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(!dateFormat.format(shiftDetails.getShiftDate()).equals(dateFormat.format(form.getWantedShiftDate())))
			throw new ShiftRegisteryDateMismatchException("cannot cancel shift in date " + form.getWantedShiftDate() + ". no such shift");
		
		boolean isUserRegistered = 
				shiftDetails.getWorkers().contains(activityEntity.getPlayerEmail());
		
		if (!isUserRegistered)
			throw new InvalidShiftCancelationException("cannot cancel shift: not registered to this shift");
		
		shiftDetails.removeWorker(activityEntity.getPlayerEmail());

		Map<String, Object> updateAttributes = 
				jackson.readValue(this.jackson.writeValueAsString(shiftDetails), Map.class);

		elementEtity.setAttributes(updateAttributes);

		this.elementService.updateElement(activityEntity.getPlayerEmail(), elementEtity.getId(), elementEtity);
		
		return new ShiftResponse(shiftDetails.getShiftDate(), activityEntity.getPlayerEmail(), activityEntity.getPlayerPlayground());
	}

}
