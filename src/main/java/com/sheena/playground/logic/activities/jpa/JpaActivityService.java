package com.sheena.playground.logic.activities.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sheena.playground.aop.MyLog;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.dal.IdGeneratorDao;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotSupportedException;

@Service
public class JpaActivityService implements ActivityService {
	private final String unknownType = "reallyWeirdType";
	
	private ActivityDao activities;
	private IdGeneratorDao idGenerator;

	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator) {
		this.activities = activities;
		this.idGenerator = idGenerator;
	}

	@Override
	@Transactional
	@MyLog
	public void cleanup() {
		this.activities.deleteAll();

	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public List<ActivityEntity> getAllActivities(int size, int page) {
		List<ActivityEntity> allList = new ArrayList<>();
		this.activities.findAll().forEach(o -> allList.add(o));

		return allList.stream() // MessageEntity stream
				.skip(size * page) // MessageEntity stream
				.limit(size) // MessageEntity stream
				.collect(Collectors.toList()); // List
	}

	@Override
	@Transactional
	@MyLog
	public ActivityEntity addNewActivity(ActivityEntity activityEntity) throws ActivityTypeNotSupportedException {
		if (this.unknownType.equalsIgnoreCase(activityEntity.getType())) {
			throw new ActivityTypeNotSupportedException("Activity's type is not supported: " + activityEntity.getType());
		}
		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);
		activityEntity.setId("" + dummyId);
		return this.activities.save(activityEntity);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public ActivityEntity getActivityByType(String type) throws ActivityNotFoundException {
		return this.activities.findById(type)
				.orElseThrow(() -> new ActivityNotFoundException("Activity's type is not found: " + type));
	}

}
