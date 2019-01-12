package com.sheena.playground.logic.activities.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.aop.IsUserPlayer;
import com.sheena.playground.dal.ActivityDao;

import com.sheena.playground.plugins.PlaygroundPlugin;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;
import com.sheena.playground.logic.activities.ActivityWithNoTypeException;

@Service
public class JpaActivityService implements ActivityService {
	private ActivityDao activities;
	private ApplicationContext spring;
	private String playgroundName;

	@Value("${playground.name:defaultPlayground}")
	public void setPlaygroundName(String playgroundName) {
		this.playgroundName = playgroundName;
	}

	@Autowired
	public JpaActivityService(ActivityDao activities, ApplicationContext spring) {
		super();
		this.activities = activities;
		this.spring = spring;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityEntity> getAllActivities(int size, int page) {
		Page<ActivityEntity> activitiesPage = activities.findAll(PageRequest.of(page, size));
		return activitiesPage.getContent();
	}

	@Override
	@Transactional
	@IsUserPlayer
	public Object addNewActivity(ActivityEntity activityEntity, String userPlayground, String email)
			throws ActivityTypeNotAllowedException, ActivityWithNoTypeException {
		if (activityEntity.getType() == null)
			throw new ActivityWithNoTypeException("activity must have type");

		Object rv = null;
		Object[] rvArray = null;
		boolean isArrayFlag = true;

		try {
			String type = activityEntity.getType();
			String className = "com.sheena.playground.plugins." + type + "Plugin";
			Class<?> theClass = Class.forName(className);
			PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
			rv = plugin.invokeOperation(activityEntity);

			isArrayFlag = rv instanceof Object[];

			if (isArrayFlag) {
				rvArray = (Object[]) rv;
				activityEntity.setResponse(rvArray);
			} else {
				Object[] singleResponse = new Object[1];
				singleResponse[0] = rv;
				activityEntity.setResponse(singleResponse);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		activityEntity.setPlayground(this.playgroundName);
		this.activities.save(activityEntity);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityEntity getActivityByType(String type) throws ActivityTypeNotAllowedException {
		return this.activities.findById(type)
				.orElseThrow(() -> new ActivityTypeNotAllowedException("Activity's type is not allowed: " + type));
	}

	@Override
	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException {
		Optional<ActivityEntity> op = this.activities.findById(id);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new ActivityNotFoundException("No activity found with id: " + id);
		}
	}

}
