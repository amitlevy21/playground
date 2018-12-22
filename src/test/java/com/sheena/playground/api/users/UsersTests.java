package com.sheena.playground.api.users;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class UsersTests {
	
	private static final String PREFIX_URL = "/{playground}/{email}";
	private static final String LOGIN_URL = "/login/{playground}/{email}";
	private static final String CONFIRM_URL = "/confirm/{playground}/{email}/{code}";

	private final int numCases = 9;

	@LocalServerPort
	private int port;
	
	private String url;
	private RestTemplate restTemplate;
	@SuppressWarnings("unused")
	private ObjectMapper jsonMapper;
	private Comparator<UserTO> userTOComparator;
	
	@Autowired
	private UsersService usersService;
	
	private final String playground = "Sheena.2019A";
	private final String verificationCodeSuffix = "code";
	
	//Data attributes for test suite
	private final String emailDomain = "@afeka.edu";
	private final String userName = "user";
	private final String avatar = "lion";
	private final String playerRole = "player";
	
	private List<NewUserForm> newUserForms;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.userTOComparator = new UserTOComparator();
		this.url = "http://localhost:" + port + "/playground/users";		

		this.jsonMapper = new ObjectMapper();

		System.err.println(this.url);
	}
	
	@Before
	public void setup() {
		this.newUserForms = this.generateNewUserForms(this.numCases);
	}

	@After
	public void teardown() {
		this.usersService.cleanup();
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testServerIsBootingCorrectly() throws Exception {
	}
	
	@Test
	public void testRegisterNewUser() throws JsonParseException, JsonMappingException, IOException {
		UserTO expectedUserTO = new UserTO(this.newUserForms.get(0), this.playground);
		UserTO actualReturnedValue = this.restTemplate.postForObject(this.url, this.newUserForms.get(0), UserTO.class);
		
		assertThat(actualReturnedValue)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testRegisterNewUserWithUndefinedRole() throws JsonParseException, JsonMappingException, IOException {
		NewUserForm newUserWithUndefinedRole = this.newUserForms.get(1);
		newUserWithUndefinedRole.setRole("admin");
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.postForObject(this.url, newUserWithUndefinedRole, UserTO.class);
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testVerifyNewUserAccount() throws JsonParseException, JsonMappingException, IOException, UserAlreadyExistsException, RoleDoesNotExistException {
		NewUserForm newUser = this.newUserForms.get(2);
		UserTO expectedUserTO = new UserTO(this.usersService.createNewUser(
				new UserTO(newUser, playground).toEntity()));

		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + CONFIRM_URL, UserTO.class, 

				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
		
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testVerifyNewUserAccountWithWrongCode() throws JsonParseException, JsonMappingException, IOException, UserAlreadyExistsException, RoleDoesNotExistException {
		//Given
		NewUserForm newUser = this.newUserForms.get(3);
		this.usersService.createNewUser(new UserTO(newUser, playground).toEntity());
		
		String wrongCode = newUser.getEmail() + this.verificationCodeSuffix + "NOPE";
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject( 
				this.url + CONFIRM_URL, UserTO.class, 
				this.playground, newUser.getEmail(), wrongCode);
		
		//Then
		//An HttpServerErrorException is caught with status code 404
	}
	
	@Test
	public void testUserSuccessfulLogin() throws JsonParseException, JsonMappingException, IOException, UserAlreadyExistsException, RoleDoesNotExistException, UserDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {
		//Given
		NewUserForm newUser = this.newUserForms.get(4);
		UserEntity newUserEntity = this.usersService.createNewUser(
				new UserTO(newUser, playground).toEntity());
		this.usersService.verifyUserRegistration(
				playground, newUserEntity.getEmail(), 
				newUserEntity.getEmail() + this.verificationCodeSuffix);
		UserTO expectedUserTO = new UserTO(newUserEntity);
		
		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + LOGIN_URL, UserTO.class, 
				this.playground, newUser.getEmail());
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
		
	}
	
	@Test
	public void testUserLoginWithUnknownEmail() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException, UserAlreadyExistsException, RoleDoesNotExistException {
		//Given
		NewUserForm newUser = this.newUserForms.get(5);
		UserEntity newUserEntity = this.usersService.createNewUser(
				new UserTO(newUser, playground).toEntity());
		this.usersService.verifyUserRegistration(
				playground, newUserEntity.getEmail(), 
				newUserEntity.getEmail() + this.verificationCodeSuffix);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject(
				this.url + LOGIN_URL, UserTO.class, 
				this.playground, newUser.getEmail() + "NOPE");
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When the unverified user tries to login
		this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, newUser.getEmail());
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUnverifiedUserLogin() throws UserAlreadyExistsException, RoleDoesNotExistException, UserDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {
		//Given there is a sign up request
		NewUserForm newUser = this.newUserForms.get(6);
		this.usersService.createNewUser(new UserTO(newUser, playground).toEntity());
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When the unverified user tries to login
		this.restTemplate.getForObject(
				this.url + LOGIN_URL, UserTO.class, 
				this.playground, newUser.getEmail());
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUpdateUserDetailsSuccessfully() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException, UserAlreadyExistsException, RoleDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {
		//Given
		NewUserForm newUser = this.newUserForms.get(7);
		UserEntity newUserEntity = this.usersService.createNewUser(
				new UserTO(newUser, playground).toEntity());
		this.usersService.verifyUserRegistration(
				playground, newUserEntity.getEmail(), 
				newUserEntity.getEmail() + this.verificationCodeSuffix);
		
		UserTO userWithUpdate = new UserTO(newUser, this.playground);
		userWithUpdate.setUsername("leon");
		userWithUpdate.setAvatar("giraffe");
		userWithUpdate.setRole("manager");
		
		//When
		this.restTemplate.put(
				this.url + PREFIX_URL, userWithUpdate, this.playground, 
				userWithUpdate.getEmail());
		
		//Then
		UserEntity actualEntity = usersService.getUserByEmail(userWithUpdate.getEmail());
		
		assertThat(new UserTO(actualEntity))
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(userWithUpdate);
	}
	
	@Test
	public void testUpdateUserDetailsThatAreNotAllowed() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException, UserAlreadyExistsException, RoleDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {
		//Given
		NewUserForm newUser = this.newUserForms.get(8);
		UserEntity newUserEntity = this.usersService.createNewUser(
				new UserTO(newUser, playground).toEntity());
		this.usersService.verifyUserRegistration(
				playground, newUserEntity.getEmail(), 
				newUserEntity.getEmail() + this.verificationCodeSuffix);
		
		UserTO userWithUnAllowedUpdate = new UserTO(newUser, this.playground);
		userWithUnAllowedUpdate.setPoints(1250L);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.put(this.url + PREFIX_URL, userWithUnAllowedUpdate, this.playground, 
				userWithUnAllowedUpdate.getEmail());
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}

	private List<NewUserForm> generateNewUserForms(int numCases) {
		List<NewUserForm> newUserForms = new ArrayList<>();
		for (int i = 0; i < numCases; i++) {
			newUserForms.add(new NewUserForm(
					this.userName + "_" + i + "_" + this.emailDomain
					,this.userName + "_" + i + "_"
					,this.avatar
					,this.playerRole));
		}
		return newUserForms;
	}
}
