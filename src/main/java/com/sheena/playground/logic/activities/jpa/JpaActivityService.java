package com.sheena.playground.logic.activities.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.IdGenerator;
import com.sheena.playground.logic.IdGeneratorDao;
import com.sheena.playground.logic.activities.ActivityAlreadyExistsException;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;


@Service
public class JpaActivityService implements ActivityService {
	private final String ALLOWED_TYPE = "Echo";
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;
	
	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator) {
		super();
		this.activities = activities;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<ActivityEntity> getAllActivities(int size, int page) {
		List<ActivityEntity> allList = new ArrayList<>();
		this.activities
				.findAll()
				.forEach(o->allList.add(o));
		
		return 
			allList
			.stream() // MessageEntity stream
			.skip(size * page) // MessageEntity stream 
			.limit(size) // MessageEntity stream
			.collect(Collectors.toList()); // List
	}

	@Override
	@Transactional
	public ActivityEntity addNewActivity(ActivityEntity activityEntity)
			throws ActivityTypeNotAllowedException, ActivityAlreadyExistsException {
		if (!activityEntity.getType().equals(ALLOWED_TYPE)) {
			throw new ActivityTypeNotAllowedException("Activity type is not: " + ALLOWED_TYPE);
		}
		if (this.activities.existsById(activityEntity.getType())) {
			throw new ActivityAlreadyExistsException("Activity already exists with type: " + activityEntity.getType());
		}
		else {
			IdGenerator tmp = this.idGenerator.save(new IdGenerator()); 
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);
			
			activityEntity.setId("" + dummyId);
			
			return this.activities.save(activityEntity);
		}
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
