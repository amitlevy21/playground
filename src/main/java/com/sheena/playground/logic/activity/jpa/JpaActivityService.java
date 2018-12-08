package com.sheena.playground.logic.activity.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activity.ActivityAlreadyExistsException;
import com.sheena.playground.logic.activity.ActivityEntity;
import com.sheena.playground.logic.activity.ActivityNotFoundException;
import com.sheena.playground.logic.activity.ActivityService;
import com.sheena.playground.logic.activity.ActivityTypeNotAllowedException;

@Service
public class JpaActivityService implements ActivityService {
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;

	@Autowired
	public JpaActivityService(IdGeneratorDao idGenerator, ActivityDao activities) {
		this.activities = activities;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	public void cleanup() {
		this.activities.deleteAll();
	}

	@Override
	public ActivityEntity getActivityByType(String type) throws ActivityNotFoundException {
		return this.activities.findById(type)
				.orElseThrow(() -> new ActivityNotFoundException("No activities with type: " + type));
	}

	@Override
	public List<ActivityEntity> getAllActivities(int size, int page) {
		List<ActivityEntity> allList = new ArrayList<>();
		this.activities.findAll().forEach(o -> allList.add(o));

		return allList.stream() // ActivityEntity stream
				.skip(size * page) // ActivityEntity stream
				.limit(size) // ActivityEntity stream
				.collect(Collectors.toList()); // List
	}

	@Override
	public ActivityEntity addNewActivity(ActivityEntity activityEntity)
			throws ActivityTypeNotAllowedException, ActivityAlreadyExistsException {
		final String ALLOWED_TYPE = "Echo";
		
		if (!activityEntity.getType().equals(ALLOWED_TYPE)) {
			throw new ActivityTypeNotAllowedException("Activity type is not: " + ALLOWED_TYPE);
		}
		if (this.activities.existsById(activityEntity.getType())) {
			throw new ActivityAlreadyExistsException("Activity already exists with type: " + activityEntity.getType());
		}
		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);

		activityEntity.setId("" + dummyId);

		return this.activities.save(activityEntity);
	}

}
