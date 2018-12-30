package com.sheena.playground.logic.activities.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

//import org.springframework.stereotype.Service;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;

//@Service
public class DummyActivityService implements ActivityService {
	private Map<String, ActivityEntity> activities;

	private final String ALLOWED_TYPE = "Echo";

	@PostConstruct
	public void init() {
		this.activities = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public void cleanup() {
		this.activities.clear();
	}

	@Override
	public List<ActivityEntity> getAllActivities(int size, int page) {
		return new ArrayList<>(this.activities.values() // collection of MessageEntity
				)// list copy of MessageEntity
				.stream() // MessageEntity stream
				.skip(size * page) // MessageEntity stream
				.limit(size) // MessageEntity stream
				.collect(Collectors.toList()); // List
	}

	@Override
	public ActivityEntity addNewActivity(ActivityEntity activityEntity)
			throws ActivityTypeNotAllowedException {
		if (!activityEntity.getType().equals(ALLOWED_TYPE)) {
			throw new ActivityTypeNotAllowedException("Activity type is not: " + ALLOWED_TYPE);
		}

		this.activities.put(activityEntity.getType(), activityEntity);
		return activityEntity;
	}

	@Override
	public ActivityEntity getActivityByType(String type) throws ActivityTypeNotAllowedException {
		ActivityEntity rv = this.activities.get(type);
		if (rv == null) {
			throw new ActivityTypeNotAllowedException("Activity not found for type: " + type);
		}
		return rv;
	}

}
