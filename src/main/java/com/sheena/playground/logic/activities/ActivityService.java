package com.sheena.playground.logic.activities;

import java.util.List;


public interface ActivityService {
		
	public void cleanup();
	
	public ActivityEntity getActivityByType(String type) throws ActivityNotFoundException;
	
	public List<ActivityEntity> getAllActivities (int size, int page);

	ActivityEntity addNewActivity(ActivityEntity activityEntity) throws ActivityTypeNotSupportedException;
	
}
