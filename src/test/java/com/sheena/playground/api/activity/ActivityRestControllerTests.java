package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

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
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.api.users.UserTOComparator;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActivityRestControllerTests {

	private static final String ACTIVITIES_URL = "/playground/activities/{userPlayground}/{email}";

	@LocalServerPort
	private int port;
	private String url;
	private RestTemplate restTemplate;
	@SuppressWarnings("unused")
	private ObjectMapper jsonMapper;
	private Comparator<UserTO> userTOComparator;
	private Comparator<ActivityEntity> activityEntityComparator;
	private ActivityRestControllerTestsHelper helper;

	@Autowired
	private ActivityService activityService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private ElementService elementsService;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port;
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		this.userTOComparator = new UserTOComparator();
		this.activityEntityComparator = new ActivityEntityCompartor();
		this.helper = new ActivityRestControllerTestsHelper();
		System.err.println(this.url);
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		this.usersService.cleanup();
		this.elementsService.cleanup();
		this.activityService.cleanup();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}

	///////////////////////////////// Rgister Shift Plugin Tests (11-X) /////////////////////////////////

	@Test
	public void testVerifiedPlayerRgisterShiftAndCancelSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 11;

		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

		ElementTO elementTO = 
				this.helper.generateSpecificShiftElement(
						verifiedUser.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.REGISTER_CANCEL_SHIFT_TYPE,
						verifiedUser.getUsername(),
						verifiedUser.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		// Register to a shift
		ActivityTO activity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Register to a shift
		ActivityTO activity1 = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.CANCEL_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity1 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}


	@Test
	public void testVerifiedPlayerRgisterShiftAndCancelUnregisteredShift() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 12;

		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

		ElementTO elementTO = 
				this.helper.generateSpecificShiftElement(
						verifiedUser.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.REGISTER_CANCEL_SHIFT_TYPE,
						verifiedUser.getUsername(),
						verifiedUser.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		// Register to a shift
		ActivityTO activity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Register to a shift
		ActivityTO activity1 = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.CANCEL_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity1 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}


	/*
	///////////////////////////////// Check-In Plugin Tests (0-3) /////////////////////////////////

	@Test
	public void testVerifiedPlayerCheckInSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 0;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_IN_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testVerifiedPlayerCheckInWithFutureDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 1;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_IN_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.FUTURE_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testVerifiedPlayerCheckInWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 1;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_IN_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PAST_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testNotPlayerCheckIn() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 3;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.managerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_IN_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	///////////////////////////////// Check-Out Plugin Tests (4-7) /////////////////////////////////

	@Test
	public void testVerifiedPlayerCheckOutSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 4;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_OUT_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);
	}

	@Test
	public void testVerifiedPlayerCheckOutWithFutureDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 5;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_OUT_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.FUTURE_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testVerifiedPlayerCheckOutWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 6;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_OUT_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PAST_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testNotPlayerCheckOut() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 7;

		NewUserForm newUser = this.helper.generateSpecificNewUserForms(this.helper.managerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = this.usersService.verifyUserRegistration(expectedUserTO.getPlayground(),
				expectedUserTO.getEmail(), expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		//		System.err.println("Verified: " + userEntity.isVerifiedUser() + "\nUserId: " + userEntity.getId());

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser).isNotNull().usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);

		ElementTO elementTO = this.helper.generateSpecificCheckInOutElement(verifiedUser.getPlayground(), this.helper.checkInOutElement,
				this.helper.CHECK_OUT_TYPE, verifiedUser.getUsername(), verifiedUser.getEmail(), testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	///////////////////////////////// Rgister Shift Plugin Tests (8-10) /////////////////////////////////

	@Test
	public void testVerifiedPlayerRgisterShiftSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 8;

		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

		ElementTO elementTO = 
				this.helper.generateSpecificShiftElement(
						verifiedUser.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.REGISTER_SHIFT_TYPE,
						verifiedUser.getUsername(),
						verifiedUser.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testVerifiedPlayerRgisterFullShift() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 9;

		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

		NewUserForm newUser1 =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId + 1);
		UserTO expectedUserTO1 = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser1, this.helper.playground).toEntity()));

		UserEntity userEntity1 = 
				this.usersService.verifyUserRegistration(
						expectedUserTO1.getPlayground(),
						expectedUserTO1.getEmail(),
						expectedUserTO1.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser1 = new UserTO(userEntity1);

		assertThat(verifiedUser1)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO1);

		ElementTO elementTO = 
				this.helper.generateSpecificShiftElement(
						verifiedUser.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.REGISTER_SHIFT_TYPE,
						verifiedUser.getUsername(),
						verifiedUser.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity1 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						activity,
						ActivityTO.class,
						verifiedUser1.getPlayground(),
						verifiedUser1.getEmail());

		// Then
		ActivityEntity expectedOutcome = activity.toActivityEntity();
		expectedOutcome.setId(actualActivity.getId());
		expectedOutcome.setPlayground(actualActivity.getPlayground());

		ActivityEntity actual = this.activityService.getActivityById(actualActivity.getId());

		assertThat(actual)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}

	@Test
	public void testVerifiedPlayerRgistertoUnexistsShift() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 10;

		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		UserEntity userEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser = new UserTO(userEntity);

		assertThat(verifiedUser)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

		ElementTO elementTO = 
				this.helper.generateSpecificShiftElement(
						verifiedUser.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.REGISTER_SHIFT_TYPE,
						verifiedUser.getUsername(),
						verifiedUser.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(elementTO.toEntity());

		// when
		ActivityTO activity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				elementEntity.getType(),
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				!this.helper.shiftIsExists);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
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
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedOutcome);

	}
	 */





}
