package com.sheena.playground.logic.activities.jpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.aop.IsUserPlayer;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.logic.jpa.IdGeneratorDao;

import com.sheena.playground.plugins.PlaygroundPlugin;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotSupportedException;
import com.sheena.playground.logic.activities.ActivityWithNoTypeException;

@Service
public class JpaActivityService implements ActivityService {
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;
	private ApplicationContext spring;

	// REGISTERY SHIFT PLUGINS
	private final String REGISTER_SHIFT = "RegisterShift";
	private final String CANCEL_SHIFT = "CancelShift";
	private final String REGISTERY_SHIFT_PACKAGE = "shiftRegistery";

	// ATTENDANCE CLOCK PUGINS
	private final String CLOCK = "Clock";
	private final String ATTENDANCE_CLOCK_PACKAGE = "attendaceClock";

	// MESSAGE BOARD PLUGINS
	private final String POST_MESSAGE = "PostMessage";
	private final String VIEW_MESSAGES = "ViewMessages";
	private final String MESSAGE_BOARD_PACKAGE = "ViewMessages";

	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator, ApplicationContext spring) {
		super();
		this.activities = activities;
		this.idGenerator = idGenerator;
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
			throws ActivityTypeNotSupportedException, ActivityWithNoTypeException {
		String type = activityEntity.getType();

		if (type == null) {
			throw new ActivityWithNoTypeException("activity must have field: type");
		} 
		
		Object rv = null;
		Object[] rvArray = null;
		Map<String, Object> rvMap = null;
		boolean isArrayFlag = true;

		try {
			String className = "com.sheena.playground.plugins." + type + "Plugin";
			Class<?> theClass = Class.forName(className);
			PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
			rv = plugin.invokeOperation(activityEntity);

			isArrayFlag = (rv instanceof Object[]);

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

		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);
		activityEntity.setId("" + dummyId);
		this.activities.save(activityEntity);

		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityEntity getActivityByType(String type) throws ActivityTypeNotSupportedException {
		return this.activities.findById(type)
				.orElseThrow(() -> new ActivityTypeNotSupportedException("Activity's type is not allowed: " + type));
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
