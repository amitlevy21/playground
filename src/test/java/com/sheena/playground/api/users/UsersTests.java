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
import com.sheena.playground.logic.users.UserDoesNotExistException;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class UsersTests {
	
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
		this.jsonMapper = new ObjectMapper(); // Jackson init
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
	public void testVerifyNewUserAccount() throws JsonParseException, JsonMappingException, IOException {
		NewUserForm newUser = this.newUserForms.get(2);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		UserTO expectedUserTO = new UserTO(newUser, this.playground);

		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
		
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testVerifyNewUserAccountWithWrongCode() throws JsonParseException, JsonMappingException, IOException {
		//Given
		NewUserForm newUser = this.newUserForms.get(3);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		
		String wrongCode = newUser.getEmail() + this.verificationCodeSuffix + "NOPE";
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject( 
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), wrongCode);
		
		//Then
		//An HttpServerErrorException is caught with status code 404
	}
	
	@Test
	public void testUserSuccessfulLogin() throws JsonParseException, JsonMappingException, IOException {
		//Given
		NewUserForm newUser = this.newUserForms.get(4);
		
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
		
		UserTO expectedUserTO = new UserTO(newUser, this.playground);
		
		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, newUser.getEmail());
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
		
	}
	
	@Test
	public void testUserLoginWithUnknownEmail() throws JsonParseException, JsonMappingException, IOException {
		//Given
		NewUserForm newUser = this.newUserForms.get(5);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, newUser.getEmail() + "NOPE");
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUnverifiedUserLogin() {
		//Given there is a sign up request
		NewUserForm newUser = this.newUserForms.get(6);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		
		this.exception.expect(HttpClientErrorException.class);
		
		//When the unverified user tries to login
		this.restTemplate.getForObject(
				this.url + "/login/{playground}/{email}", UserTO.class, 
				this.playground, newUser.getEmail());
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUpdateUserDetailsSuccessfully() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		NewUserForm newUser = this.newUserForms.get(7);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
		
		UserTO userWithUpdate = new UserTO(newUser, this.playground);
		userWithUpdate.setUsername("leon");
		userWithUpdate.setAvatar("giraffe");
		userWithUpdate.setRole("manager");
		
		//When
		this.restTemplate.put(
				this.url + "/{playground}/{email}", userWithUpdate, this.playground, 
				userWithUpdate.getEmail());
		
		//Then
		UserEntity actualEntity = usersService.getUserByEmail(userWithUpdate.getEmail());
		
		assertThat(new UserTO(actualEntity))
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(userWithUpdate);
	}
	
	@Test
	public void testUpdateUserDetailsThatAreNotAllowed() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		NewUserForm newUser = this.newUserForms.get(8);
		this.restTemplate.postForObject(this.url, newUser, UserTO.class);
		this.restTemplate.getForObject(
				this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, 
				this.playground, newUser.getEmail(), 
				newUser.getEmail() + this.verificationCodeSuffix);
		
		UserTO userWithUnAllowedUpdate = new UserTO(newUser, this.playground);
		userWithUnAllowedUpdate.setPoints(1250L);
				
		this.exception.expect(HttpClientErrorException.class);
		
		//When
		this.restTemplate.put(this.url + "/{playground}/{email}", userWithUnAllowedUpdate, this.playground, 
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
