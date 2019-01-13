package com.sheena.playground.plugins;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.plugins.shiftRegistery.ShiftDetails;
import com.sheena.playground.plugins.shiftRegistery.ShiftForm;
import com.sheena.playground.plugins.shiftRegistery.ShiftRegisteryDateMismatchException;
import com.sheena.playground.plugins.shiftRegistery.ShiftResponse;
import com.sheena.playground.plugins.shiftRegistery.fullShiftException;

@Component
public class RegisterShiftPlugin implements PlaygroundPlugin {
	public static final String SHIFT_ELEMENT_TYPE = "shift";
	public static final long AWARD_POINTS = 10L;

	private ObjectMapper jackson;
	private ElementService elementService;
	private UsersService usersService;
	private ActivityDao activityDao;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setElementService(ElementService elementService, UsersService usersService) {
		this.elementService = elementService;
		this.usersService = usersService;
	}
	
	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public Object invokeOperation(ActivityEntity activityEntity) throws Exception {
		ElementEntity elementEntity = this.elementService.getElementById(activityEntity.getElementId());
		
		String elementType = elementEntity.getType();
		
		if (!elementType.equalsIgnoreCase(SHIFT_ELEMENT_TYPE)) {
			throw new ElementDoesNotMatchActivityException(
					"activity requires element of type: " + SHIFT_ELEMENT_TYPE);
		}

		ShiftDetails shiftDetails = this.jackson.readValue(
				this.jackson.writeValueAsString(elementEntity.getAttributes()),
				ShiftDetails.class);
		
		ShiftForm form = this.jackson.readValue(this.jackson.writeValueAsString(
				activityEntity.getAttributes()), ShiftForm.class);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(!dateFormat.format(shiftDetails.getShiftDate()).equals(dateFormat.format(form.getWantedShiftDate())))
			throw new ShiftRegisteryDateMismatchException("cannot register to shift in date " + form.getWantedShiftDate() + ". no such shift");

		int numOfWorkers = this.activityDao
							.findActivityByElementId(
									activityEntity.getElementId(),
									PageRequest.of(0, shiftDetails.getMaxWorkersInShift()))
							.size();

		if (numOfWorkers == shiftDetails.getMaxWorkersInShift())
			throw new fullShiftException("cannot register to shift in date: " + shiftDetails.getShiftDate() + " because shift is already full");
		
		UserEntity player = this.usersService.getUserByEmail(activityEntity.getPlayerEmail());
		this.usersService.updatePoints(player, AWARD_POINTS);
		
		return new ShiftResponse(shiftDetails.getShiftDate(), activityEntity.getPlayerEmail(), activityEntity.getPlayerPlayground());
	}
}