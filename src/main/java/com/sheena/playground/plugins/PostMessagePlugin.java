package com.sheena.playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.plugins.messageBoard.BoardMessage;
import com.sheena.playground.plugins.messageBoard.BoardMessageResponse;

@Component
public class PostMessagePlugin implements PlaygroundPlugin {

	private ElementService elementService;
	private UsersService usersService;
	private ObjectMapper jackson;
	public static final String MESSAGE_BOARD_ELEMENT_TYPE = "messageBoard";
	public static final long AWARD_POINTS = 5L;
	
	public PostMessagePlugin() {
	}

	@Autowired
	public PostMessagePlugin(ElementService elementService, UsersService usersService) {
		this.elementService = elementService;
		this.usersService = usersService;
	}
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Override
	public Object invokeOperation(ActivityEntity activityEntity) throws Exception {
		ElementEntity entity = elementService.getElementById(activityEntity.getElementId());

		if(!entity.getType().equals(MESSAGE_BOARD_ELEMENT_TYPE))
			throw new ElementDoesNotMatchActivityException("activity requires element of type: " + MESSAGE_BOARD_ELEMENT_TYPE);
		
		BoardMessage message = this.jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()), BoardMessage.class);
		
		UserEntity player = this.usersService.getUserByEmail(activityEntity.getPlayerEmail());
		this.usersService.updatePoints(player, AWARD_POINTS);
		
		return new BoardMessageResponse(message.getText(), activityEntity.getPlayerEmail(), activityEntity.getPlayerPlayground());
	}
}
