package com.sheena.playground;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.logic.ActivityService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityRestControllerTests {

	@Autowired
	private ActivityService activityService;
	
	@PostConstruct
	public void init() {
		
	}
	
	@Before
	public void setup() {
	}
	
	@After
	public void teardown() {
		this.activityService.cleanup();
	}
	
	@Test
	public void testCreateActivitySuccessfully() {
		Map<String, Object> customAttributes = new HashMap<>();
		customAttributes.put("language", "java");
		customAttributes.put("numOfStudyYrs", 4);
		
		
		ActivityTO postActivity = new ActivityTO(
				"playground",
				"id",
				"elementPlayground",
				"elementId",
				"type",
				"playerPlayground",
				"playerEmail",
				customAttributes);
		
		
	}
	
	@Test(expected=Exception.class)
	public void testCreateActivityWithExistingActivity() {
	}

}
