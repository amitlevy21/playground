package com.sheena.playground.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.logic.ActivityEntity;
import com.sheena.playground.logic.ActivityService;

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

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/playground/activities";
		System.err.println(this.url);

		// Jackson init
		this.jsonMapper = new ObjectMapper();
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		this.activityService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	@Test
	public void testCreateActivitySuccessfully() throws Exception {
		String allowdType = "allowdType";

		Map<String, Object> customAttributes = new HashMap<>();
		customAttributes.put("language", "java");
		customAttributes.put("numOfStudyYrs", 4);

		ActivityTO postActivity = new ActivityTO(
				"playground",
				"elementPlayground",
				"elementId",
				allowdType,
				customAttributes);
		
		postActivity.setPlayerPlayground("myUserPlayground");
		postActivity.setPlayerEmail("myEmail");
		
		
		System.err.println(postActivity.toString());

		ActivityTO actualReturnedValue = this.restTemplate.postForObject(
				this.url + "/{userPlayground}/{email}",
				postActivity,
				ActivityTO.class);
		
		
		assertThat(actualReturnedValue.getType()).isEqualTo(allowdType);

		ActivityEntity expectedOutcome = postActivity.toActivityEntity();

		assertThat(this.activityService.getActivityByType(allowdType)).isNotNull().isEqualTo(expectedOutcome);
	}

	@Test(expected = Exception.class)
	public void testCreateActivityWithExistingActivity() {
	}

}
