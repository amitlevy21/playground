package com.sheena.playground.logic.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.ActivityAlreadyExistsException;
import com.sheena.playground.logic.ActivityEntity;
import com.sheena.playground.logic.ActivityNotFoundException;
import com.sheena.playground.logic.ActivityService;
import com.sheena.playground.logic.ActivityTypeNotAllowedException;

@Service
public class JpaActivityService implements ActivityService {
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;

	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator) {
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
	public ActivityEntity getActivityByType(String type) throws ActivityNotFoundException {
		return 
				this.activities.findById(type)
				.orElseThrow(()->
					new ActivityNotFoundException(
						"No activity with type: " + type));
	}

	@Override
	@Transactional(readOnly=true)
	public List<ActivityEntity> getAllActivities(int size, int page) {
		List<ActivityEntity> allList = new ArrayList<>();
		this.activities
				.findAll()
				.forEach(o->allList.add(o));
		
		return allList
			.stream() // MessageEntity stream
			.skip(size * page) // MessageEntity stream 
			.limit(size) // MessageEntity stream
			.collect(Collectors.toList()); // List
	}

	@Override
	@Transactional
	public ActivityEntity addNewActivity(ActivityEntity activityEntity)
			throws ActivityTypeNotAllowedException, ActivityAlreadyExistsException {
		// TODO: please pay attention that the identifier of Activity
		// can be also the Id of the activity
		// meanwhile the identifier of Activity is its type
		if (!this.activities.existsById(activityEntity.getType())) {
			IdGenerator tmp = this.idGenerator.save(new IdGenerator());
			Long dummyId = tmp.getId();
			this.idGenerator.delete(tmp);

			activityEntity.setId("" + dummyId);

			return this.activities.save(activityEntity);
			
		} else {
			throw new ActivityAlreadyExistsException("activity exists with type: " + activityEntity.getType());
		}

	}

}
