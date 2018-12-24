package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

	@Autowired
	private ActivityService activityService;
	@Autowired
	private UsersService usersService;

	// Data attributes for test suite
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
	private final String activityType = "type";
	private final String unknownType = "reallyWeirdType";

	// Even places (0,2,4...) are PLAYERs, odd places are MANAGERs (1,3,5...)
	private List<NewUserForm> newUserForms;
	private List<ActivityTO> dummyActivities;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port;
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		this.userTOComparator = new UserTOComparator();

		System.err.println(this.url);
	}

	@Before
	public void setup() {
		this.newUserForms = this.generateNewUserForms();
		this.dummyActivities = this.generateActivities();

	}

	@After
	public void teardown() {
		this.activityService.cleanup();
		// this.usersService.cleanup();
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	@Test
	public void testVerifiedPlayerUserCreateActivitySuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		NewUserForm newUser = this.newUserForms.get(0); // player user form
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		// when
		ActivityTO activity = this.dummyActivities.get(0);
		ActivityTO actualActivity = this.restTemplate.postForObject(this.url + ACTIVITIES_URL, activity,
				ActivityTO.class, verifiedUser.getPlayground(), verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();

		ActivityEntity actual = this.activityService.getActivityByType(actualActivity.getType());
		actual.setId(expectedOutcome.getId());

		assertThat(actual).isNotNull().isEqualTo(expectedOutcome);
	}

	@Test
	public void testVerifiedManagerUserCreateActivity() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role

		NewUserForm newUser = this.newUserForms.get(1); // manger user form
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		// when
		ActivityTO activity = this.dummyActivities.get(1);
		ActivityTO actualActivity = this.restTemplate.postForObject(this.url + ACTIVITIES_URL, activity,
				ActivityTO.class, verifiedUser.getPlayground(), verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();

		ActivityEntity actual = this.activityService.getActivityByType(actualActivity.getType());
		actual.setId(expectedOutcome.getId());

		assertThat(actual).isNotNull().isEqualTo(expectedOutcome);
	}

	@Test
	public void testVerifiedPlayerUserCreateActivityWithUnknownType() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		NewUserForm newUser = this.newUserForms.get(2); // player user form
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(new UserTO(newUser, playground).toEntity()));

		UserTO verifiedUser = new UserTO(this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.verificationCodeSuffix));

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		// when
		ActivityTO activity = this.dummyActivities.get(0);
		activity.setType(unknownType);
		ActivityTO actualActivity = this.restTemplate.postForObject(this.url + ACTIVITIES_URL, activity,
				ActivityTO.class, verifiedUser.getPlayground(), verifiedUser.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();

		ActivityEntity actual = this.activityService.getActivityByType(actualActivity.getType());
		actual.setId(expectedOutcome.getId());

		assertThat(actual).isNotNull().isEqualTo(expectedOutcome);
	}

	private List<NewUserForm> generateNewUserForms() {
		String role;
		int numOfUserForms = this.numCases * this.numOfRoles;
		List<NewUserForm> newUserForms = new ArrayList<>();
		for (int i = 0; i < numOfUserForms; i++) {
			// Even places are for PLAYERs
			if (i % this.numOfRoles == 0) {
				role = this.playerRole;
			}
			// Odd places are for MANAGERs
			else {
				role = this.managerRole;
			}
			newUserForms.add(new NewUserForm(this.userName + "_" + i + "_" + this.emailDomain,
					this.userName + "_" + i + "_", this.avatar, role));
		}
		return newUserForms;
	}

	private List<ActivityTO> generateActivities() {
		List<ActivityTO> activities = new ArrayList<>();
		for (int i = 0; i < this.numCases; i++) {
			activities.add(new ActivityTO(this.playground, this.elementPlayground, this.elementId, this.activityType,
					this.userPlayground, this.userName + "_" + i + "_" + this.emailDomain, null));

		}
		return activities;
	}
}
