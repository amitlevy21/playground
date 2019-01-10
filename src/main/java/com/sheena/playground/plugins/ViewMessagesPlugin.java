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
import com.sheena.playground.plugins.messageBoard.BoardMessage;
import com.sheena.playground.plugins.messageBoard.ViewMessagesParameters;

@Component
public class ViewMessagesPlugin implements PlaygroundPlugin {
	
	private ElementService elementService;
	private ActivityDao activityDao;
	private ObjectMapper jackson;
	private final String MESSAGE_BOARD_ELEMENT_TYPE = "messageBoard";
	private final String POST_MESSAGE_ACTIVITY_TYPE = "PostMessage";
	
	private final String MESSAGE_TEXT_ATTRIBUTE = "text";
	private final String PUBLISHER_EMAIL_ATTRIBUTE = "publisherEmail";
	private final String PUBLISHER_PLAYGROUND_ATTRIBUTE = "publisherPlayground";
	
	public ViewMessagesPlugin() {
	}

	@Autowired
	public ViewMessagesPlugin(ElementService elementService, ActivityDao activityDao) {
		this.elementService = elementService;
		this.activityDao = activityDao;
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
		
		BoardMessage[] messages = postMessageActivities.stream()
				.map(a -> new BoardMessage(
						a.getAttributes().get(MESSAGE_TEXT_ATTRIBUTE)+"", 
						a.getAttributes().get(PUBLISHER_EMAIL_ATTRIBUTE)+"", 
						a.getAttributes().get(PUBLISHER_PLAYGROUND_ATTRIBUTE)+""))
				.collect(Collectors.toList())
				.toArray(new BoardMessage[0]);
		
		return messages;
	}
}
