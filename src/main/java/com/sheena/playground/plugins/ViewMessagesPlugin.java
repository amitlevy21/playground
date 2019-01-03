package com.sheena.playground.plugins;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;

@Component
public class ViewMessagesPlugin implements PlaygroundPlugin {
	
	private ElementService elementService;
	private ActivityDao activityDao;
	private ObjectMapper jackson;
	public final String MESSAGE_BOARD_ELEMENT_TYPE = "messageBoard";
	public final String POST_MESSAGE_ACTIVITY_TYPE = "PostMessage";
	
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
	public Object invokeOperation(ActivityEntity activityEntity) {
		try {
			ElementEntity entity = elementService.getElementById(activityEntity.getElementId());
			if(!entity.getType().equals(MESSAGE_BOARD_ELEMENT_TYPE))
				throw new ElementDoesNotMatchActivityException("activity PostMessage requires element of type: " + MESSAGE_BOARD_ELEMENT_TYPE);
			
			ViewMessagesParameters parameters = this.jackson.readValue(
					this.jackson.writeValueAsString(
							activityEntity.getAttributes()), ViewMessagesParameters.class);
			
			System.err.println(activityEntity.getAttributes());
			
			List<ActivityEntity> messages = activityDao.findActivityByType(
					POST_MESSAGE_ACTIVITY_TYPE, PageRequest.of(parameters.getPage(), parameters.getSize()));
			
			
			return messages.stream().map(ActivityTO::new).collect(Collectors.toList())/*.toArray(new ActivityTO[0])*/;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
