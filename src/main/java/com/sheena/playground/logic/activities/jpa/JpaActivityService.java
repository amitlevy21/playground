package com.sheena.playground.logic.activities.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.logic.jpa.IdGeneratorDao;

import com.sheena.playground.plugins.PlaygroundPlugin;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;

@Service
public class JpaActivityService implements ActivityService {
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;
	private ApplicationContext spring;
	private ObjectMapper jackson;
	
	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator, ApplicationContext spring) {
		super();
		this.activities = activities;
		this.idGenerator = idGenerator;
		this.spring = spring;
		this.jackson = new ObjectMapper();
	}

	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<ActivityEntity> getAllActivities(int size, int page) {
		Page<ActivityEntity> activitiesPage = activities.findAll(PageRequest.of(page, size));
		return activitiesPage.getContent();
	}

	@Override
	@Transactional
	public ActivityEntity addNewActivity(ActivityEntity activityEntity)
			throws ActivityTypeNotAllowedException, ActivityWithNoTypeException {
		if(activityEntity.getType() == null)
			throw new ActivityWithNoTypeException("activity must have field: type");
		
		try {
			String type = activityEntity.getType();
			String className = "com.sheena.playground.plugins." + type + "Plugin";
			Class<?> theClass = Class.forName(className);
			
			PlaygroundPlugin plugin = (PlaygroundPlugin) this.spring.getBean(theClass);
			Object rv = plugin.invokeOperation(activityEntity);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> rvMap = this.jackson.readValue(
					this.jackson.writeValueAsString(rv),
					Map.class);
			
			activityEntity.getAttributes().putAll(rvMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		IdGenerator tmp = this.idGenerator.save(new IdGenerator()); 
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);
		
		activityEntity.setId("" + dummyId);
		
		return this.activities.save(activityEntity);
		
	}

	@Override
	@Transactional(readOnly=true)
	public ActivityEntity getActivityByType(String type)
			throws ActivityTypeNotAllowedException {
		return this.activities
				.findById(type)
				.orElseThrow(()->
					new ActivityTypeNotAllowedException(
						"Activity's type is not allowed: " + type));
	}

}
