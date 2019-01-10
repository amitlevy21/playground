package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.plugins.ClockPlugin;
import com.sheena.playground.plugins.attendanceClock.AttendanceClockResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AttendanceClockPluginTests {

	@LocalServerPort
	private int port;

	@Value("${playground.name:defaultPlayground}")
	private String playgroundName;

	private final String url = "/playground/activities/%s/%s"; // /playground/activities/{userPlayground}/{email}
	private final int pageableSize = 10;
	private final int pageablePage = 0;

	private String host = "http://localhost:%s";
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;

	@Autowired
	private UsersService usersService;

	@Autowired
	private ElementService elementService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivityDao activityDao;

	// Data attributes for test suite
	private final String emailDomain = "@afeka.edu";
	private final String userName1 = "user1";
	private final String userName2 = "user2";
	private final String avatar = "lion";
	private final String playerRole = "player";
	private final String managerRole = "manager";

	private UserEntity managerUser;
	private UserEntity playerUser;

	private String activityType = ClockPlugin.CLOCK_ACTIVITY_TYPE;
	private String attendanceClockElementType = ClockPlugin.ATTENDANCE_CLOCK_ELEMENT_TYPE;

	private final String attendanceClockDateFormat = "yyyy-MM-dd";
	private final String attendanceClock01Expiry = "2090-01-01";
	private ElementEntity attendanceClock01;
	private final String attendanceClock01Date = "2020-01-01";
	private final String dummyClockInDate = "2002-01-01";

	@PostConstruct
	public void init() {
		this.host = String.format(this.host, this.port);
		this.restTemplate = new RestTemplate();
		this.jsonMapper = new ObjectMapper();
	}

	@Before
	public void setup() throws UserAlreadyExistsException, RoleDoesNotExistException, ParseException {
		this.managerUser = new UserEntity(userName1 + emailDomain, playgroundName, userName1, avatar, managerRole);
		this.playerUser = new UserEntity(userName2 + emailDomain, playgroundName, userName2, avatar, playerRole);

		this.managerUser.setVerifiedUser(true);
		this.playerUser.setVerifiedUser(true);

		// Creating the users already verified - a hack to avoid verification via server
		this.managerUser = this.usersService.createNewUser(this.managerUser);
		this.playerUser = this.usersService.createNewUser(this.playerUser);

		Map<String, Object> attendanceClock01Attributes = new HashMap<>();
		attendanceClock01Attributes.put("workDate",
				new SimpleDateFormat(attendanceClockDateFormat).parse(this.attendanceClock01Date));

		this.attendanceClock01 = new ElementEntity(playgroundName, 0.0, 0.0, "attendanceClock01", new Date(),
				new SimpleDateFormat(attendanceClockDateFormat).parse(attendanceClock01Expiry),
				attendanceClockElementType, attendanceClock01Attributes, playgroundName, managerUser.getEmail());

		this.attendanceClock01 = this.elementService.addNewElement(this.managerUser.getEmail(), this.attendanceClock01);
	}

	@After
	public void teardown() {
		this.usersService.cleanup();
		this.elementService.cleanup();
		this.activityService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	@Test
	public void testClockInSuccessfully()
			throws ParseException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		String requestUrl = this.host + String.format(url, playgroundName, playerUser.getEmail());

		Map<String, Object> clockInActivityAttributes = new HashMap<>();
		clockInActivityAttributes.put("clockingDate",
				new SimpleDateFormat(attendanceClockDateFormat).parse(this.attendanceClock01Date));

		ActivityTO clockInActivity = new ActivityTO(this.attendanceClock01.getPlayground(),
				this.attendanceClock01.getId(), this.activityType, this.playerUser.getPlayground(),
				this.playerUser.getEmail(), clockInActivityAttributes);

		Object rv = this.restTemplate.postForObject(requestUrl, clockInActivity, Object.class);

		AttendanceClockResponse rvCastToResponse = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), AttendanceClockResponse.class);

		ActivityEntity activityEntity = this.activityDao
				.findActivityByElementId(this.attendanceClock01.getId(), PageRequest.of(pageablePage, pageableSize))
				.get(0);

		AttendanceClockResponse expected = (AttendanceClockResponse) activityEntity.getResponse()[0];
		
		assertThat(rvCastToResponse).isNotNull().isEqualTo(expected);
	}
	
	@Test
	public void testClockInWrongDate()
			throws ParseException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		String requestUrl = this.host + String.format(url, playgroundName, playerUser.getEmail());

		Map<String, Object> clockInActivityAttributes = new HashMap<>();
		clockInActivityAttributes.put("clockingDate",
				new SimpleDateFormat(attendanceClockDateFormat).parse(this.dummyClockInDate));

		ActivityTO clockInActivity = new ActivityTO(this.attendanceClock01.getPlayground(),
				this.attendanceClock01.getId(), this.activityType, this.playerUser.getPlayground(),
				this.playerUser.getEmail(), clockInActivityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		this.restTemplate.postForObject(requestUrl, clockInActivity, Object.class);
	}

}
