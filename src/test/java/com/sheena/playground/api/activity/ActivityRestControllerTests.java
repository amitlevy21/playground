package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.api.users.UserTOComparator;

import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.users.UsersService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActivityRestControllerTests {
	private static final String ACTIVITIES_URL = "/playground/activities/{userPlayground}/{email}";

	private final int numCases = 3;
	private final int numOfRoles = 2;

	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	@SuppressWarnings("unused")
	private ObjectMapper jsonMapper;
	private Comparator<UserTO> userTOComparator;
	private Comparator<ActivityEntity> activityEntityComparator;
	private final String format = "MM/dd/yyyy hh:mm a";
	private SimpleDateFormat sdf;
	
	
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UsersService usersService;

	// Data attributes for users
	private final String userName = "user";
	private final String emailDomain = "@afeka.edu";
	private final String playground = "Sheena.2019A";
	private final String avatar = "lion";
	private final String playerRole = "player";
	private final String managerRole = "manager";
	private final String verificationCodeSuffix = "code";
	private final String userPlayground = "userPlayground";
	private final String elementPlayground = "elementPlayground";
	private final String elementId = "elementId";
	
	private final String unsupportedType = "unsupportedType";
	
	// Data attributes for check-in & out
	private final String FUTURE = "FUTURE";
	private final String PAST = "PAST";
	private final String PRESENT = "PRESENT";
	
	// Data attributes for check-in
	private final String successCheckInMessage = "Welcome, have a nice day!";
	private final String failFutureCheckInDateMessage = "Your start date is in the future - NOT VALID!";
	private final String failPastCheckInDateMessage = "It's has been passed more than hour since your start date - NOT VALID!";
	private final String CheckInType = "CheckIn";
	private final String CheckInAttributesKey = "start";
	
	// Data attributes for check-out
	private final String successCheckOutMessage = "Welcome, have a nice day!";
	private final String failFutureCheckOutDateMessage = "Your start date is in the future - NOT VALID!";
	private final String failPastCheckOutDateMessage = "It's has been passed more than hour since your start date - NOT VALID!";
	private final String CheckOutType = "CheckIn";
	private final String CheckOutAttributesKey = "start";

	// Even places (0,2,4...) are PLAYERs, odd places are MANAGERs (1,3,5...)
//	private List<NewUserForm> newUserForms;
//	private List<ActivityTO> dummyActivities;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port;
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		this.userTOComparator = new UserTOComparator();
		this.activityEntityComparator = new ActivityEntityCompartor();
		this.sdf = new SimpleDateFormat(this.format);
		System.err.println(this.url);
	}

	@Before
	public void setup() {
//		this.newUserForms = this.generateNewUserForms();
//		this.dummyActivities = this.generateActivities();

	}

	@After
	public void teardown() {
		this.activityService.cleanup();
		this.usersService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}
	
	///////////////////////////////// Check-In Plugin Tests /////////////////////////////////
	
	@Test
	public void testVerifiedPlayerUserCheckInSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 0;
		
		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);
		
		
		// when
		ActivityTO activity = generateSpecificCheckInOut(
						this.CheckInType,
						this.CheckInAttributesKey,
						testId,
						this.PRESENT);
		
		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());
		
		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(successCheckInMessage);
		
		assertThat(actual)
			.isNotNull()
			.usingComparator(this.activityEntityComparator)
			.isEqualTo(expectedOutcome);
	}
	
	@Test
	public void testVerifiedPlayerUserCheckInWithFutreDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 1;

		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);


		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckInType,
				this.CheckInAttributesKey,
				testId,
				this.FUTURE);

		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(this.failFutureCheckInDateMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);
	}
	
	@Test
	public void testVerifiedPlayerUserCheckInWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 2;

		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);


		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckInType,
				this.CheckInAttributesKey,
				testId,
				this.PAST);

		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(this.failPastCheckInDateMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);
	}
	
	///////////////////////////////// Check-Out Plugin Tests /////////////////////////////////
	
	@Test
	public void testVerifiedPlayerUserCheckOutSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 3;
		
		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);
		
		
		// when
		ActivityTO activity = generateSpecificCheckInOut(
						this.CheckOutType,
						this.CheckOutAttributesKey,
						testId,
						this.PRESENT);
		
		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());
		
		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(successCheckOutMessage);
		
		assertThat(actual)
			.isNotNull()
			.usingComparator(this.activityEntityComparator)
			.isEqualTo(expectedOutcome);
	}
	
	@Test
	public void testVerifiedPlayerUserCheckOutWithFutreDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 4;

		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);


		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckOutType,
				this.CheckOutAttributesKey,
				testId,
				this.FUTURE);

		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(this.failFutureCheckOutDateMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);
	}
	
	@Test
	public void testVerifiedPlayerUserCheckOutWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 5;

		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);


		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckOutType,
				this.CheckOutAttributesKey,
				testId,
				this.PAST);

		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(this.failPastCheckOutDateMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);
	}
	
	///////////////////////////////// General Check- In&Out Plugin Tests /////////////////////////////////
	
	@Test
	public void testNotPlayerUserCheckIn() throws Exception {
		// Given 
		// The server is up and there is an verified user with "manager" role 
		// (or other role that is not "player"
		final int testId = 6;
		
		NewUserForm newUser = generateSpecificNewUserForms(this.managerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService
					.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
			.isNotNull()
			.usingComparator(this.userTOComparator)
			.isEqualTo(expectedUserTO);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckInType,
				this.CheckInAttributesKey,
				testId,
				this.PRESENT);
		
		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(successCheckInMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}
	
	@Test
	public void testNotPlayerUserCheckOut() throws Exception {
		// Given 
		// The server is up and there is an verified user with "manager" role
		// (or other role that is not "player"
		final int testId = 7;
		
		NewUserForm newUser = generateSpecificNewUserForms(this.managerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService
					.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
			.isNotNull()
			.usingComparator(this.userTOComparator)
			.isEqualTo(expectedUserTO);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		// when
		ActivityTO activity = generateSpecificCheckInOut(
				this.CheckOutType,
				this.CheckOutAttributesKey,
				testId,
				this.PRESENT);
		
		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(successCheckOutMessage);

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}
	
	@Test
	public void testActivityWithUnsupportedType() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 4;
		
		NewUserForm newUser = generateSpecificNewUserForms(this.playerRole, testId);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, this.playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		// when
		ActivityTO activity = generateSpecificCheckInOut(
						this.unsupportedType,
						this.CheckInAttributesKey,
						testId, 
						this.PRESENT);
		
		ActivityTO actualActivity = this.restTemplate
				.postForObject( this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = 
				this.activityService.getActivityById(actualActivity.getId());
		
		assertThat(actual)
		.isNotNull()
		.extracting("attributes")
		.extracting("response")
		.containsExactly(successCheckInMessage);
		
		assertThat(actual)
			.isNotNull()
			.usingComparator(this.activityEntityComparator)
			.isEqualTo(expectedOutcome);

	}
	
	
	private ActivityTO generateSpecificCheckInOut(String type, String attributesPattern, int testCaseNum, String when) {
		Map<String, Object> attributes = new HashMap<>();
		Date theDate;
		String date = "", time = "";
		try {
			if (when.equalsIgnoreCase(this.PAST)) {
				date = "12/26/2009";
				time = "08:58 PM";
				theDate = sdf.parse(date + " " + time);
			} else if (when.equalsIgnoreCase(this.FUTURE)) {
				date = "02/20/2027";
				time = "11:28 PM";
				theDate = sdf.parse(date + " " + time);
			} else {
				theDate = new Date();
			}

			if (type.equalsIgnoreCase(this.CheckInType)) {
				attributes.put(CheckInAttributesKey, theDate);
			} else {
				attributes.put(CheckOutAttributesKey, theDate);
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

		return new ActivityTO(this.elementPlayground, this.elementId, type, this.userPlayground,
				this.userName + "_" + testCaseNum + "_" + this.emailDomain, attributes);

	}		
	
	
	private NewUserForm generateSpecificNewUserForms(String role, int testCaseNum) {
		return new NewUserForm(
				this.userName + "_" + testCaseNum + "_" + this.emailDomain,
				this.userName + "_" + testCaseNum + "_",
				this.avatar,
				role);
	}
}
