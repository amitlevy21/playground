package com.sheena.playground.api.activity;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
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
	
	//Data attributes for test suite
  	private final String emailDomain = "@afeka.edu";
  	private final String userName1 = "user1";
  	private final String userName2 = "user2";
  	private final String avatar = "lion";
  	private final String playerRole = "player";
  	private final String managerRole = "manager";
  	
    private UserEntity managerUser;
    private UserEntity playerUser;
    
    private ElementEntity attendanceClock;
    
    @PostConstruct
    public void init() {
    	this.host = String.format(this.host, this.port);
    	this.restTemplate = new RestTemplate();
    	this.jsonMapper = new ObjectMapper();
    }
	
	@Before
	public void setup() throws UserAlreadyExistsException, RoleDoesNotExistException, ParseException {
		this.managerUser = new UserEntity(userName1+emailDomain, playgroundName, userName1, avatar, managerRole);
		this.playerUser = new UserEntity(userName2+emailDomain, playgroundName, userName2, avatar, playerRole);
		
		this.managerUser.setVerifiedUser(true);
		this.playerUser.setVerifiedUser(true);
		
		// Creating the users already verified - a hack to avoid verification via server
		this.managerUser = this.usersService.createNewUser(this.managerUser);
		this.playerUser = this.usersService.createNewUser(this.playerUser);
		
		Map<String, Object> attendanceClock01Attributes = new HashMap<>();
		attendanceClock01Attributes.put("workDate", new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01"));
		this.attendanceClock = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"attendanceClock01", 
				new Date(), new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"attendanceClock", 
				attendanceClock01Attributes, 
				playgroundName, 
				managerUser.getEmail());
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
}
