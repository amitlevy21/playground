package com.sheena.playground.plugins;

import java.util.List;
import java.util.stream.Collectors;

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
import com.sheena.playground.plugins.messageBoard.ViewMessagesParameters;

@Component
public class ViewMessagesPlugin implements PlaygroundPlugin {
	
	private ElementService elementService;
	private UsersService usersService;
	private ActivityDao activityDao;
	private ObjectMapper jackson;
	
	public static final String MESSAGE_BOARD_ELEMENT_TYPE = "messageBoard";
	public static final String POST_MESSAGE_ACTIVITY_TYPE = "PostMessage";
	
	public static final String MESSAGE_TEXT_ATTRIBUTE = "text";
	public static final String PUBLISHER_EMAIL_ATTRIBUTE = "publisherEmail";
	public static final String PUBLISHER_PLAYGROUND_ATTRIBUTE = "publisherPlayground";
	public static final long AWARD_POINTS = -5L;
	
	public ViewMessagesPlugin() {
	}

	@Autowired
	public ViewMessagesPlugin(ElementService elementService, ActivityDao activityDao, UsersService usersService) {
		this.elementService = elementService;
		this.activityDao = activityDao;
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
		
		ViewMessagesParameters parameters = this.jackson.readValue(
				this.jackson.writeValueAsString(
						activityEntity.getAttributes()), ViewMessagesParameters.class);
		
		List<ActivityEntity> postMessageActivities = activityDao.findActivityByType(
				POST_MESSAGE_ACTIVITY_TYPE, 
				PageRequest.of(parameters.getPage(), parameters.getSize()));
		
		Object[] messages = postMessageActivities.stream()
				.map(activity -> activity.getResponse()[0])
				.collect(Collectors.toList())
				.toArray(new Object[0]);
		
		UserEntity player = this.usersService.getUserByEmail(activityEntity.getPlayerEmail());
		this.usersService.updatePoints(player, AWARD_POINTS);
		
		return messages;
	}
}
