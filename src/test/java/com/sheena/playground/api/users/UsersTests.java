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
import org.springframework.web.client.HttpServerErrorException;
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
	@LocalServerPort
	private int port;
	
	private String url;
	
	private RestTemplate restTemplate;
	
	private ObjectMapper jsonMapper;
	
	private Comparator<UserTO> userTOComparator;
	
	@Autowired
	private UsersService usersService;
	
	private String PLAYGROUND;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.userTOComparator = new UserTOComparator();
		this.url = "http://localhost:" + port + "/playground/users";		
		// Jackson init
		this.jsonMapper = new ObjectMapper();
		this.PLAYGROUND = "Sheena.2019A";
		System.err.println(this.url);
	}
	
	@Before
	public void setup() {
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
		//Given
		String newUserString = "{\"email\": \"bison@flex.gnu\", \"username\": \"moshes\", \"avatar\": \"avatar\", \"role\": \"player\"}";

		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		
		UserTO expectedUserTO = new UserTO(newUserPostObject, this.PLAYGROUND);
		UserTO actualReturnedValue = this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		
		assertThat(actualReturnedValue)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testRegisterNewUserWithUndefinedRole() throws JsonParseException, JsonMappingException, IOException {
		//Given
		String newUserString = "{\"email\": \"bison@flex.gnu\", \"username\": \"moshes\", \"avatar\": \"avatar\", \"role\": \"admin\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
				
		this.exception.expect(HttpClientErrorException.class);
		this.exception.expectMessage("404");
		
		//When
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testVerifyNewUserAccount() throws JsonParseException, JsonMappingException, IOException {
		//Given
		String newUserString = "{\"email\": \"bison@yacc.gnu\", \"username\": \"moshe\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		UserTO expectedUserTO = new UserTO(newUserPostObject, this.PLAYGROUND);

		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@yacc.gnu", "bison@yacc.gnu007");
		
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testVerifyNewUserAccountWithWrongCode() throws JsonParseException, JsonMappingException, IOException {
		//Given
		String newUserString = "{\"email\": \"bison@yacc.gnu\", \"username\": \"moshe\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		//When
		this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@yacc.gnu", "bison@yacc.gnu007" + "Bond");
		
		//Then
		//An HttpServerErrorException is caught with status code 500
	}
	
	@Test
	public void testUserSuccessfulLogin() throws JsonParseException, JsonMappingException, IOException {
		//Given
		String newUserString = "{\"email\": \"bison@bison.gnu\", \"username\": \"moshes123\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@bison.gnu", "bison@bison.gnu007");
		
		UserTO expectedUserTO = new UserTO(newUserPostObject, this.PLAYGROUND);
		
		//When
		UserTO returnedAnswer = this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class, "Sheena.2019A", "bison@bison.gnu");
		//Then
		assertThat(returnedAnswer)
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expectedUserTO);
	}
	
	@Test
	public void testUserLoginWithUnknownEmail() throws JsonParseException, JsonMappingException, IOException {
		//Given
		String newUserString = "{\"email\": \"bison@bison.gnu\", \"username\": \"moshes123\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@bison.gnu", "bison@bison.gnu007");
				
		this.exception.expect(HttpClientErrorException.class);
		this.exception.expectMessage("404");
		
		//When
		this.restTemplate.getForObject(this.url + "/login/{playground}/{email}", UserTO.class, "Sheena.2019A", "bison@bison.gnu" + "linux");
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
	
	@Test
	public void testUpdateUserDetailsSuccessfully() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		String newUserString = "{\"email\": \"bison@ubuntu.gnu\", \"username\": \"moshes123\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@ubuntu.gnu", "bison@ubuntu.gnu007");
		
		String updatedUserDetailsString = "{\"email\": \"bison@ubuntu.gnu\", \"playground\": \"Sheena.2019A\", \"username\": \"Leon\", \"avatar\": \"lion\", \"role\": \"player\"}";
		UserTO putObject = this.jsonMapper.readValue(updatedUserDetailsString, UserTO.class);
		
		String userFormString = "{\"email\": \"bison@ubuntu.gnu\", \"username\": \"Leon\", \"avatar\": \"lion\", \"role\": \"player\"}";
		UserTO expected = new UserTO(this.jsonMapper.readValue(userFormString, NewUserForm.class), this.PLAYGROUND);
		
		//When
		this.restTemplate.put(this.url + "/{playground}/{email}", putObject, this.PLAYGROUND, "bison@ubuntu.gnu");
		
		//Then
		UserEntity actualEntity = usersService.getUser("bison@ubuntu.gnu");
		
		assertThat(new UserTO(actualEntity))
		.isNotNull()
		.usingComparator(this.userTOComparator).isEqualTo(expected);
	}
	
	@Test
	public void testUpdateUserDetailsThatAreNotAllowed() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		//Given
		String newUserString = "{\"email\": \"bison@suse.gnu\", \"username\": \"moshes123\", \"avatar\": \"avatar\", \"role\": \"player\"}";
		NewUserForm newUserPostObject = this.jsonMapper.readValue(newUserString, NewUserForm.class);
		this.restTemplate.postForObject(this.url, newUserPostObject, UserTO.class);
		this.restTemplate.getForObject(this.url + "/confirm/{playground}/{email}/{code}", UserTO.class, "Sheena.2019A", "bison@suse.gnu", "bison@suse.gnu007");
		
		String updatedUserDetailsString = "{\"points\": \"1250\"}";
		UserTO putObject = this.jsonMapper.readValue(updatedUserDetailsString, UserTO.class);
				
		this.exception.expect(HttpClientErrorException.class);
		this.exception.expectMessage("404");
		
		//When
		this.restTemplate.put(this.url + "/{playground}/{email}", putObject, this.PLAYGROUND, "bison@ubuntu.gnu");
		
		//Then
		//An HttpClientErrorException is caught with status code 404
	}
}
