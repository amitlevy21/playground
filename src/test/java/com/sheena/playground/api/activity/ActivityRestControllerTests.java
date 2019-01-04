package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.api.elements.ElementTOBasicComparator;
import com.sheena.playground.api.users.UserTOComparator;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;


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
	private Comparator<ElementTO> elementTOComparator;
	private ActivityRestControllerTestsHelper helper;

	@Autowired
	private ActivityService activityService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private ElementService elementsService;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	private UserTO managerVerifiedUserTO;
	private UserEntity managerVerifiedUserEntity;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port;
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		this.userTOComparator = new UserTOComparator();
		this.activityEntityComparator = new ActivityEntityCompartor();
		this.elementTOComparator = new ElementTOBasicComparator();
		this.helper = new ActivityRestControllerTestsHelper();
		System.err.println(this.url);
	}

	@Before
	public void setup() throws Exception {
		// setting up a manager user who creates all elements
		NewUserForm newUser =
				this.helper.generateSpecificNewUserForms(this.helper.managerRole, 99);
		UserTO expectedUserTO = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser, this.helper.playground).toEntity()));

		this.managerVerifiedUserEntity = 
				this.usersService.verifyUserRegistration(
						expectedUserTO.getPlayground(),
						expectedUserTO.getEmail(),
						expectedUserTO.getEmail() + this.helper.verificationCodeSuffix);

		this.managerVerifiedUserTO = new UserTO(managerVerifiedUserEntity);

		assertThat(managerVerifiedUserTO)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO);

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

	///////////////////////////////// Check-In&Out Plugin Tests (0-4) /////////////////////////////////

	@Test
	public void testVerifiedPlayerCheckInAndOutSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 0;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificCheckInOutElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.checkInOutElement,
						this.helper.CHECK_IN_OUT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Check-In
		ActivityTO checkInActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_IN_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckInActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkInActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Check-Out
		ActivityTO checkOutActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_OUT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckOutActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkOutActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// Then
		ActivityEntity expectedCheckInOutcome = checkInActivity.toActivityEntity();
		expectedCheckInOutcome.setId(actualCheckInActivity.getId());
		expectedCheckInOutcome.setPlayground(actualCheckInActivity.getPlayground());

		ActivityEntity actualCheckIn =
				this.activityService.getActivityById(actualCheckInActivity.getId());

		assertThat(actualCheckIn)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedCheckInOutcome);

		ActivityEntity expectedCheckOutOutcome = checkOutActivity.toActivityEntity();
		expectedCheckOutOutcome.setId(actualCheckOutActivity.getId());
		expectedCheckOutOutcome.setPlayground(actualCheckOutActivity.getPlayground());

		ActivityEntity actualCheckOut =
				this.activityService.getActivityById(actualCheckOutActivity.getId());

		assertThat(actualCheckOut)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedCheckOutOutcome);


	}

	@Test
	public void testVerifiedPlayerCheckInWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 1;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificCheckInOutElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.checkInOutElement,
						this.helper.CHECK_IN_OUT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Check-In
		ActivityTO checkInActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_IN_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PAST_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCheckInActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkInActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Check-Out
		ActivityTO checkOutActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_OUT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckOutActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkOutActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// Then
		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	@Test
	public void testVerifiedPlayerCheckOutWithPastDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 2;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificCheckInOutElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.checkInOutElement,
						this.helper.CHECK_IN_OUT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Check-In
		ActivityTO checkInActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_IN_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckInActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkInActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Check-Out
		ActivityTO checkOutActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_OUT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PAST_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCheckOutActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkOutActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	@Test
	public void testVerifiedPlayerCheckInWithFutureDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 3;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificCheckInOutElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.checkInOutElement,
						this.helper.CHECK_IN_OUT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Check-In
		ActivityTO checkInActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_IN_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.FUTURE_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCheckInActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkInActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Check-Out
		ActivityTO checkOutActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_OUT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckOutActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkOutActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// Then
		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	@Test
	public void testVerifiedPlayerCheckOutWithFutureDate() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 4;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificCheckInOutElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.checkInOutElement,
						this.helper.CHECK_IN_OUT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Check-In
		ActivityTO checkInActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_IN_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.PRESENT_DATE);

		ActivityTO actualCheckInActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkInActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Check-Out
		ActivityTO checkOutActivity = this.helper.generateSpecificCheckInOutActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.CHECK_OUT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.FUTURE_DATE);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCheckOutActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						checkOutActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Then
		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	///////////////////////////////// Register&Cancel Shift Plugin Tests (5-8) /////////////////////////////////

	@Test
	public void testVerifiedPlayerRgisterAndCancelShiftSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 5;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificShiftElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.SHIFT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Register to a shift
		ActivityTO registerActivity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualRegisterActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						registerActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Cancel a shift
		ActivityTO cancelActivity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				verifiedUser.getPlayground(),
				actualElement.getId(),
				this.helper.CANCEL_SHIFT_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualCancelActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						cancelActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// Then
		ActivityEntity expectedRegisterOutcome = registerActivity.toActivityEntity();
		expectedRegisterOutcome.setId(actualRegisterActivity.getId());
		expectedRegisterOutcome.setPlayground(actualRegisterActivity.getPlayground());

		ActivityEntity actualRegister =
				this.activityService.getActivityById(actualRegisterActivity.getId());

		assertThat(actualRegister)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedRegisterOutcome);


		ActivityEntity expectedCancelOutcome = cancelActivity.toActivityEntity();
		expectedCancelOutcome.setId(actualCancelActivity.getId());
		expectedCancelOutcome.setPlayground(actualCancelActivity.getPlayground());

		ActivityEntity actualCancel =
				this.activityService.getActivityById(actualCancelActivity.getId());

		assertThat(actualCancel)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedCancelOutcome);

	}

	@Test
	public void testVerifiedPlayerRgisterShiftAndCancelUnregistedShift() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 6;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificShiftElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.SHIFT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);


		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Register to a shift
		ActivityTO registerActivity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualRegisterActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						registerActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Cancel a shift
		ActivityTO cancelActivity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.CANCEL_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				!this.helper.shiftIsExists);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCancelActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						cancelActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	@Test
	public void testVerifiedPlayersRgisterShiftAndNoMoreSpotsLeft() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 7;

		//////////////////////////////// Users ////////////////////////////////
		NewUserForm newUser1 =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId);
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

		NewUserForm newUser2 =
				this.helper.generateSpecificNewUserForms(this.helper.playerRole, testId + 100);
		UserTO expectedUserTO2 = new UserTO(
				this.usersService.createNewUser(new UserTO(newUser2, this.helper.playground).toEntity()));

		UserEntity userEntity2 = 
				this.usersService.verifyUserRegistration(
						expectedUserTO2.getPlayground(),
						expectedUserTO2.getEmail(),
						expectedUserTO2.getEmail() + this.helper.verificationCodeSuffix);

		UserTO verifiedUser2 = new UserTO(userEntity2);

		assertThat(verifiedUser2)
		.isNotNull()
		.usingComparator(this.userTOComparator)
		.isEqualTo(expectedUserTO2);


		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		// For this test case there is 1 spot in the shift so just one user can register
		ElementTO expectedElement = 
				this.helper.generateSpecificShiftElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.SHIFT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);


		//////////////////////////////// Activities ////////////////////////////////
		// when
		// User1 register to a shift
		ActivityTO registerActivityUser1 = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser1.getEmail(),
				this.helper.shiftIsExists);

		ActivityTO actualRegisterActivityUser1 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						registerActivityUser1,
						ActivityTO.class,
						verifiedUser1.getPlayground(),
						verifiedUser1.getEmail());

		// User1 register to a shift
		ActivityTO registerActivityUser2 = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.REGISTER_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser2.getEmail(),
				this.helper.shiftIsExists);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualRegisterActivityUser2 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						registerActivityUser2,
						ActivityTO.class,
						verifiedUser2.getPlayground(),
						verifiedUser2.getEmail());


		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}

	@Test
	public void testVerifiedPlayersCancelShiftWithoutRegisterShift() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 8;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		// For this test case there is 1 spot in the shift so just one user can register
		ElementTO expectedElement = 
				this.helper.generateSpecificShiftElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.RgisterCancelShiftElement,
						this.helper.SHIFT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);


		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Cancel a shift
		ActivityTO cancelActivity = this.helper.generateSpecificregisterCancelShiftActivity(
				this.helper.playground,
				elementEntity.getPlayground(),
				elementEntity.getId(),
				this.helper.CANCEL_SHIFT_TYPE,
				elementEntity.getCreatorPlayground(),
				verifiedUser.getEmail(),
				!this.helper.shiftIsExists);

		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");

		ActivityTO actualCancelActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						cancelActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		// Not suppose to do the following checks
		System.err.println("Failed to catch Exception!!! - check test#" + testId);
	}
	
	///////////////////////////////// Post&View Messages Plugin Tests (9) /////////////////////////////////

	@Test
	public void testVerifiedPlayerPostAndViewMessagesSuccessfully() throws Exception {
		// Given
		// The server is up and there is an verified user with "player" role
		final int testId = 9;

		//////////////////////////////// Users ////////////////////////////////
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

		//////////////////////////////// Elements ////////////////////////////////
		// Only manager can create Elements
		ElementTO expectedElement = 
				this.helper.generateSpecificMessageBoardElement(
						this.managerVerifiedUserTO.getPlayground(),
						this.helper.MESSAGE_BOARD_ELEMENT_TYPE,
						this.helper.MESSAGE_BOARD_ELEMENT_TYPE,
						this.managerVerifiedUserTO.getUsername(),
						this.managerVerifiedUserTO.getEmail(),
						testId);

		ElementEntity elementEntity = this.elementsService.addNewElement(expectedElement.toEntity());

		ElementTO actualElement = new ElementTO(elementEntity);

		assertThat(actualElement)
		.isNotNull()
		.usingComparator(this.elementTOComparator)
		.isEqualTo(expectedElement);

		//////////////////////////////// Activities ////////////////////////////////
		// when
		// Post 1
		ActivityTO postMessageActivity1 = this.helper.generateSpecificPostViewMessageActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.POST_MESSAGE_ACTIVITY_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.MESSAGE_TO_POST1);

		ActivityTO actualPostActivity1 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						postMessageActivity1,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());

		ActivityTO postMessageActivity2 = this.helper.generateSpecificPostViewMessageActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.POST_MESSAGE_ACTIVITY_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				this.helper.MESSAGE_TO_POST2);

		ActivityTO actualPostActivity2 = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						postMessageActivity2,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// view
		ActivityTO viewMessageActivity = this.helper.generateSpecificPostViewMessageActivity(
				this.helper.playground,
				actualElement.getPlayground(),
				actualElement.getId(),
				this.helper.VIEW_MESSAGE_ACTIVITY_TYPE,
				verifiedUser.getPlayground(),
				verifiedUser.getEmail(),
				"");

		ActivityTO actualViewActivity = this.restTemplate
				.postForObject(
						this.url + ACTIVITIES_URL,
						viewMessageActivity,
						ActivityTO.class,
						verifiedUser.getPlayground(),
						verifiedUser.getEmail());


		// Then
		ActivityEntity expectedPostOutcome1 = postMessageActivity1.toActivityEntity();
		expectedPostOutcome1.setId(actualPostActivity1.getId());
		expectedPostOutcome1.setPlayground(actualPostActivity1.getPlayground());

		ActivityEntity actualPost1 =
				this.activityService.getActivityById(actualPostActivity1.getId());

		assertThat(actualPost1)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedPostOutcome1);

		ActivityEntity expectedPostOutcome2 = postMessageActivity2.toActivityEntity();
		expectedPostOutcome2.setId(actualPostActivity2.getId());
		expectedPostOutcome2.setPlayground(actualPostActivity2.getPlayground());

		ActivityEntity actualPost2 =
				this.activityService.getActivityById(actualPostActivity2.getId());

		assertThat(actualPost2)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedPostOutcome2);

		ActivityEntity expectedViewOutcome = viewMessageActivity.toActivityEntity();
		expectedViewOutcome.setId(actualViewActivity.getId());
		expectedViewOutcome.setPlayground(actualViewActivity.getPlayground());

		ActivityEntity actualView =
				this.activityService.getActivityById(actualViewActivity.getId());

		assertThat(actualView)
		.isNotNull()
		.usingComparator(this.activityEntityComparator)
		.isEqualTo(expectedViewOutcome);

		System.err.println("************* MESSAGES *************");
		System.err.println(actualViewActivity.getAttributes().values());
		System.err.println("************* MESSAGES *************");

	}

}
