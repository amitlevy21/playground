package com.sheena.playground.logic.activities.jpa;

//import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Optional;
//import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.aop.MyLog;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.dal.IdGeneratorDao;
import com.sheena.playground.logic.jpa.IdGenerator;
import com.sheena.playground.plugins.Plugin;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotSupportedException;

@Service
public class JpaActivityService implements ActivityService {
	// Get the name of the playground from application.properties
	@Value("${name.of.playground}")
	private String PLAYGROUND_NAME;

	private ActivityDao activities;
	private IdGeneratorDao idGenerator;

	private ConfigurableApplicationContext spring;
	private ObjectMapper jackson;

	@Autowired
	public JpaActivityService(ActivityDao activities, IdGeneratorDao idGenerator,
			ConfigurableApplicationContext spring) {
		this.activities = activities;
		this.idGenerator = idGenerator;
		this.spring = spring;
		this.jackson = new ObjectMapper();
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
		/*
		 * List<ActivityEntity> allList = new ArrayList<>();
		 * this.activities.findAll().forEach(o -> allList.add(o));
		 * 
		 * return allList.stream() // MessageEntity stream .skip(size * page) //
		 * MessageEntity stream .limit(size) // MessageEntity stream
		 * .collect(Collectors.toList()); // List
		 */
		return this.activities.findAll(PageRequest.of(page, size, Direction.ASC, "id")).getContent();
	}

	@Override
	@Transactional
	@MyLog
	public ActivityEntity addNewActivity(ActivityEntity activityEntity) throws ActivityTypeNotSupportedException {
//		if (this.unknownType.equalsIgnoreCase(activityEntity.getType())) {
//			throw new ActivityTypeNotSupportedException("Activity's type is not supported: " + activityEntity.getType());
//		}
		activityEntity.setPlayground(PLAYGROUND_NAME);

		IdGenerator tmp = this.idGenerator.save(new IdGenerator());
		Long dummyId = tmp.getId();
		this.idGenerator.delete(tmp);
		activityEntity.setId("" + dummyId);
		ActivityEntity rv = this.activities.save(activityEntity);

		try {
			if (rv.getType() != null) {
				String type = rv.getType();
				Plugin plugin = (Plugin) spring
						.getBean(Class.forName("com.sheena.playground.plugins." + type + "Plugin"));
				Object content = plugin.execute(rv);
				Map<String, Object> rvMap = this.jackson.readValue(this.jackson.writeValueAsString(rv), Map.class);
				activityEntity.getAttributes().putAll(rvMap);
			}
		} catch (ClassNotFoundException e) {
			throw new ActivityTypeNotSupportedException("Activity type is not supported: " + rv.getType());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(e);
		}

		return this.activities.save(activityEntity);
	}

	@Override
	@Transactional(readOnly = true)
	@MyLog
	public List<ActivityEntity> getActivitiesByType(String type, int size, int page) throws ActivityNotFoundException {
		/*
		 * return this.activities.findById(type) .orElseThrow(() -> new
		 * ActivityNotFoundException("Activity's type is not found: " + type));
		 */
		return this.activities.findAllByTypeLike(type, PageRequest.of(page, size, Direction.ASC, "id"));

	}

	@Override
	public ActivityEntity getActivityById(String id) throws ActivityNotFoundException {
		Optional<ActivityEntity> op = this.activities.findById(id);
		if (op.isPresent()) {
			return op.get();
		} else {
			throw new ActivityNotFoundException("No activity with id: " + id);
		}
	}

}
