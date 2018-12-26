package com.sheena.playground.logic.activities;

import java.util.List;


public interface ActivityService {
		
	public void cleanup();
		
	public List<ActivityEntity> getAllActivities (int size, int page);

	ActivityEntity addNewActivity(ActivityEntity activityEntity) throws ActivityTypeNotSupportedException;

	List<ActivityEntity> getActivitiesByType(String type, int size, int page) throws ActivityNotFoundException;

	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException;
	
}
