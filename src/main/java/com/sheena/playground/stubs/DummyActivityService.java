package com.sheena.playground.stubs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sheena.playground.logic.ActivityAlreadyExistsException;
import com.sheena.playground.logic.ActivityEntity;
import com.sheena.playground.logic.ActivityService;

@Service
public class DummyActivityService implements ActivityService{
	private Map<String, ActivityEntity> activities;
	
	@PostConstruct
	public void init() {
		this.activities = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public ActivityEntity addNewActivity(ActivityEntity activity) throws ActivityAlreadyExistsException {
		if (this.activities.containsKey(activity.getId())) {
			throw new ActivityAlreadyExistsException();
		}
		this.activities.put(activity.getId(), activity);
		return activity;
	}

	@Override
	public void cleanup() {
		this.activities.clear();
	}


}
