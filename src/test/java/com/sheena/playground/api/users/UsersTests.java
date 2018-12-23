package com.sheena.playground.api.users;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class UsersTests {
	
	@LocalServerPort
	private int port;
	
	private String url;
	private RestTemplate restTemplate;
	@SuppressWarnings("unused")
	private ObjectMapper jsonMapper;
	private Comparator<UserTO> userTOComparator;
	
	@Autowired
	private UsersService usersService;
	
	private String playground;
	private String verificationCodeSuffix;
	
	//Data attributes for test suite
	private NewUserForm newUser; 
	private NewUserForm newUserUndefinedRole;
	private UserTO userWithUpdate;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.userTOComparator = new UserTOComparator();
		this.url = "http://localhost:" + port + "/playground/users";		
		this.jsonMapper = new ObjectMapper(); // Jackson init
		this.playground = "Sheena.2019A";
		this.verificationCodeSuffix = "code";
		System.err.println(this.url);
	}
	
	@Before
	public void setup() {
		this.newUser = new NewUserForm("moshe@afeka.edu", "moshe", "lion", "player");
		this.newUserUndefinedRole = new NewUserForm("moshe@afeka.edu", "moshe", "lion", "admin");
		
		this.userWithUpdate = new UserTO(this.newUser, this.playground);
		this.userWithUpdate.setUsername("leon");
		this.userWithUpdate.setAvatar("giraffe");
		this.userWithUpdate.setRole("manager");
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
		UserTO expectedUserTO = new UserTO(this.newUser, this.playground);
		UserTO actualReturnedValue = this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		
		assertThat(actualReturnedValue)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testRegisterNewUserWithUndefinedRole() throws JsonParseException, JsonMappingException, IOException {
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.postForObject(this.url, this.newUserUndefinedRole, UserTO.class);
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testVerifyNewUserAccount() throws JsonParseException, JsonMappingException, IOException {
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		UserTO expectedUserTO = new UserTO(this.newUser, this.playground);

		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), 
				this.newUser.getEmail() + this.verificationCodeSuffix);
		
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testVerifyNewUserAccountWithWrongCode() throws JsonParseException, JsonMappingException, IOException {
		//Given
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		
		String wrongCode = this.newUser.getEmail() + this.verificationCodeSuffix + "NOPE";
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject( 
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), wrongCode);
		
		//Then
		//An HttpServerErrorException is caught with status code 404
	}
	
	@Test
	public void testUserSuccessfulLogin() throws JsonParseException, JsonMappingException, IOException {
		//Given
		
		
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), 
				this.newUser.getEmail() + this.verificationCodeSuffix);
		
		UserTO expectedUserTO = new UserTO(this.newUser, this.playground);
		
		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, this.newUser.getEmail());
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
		
	}
	
	@Test
	public void testUserLoginWithUnknownEmail() throws JsonParseException, JsonMappingException, IOException {
		//Given
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), 
				this.newUser.getEmail() + this.verificationCodeSuffix);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, this.newUser.getEmail() + "NOPE");
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUpdateUserDetailsSuccessfully() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), 
				this.newUser.getEmail() + this.verificationCodeSuffix);
		
		//When
		this.restTemplate.put(
				this.url + "/{playground}/{email}", this.userWithUpdate, this.playground, 
				this.userWithUpdate.getEmail());
		
		//Then
		UserEntity actualEntity = usersService.getUserByEmail(this.userWithUpdate.getEmail());
		
		assertThat(new UserTO(actualEntity))
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(this.userWithUpdate);
	}
	
	@Test
	public void testUpdateUserDetailsThatAreNotAllowed() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		this.restTemplate.postForObject(this.url, this.newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, this.newUser.getEmail(), 
				this.newUser.getEmail() + this.verificationCodeSuffix);
		
		UserTO userWithUnAllowedUpdate = new UserTO(this.userWithUpdate);
		userWithUnAllowedUpdate.setPoints(1250L);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.put(this.url + "/{playground}/{email}", userWithUnAllowedUpdate, this.playground, 
				userWithUnAllowedUpdate.getEmail());
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
}
