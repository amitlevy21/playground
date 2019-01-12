package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.plugins.shiftRegistery.ShiftResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ShiftRegisteryPluginTests {

	@Autowired
	private UsersService usersService;

	@Autowired
	private ElementService elementService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivityDao activityDao;

	@LocalServerPort
	private int port;

	@Value("${playground.name:defaultPlayground}")
	private String playgroundName;

	private final String url = "/playground/activities/%s/%s"; // /playground/activities/{userPlayground}/{email}
	private String host = "http://localhost:%s";
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;
	private final int pageableSize = 10;
	private final int pageablePage = 0;
	
	//Data attributes for test suite
  	private final String emailDomain = "@afeka.edu";
  	private final String userName1 = "user1";
  	private final String userName2 = "user2";
  	private final String avatar = "lion";
  	private final String playerRole = "player";
  	private final String managerRole = "manager";

  	private final String REGISTER_SHIFT = "RegisterShift";
  	
    private UserEntity managerUser;
    private UserEntity playerUser;
    
    private ElementEntity shiftRegisteryElement;
    private ElementEntity wrongTypeShiftRegisteryElement;
    
    
    @PostConstruct
    public void init() {
    	this.host = String.format(this.host, this.port);
    	this.restTemplate = new RestTemplate();
    	this.jsonMapper = new ObjectMapper();
    }
    
    @Before
    public void setup() throws UserAlreadyExistsException, RoleDoesNotExistException, ParseException {
    	this.managerUser = new UserEntity(userName1+emailDomain, userName1, avatar, managerRole);
		this.playerUser = new UserEntity(userName2+emailDomain, userName2, avatar, playerRole);
		
		this.managerUser.setVerifiedUser(true);
		this.playerUser.setVerifiedUser(true);
		
		// Creating the users already verified - a hack to avoid verification via server
		this.managerUser = this.usersService.createNewUser(this.managerUser);
		this.playerUser = this.usersService.createNewUser(this.playerUser);
		
		Map<String, Object> shiftRegisteryAttributes = new HashMap<>();
		shiftRegisteryAttributes.put("shiftDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-13"));
		shiftRegisteryAttributes.put("maxWorkersInShift", 1);
		
		this.shiftRegisteryElement = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"shiftRegistery01", 
				new Date(), 
				new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"shift", 
				shiftRegisteryAttributes, 
				this.playgroundName, 
				this.managerUser.getEmail());
		
		this.wrongTypeShiftRegisteryElement = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"shiftRegistery02", 
				new Date(), 
				new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"shiftRegisteryWrong", 
				shiftRegisteryAttributes, 
				this.playgroundName, 
				this.managerUser.getEmail());
				
		this.shiftRegisteryElement = this.elementService.addNewElement(this.managerUser.getEmail(), this.shiftRegisteryElement);
		this.wrongTypeShiftRegisteryElement = this.elementService.addNewElement(this.managerUser.getEmail(), this.wrongTypeShiftRegisteryElement);
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
	public void testRegisterShiftSuccessfully() throws Exception {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		activityAttributes.put("wantedShiftDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-13"));

		ActivityEntity registerShiftActivity = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, registerShiftActivity, Object.class);
		
		ShiftResponse rvShift = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), ShiftResponse.class);
		
		ActivityEntity expectedEntity = this.activityDao.findActivityByElementId(this.shiftRegisteryElement.getId(), PageRequest.of(this.pageablePage, this.pageableSize)).get(0);
		
		ShiftResponse expectedShiftResponse = (ShiftResponse) expectedEntity.getResponse()[0]; 

		assertThat(rvShift).isNotNull().isEqualTo(expectedShiftResponse);
	}
	
	@Test
	public void testRegisterShiftAndNoShiftIsExistedInThisDate() throws Exception {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		activityAttributes.put("wantedShiftDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-14"));
		
		ActivityEntity registerShiftActivity = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, registerShiftActivity, Object.class);
	}
	
	@Test
	public void testRegisterShiftWithWrongActivityType() throws Exception {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		activityAttributes.put("wantedShiftDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-14"));
		
		ActivityEntity registerShiftActivity = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT + "Worng",
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, registerShiftActivity, Object.class);
	}
	
	@Test
	public void testRegisterShiftWithEmptyActivityAttributes() throws Exception {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		ActivityEntity registerShiftActivity = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, registerShiftActivity, Object.class);
	}
	
	
	@Test
	public void testRegisterShiftWithNoSptsLeft() throws Exception {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		activityAttributes.put("wantedShiftDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-13"));

		ActivityEntity registerShiftActivity = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		Object rv1 = this.restTemplate.postForObject(this.host+targetUrl, registerShiftActivity, Object.class);
		
		UserEntity playerUser2 = new UserEntity(userName2+"_"+emailDomain, userName2, avatar, playerRole);
		playerUser2.setVerifiedUser(true);
		
		// Creating the user already verified - a hack to avoid verification via server
		playerUser2 = this.usersService.createNewUser(playerUser2);
		String targetUrl2 = String.format(this.url, this.playgroundName, playerUser2.getEmail());
		
		ActivityEntity registerShiftActivity2 = new ActivityEntity(
				this.shiftRegisteryElement.getPlayground(), 
				this.shiftRegisteryElement.getId(), 
				REGISTER_SHIFT,
				playerUser2.getPlayground(), 
				playerUser2.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		Object rv2 = this.restTemplate.postForObject(this.host+targetUrl2, registerShiftActivity2, Object.class);
	}
}