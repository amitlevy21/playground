package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.logic.activities.ActivityAlreadyExistsException;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActivityRestControllerTests {
	@LocalServerPort
	private int port;

	private String url;

	private RestTemplate restTemplate;

	private ObjectMapper jsonMapper;

	@Autowired
	private ActivityService activityService;
	
	//dummy1 and dummy2 are equals
	private ActivityTO dummy1;
	private ActivityTO dummy2;
	
	private String playground;
	private String allowedType;
	private String dummyEmail;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activities/{userPlayground}/{email}";
		this.playground = "Sheena.2019A";
		this.allowedType  = "Echo";
		this.dummyEmail = "MyDummyEmail";
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		
		//System.err.println(this.url);
	}

	@Before
	public void setup() {
		Map<String, Object> customAttributes = new HashMap<>();
		customAttributes.put("language", "java");
		customAttributes.put("numOfStudyYrs", 4);
		
		this.dummy1 = new ActivityTO(
				"playground",
				"elementPlayground",
				"elementId",
				this.allowedType,
				this.playground,
				this.dummyEmail,
				customAttributes);
		
		this.dummy2 = new ActivityTO(
				"playground",
				"elementPlayground",
				"elementId",
				this.allowedType,
				this.playground,
				this.dummyEmail,
				customAttributes);
	}

	@After
	public void teardown() {
		this.activityService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	@Test
	public void testCreateActivitySuccessfully() throws Exception {
		// Given
		// The server is up and an activity is provided (in the setup() function)
		
		// When 
		ActivityTO actualActivity = this.restTemplate.postForObject(
				this.url,
				this.dummy1,
				ActivityTO.class,
				this.playground, 
				this.dummyEmail);
		
		// Then
		assertThat(actualActivity.getType()).isEqualTo(allowedType);

		ActivityEntity expectedOutcome = actualActivity.toActivityEntity();

		ActivityEntity actual = this.activityService.getActivityByType(allowedType);
		actual.setId(expectedOutcome.getId());

		assertThat(actual).isNotNull().isEqualTo(expectedOutcome);
	}

	@Test(expected = Exception.class)
	public void testCreateActivityWithExistingActivity() throws ActivityTypeNotAllowedException, ActivityAlreadyExistsException {
		// Given
		// The server is up and a specific activity already exists (in the setup() function)
		this.activityService.addNewActivity(this.dummy1.toActivityEntity());
		
		// When
		this.restTemplate.postForObject(
				this.url,
				this.dummy2,
				ActivityTO.class,
				this.playground, 
				this.dummyEmail);
		
		// Then 
		// an ActivityAlreadyExistsException occurs
	}
	
	
}
