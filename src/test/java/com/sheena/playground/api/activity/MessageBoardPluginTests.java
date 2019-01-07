package com.sheena.playground.api.activity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotSupportedException;
import com.sheena.playground.logic.activities.ActivityWithNoTypeException;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.plugins.messageBoard.BoardMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MessageBoardPluginTests {

	private final String VIEW_MESSAGES = "ViewMessages";

	private final String POST_MESSAGE = "PostMessage";

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
	private final int pageableSize = 10;
	private final int pageablePage = 0;
	
	private String host = "http://localhost:%s";
	private RestTemplate restTemplate;
	private ObjectMapper jsonMapper;
	
	//Data attributes for test suite
  	private final String emailDomain = "@afeka.edu";
  	private final String userName1 = "user1";
  	private final String userName2 = "user2";
  	private final String avatar = "lion";
  	private final String playerRole = "player";
  	private final String managerRole = "manager";
  	
    private UserEntity managerUser;
    private UserEntity playerUser;
    
    private ElementEntity messageBoardElement;
    private ElementEntity wrongTypeMessageBoardElement;
    private ElementEntity viewMessagesBoardElement;
    
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
		
		this.messageBoardElement = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"messageBoard01", 
				new Date(), 
				new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"messageBoard", 
				new HashMap<>(), 
				this.playgroundName, 
				this.managerUser.getEmail());
		
		this.wrongTypeMessageBoardElement = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"messageBoard02", 
				new Date(), 
				new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"messageBoardWrong", 
				new HashMap<>(), 
				this.playgroundName, 
				this.managerUser.getEmail());
		
		this.viewMessagesBoardElement = new ElementEntity(
				playgroundName, 
				0.0, 0.0, 
				"messageBoard03", 
				new Date(), 
				new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"), 
				"messageBoard", 
				new HashMap<>(), 
				this.playgroundName, 
				this.managerUser.getEmail());
		
		this.messageBoardElement = this.elementService.addNewElement(this.managerUser.getEmail(), this.messageBoardElement);
		this.wrongTypeMessageBoardElement = this.elementService.addNewElement(this.managerUser.getEmail(), this.wrongTypeMessageBoardElement);
		this.viewMessagesBoardElement = this.elementService.addNewElement(this.managerUser.getEmail(), this.viewMessagesBoardElement);
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
	public void testPostMessageSuccessfully() throws JsonParseException, JsonMappingException, IOException {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		activityAttributes.put("text", "text01");
		activityAttributes.put("publisherEmail", "publisherEmail01");
		activityAttributes.put("publisherPlayground", "publisherPlayground01");
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.messageBoardElement.getPlayground(), 
				this.messageBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
		BoardMessage rvMessage = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), BoardMessage.class);
		
		ActivityEntity expectedEntity = this.activityDao.findActivityByElementId(this.messageBoardElement.getId(), PageRequest.of(this.pageablePage, this.pageableSize)).toArray(new ActivityEntity[0])[0];
		
		BoardMessage expectedMessage = new BoardMessage(expectedEntity.getAttributes().get("text")+"", expectedEntity.getAttributes().get("publisherEmail")+"", expectedEntity.getAttributes().get("publisherPlayground")+"");
		
		assertThat(rvMessage).isNotNull().isEqualTo(expectedMessage);
	}
	
	@Test
	public void testPostMessageWithWrongElementType() {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		activityAttributes.put("text", "text02");
		activityAttributes.put("publisherEmail", "publisherEmail02");
		activityAttributes.put("publisherPlayground", "publisherPlayground02");
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.wrongTypeMessageBoardElement.getPlayground(), 
				this.wrongTypeMessageBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
	}
	
	@Test
	public void testPostMessageWithWrongActivityType() {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		activityAttributes.put("text", "text03");
		activityAttributes.put("publisherEmail", "publisherEmail03");
		activityAttributes.put("publisherPlayground", "publisherPlayground03");
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.messageBoardElement.getPlayground(), 
				this.messageBoardElement.getId(), 
				POST_MESSAGE + "Wrong",
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		this.exception.expect(HttpServerErrorException.class);
		this.exception.expectMessage("500");
		
		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
	}
	
	@Test
	public void testPostMessageWithEmptyActivityAttributes() {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.messageBoardElement.getPlayground(), 
				this.messageBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
		
		
	}
	
	@Test
	public void testViewMessagesSuccessfully() throws ActivityTypeNotSupportedException, ActivityWithNoTypeException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes1 = new HashMap<>();
		activityAttributes1.put("text", "text0301");
		activityAttributes1.put("publisherEmail", "publisherEmail0301");
		activityAttributes1.put("publisherPlayground", "publisherPlayground0301");
		
		ActivityEntity postMessageActivity1 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes1);
		
		Map<String, Object> activityAttributes2 = new HashMap<>();
		activityAttributes2.put("text", "text0302");
		activityAttributes2.put("publisherEmail", "publisherEmail0302");
		activityAttributes2.put("publisherPlayground", "publisherPlayground0302");
		
		ActivityEntity postMessageActivity2 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes2);
		
		Map<String, Object> activityAttributes3 = new HashMap<>();
		activityAttributes3.put("text", "text0303");
		activityAttributes3.put("publisherEmail", "publisherEmail0303");
		activityAttributes3.put("publisherPlayground", "publisherPlayground0303");
		
		ActivityEntity postMessageActivity3 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes3);
		
		ActivityEntity viewMessagesActivity = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				VIEW_MESSAGES, 
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				new HashMap<>());
		
//		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity1, Object.class);
//		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity2, Object.class);
//		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity3, Object.class);
		
		this.activityService.addNewActivity(postMessageActivity1, this.playgroundName, this.playerUser.getEmail());
		this.activityService.addNewActivity(postMessageActivity2, this.playgroundName, this.playerUser.getEmail());
		this.activityService.addNewActivity(postMessageActivity3, this.playgroundName, this.playerUser.getEmail());
		
		int numExpectedMessages = 3;
		
		BoardMessage[] expectedMessages = new BoardMessage[numExpectedMessages];
		
		expectedMessages[0] = new BoardMessage(activityAttributes1.get("text")+"", activityAttributes1.get("publisherEmail")+"", activityAttributes1.get("publisherPlayground")+"");
		expectedMessages[1] = new BoardMessage(activityAttributes2.get("text")+"", activityAttributes2.get("publisherEmail")+"", activityAttributes2.get("publisherPlayground")+"");
		expectedMessages[2] = new BoardMessage(activityAttributes3.get("text")+"", activityAttributes3.get("publisherEmail")+"", activityAttributes3.get("publisherPlayground")+"");
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, viewMessagesActivity, Object.class);
		BoardMessage[] rvMessages = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), BoardMessage[].class);
		
//		BoardMessage[] rv = activityDao.findActivityByElementId(this.viewMessagesBoardElement.getId(), PageRequest.of(this.pageablePage, this.pageableSize)).toArray(new BoardMessage[0]);
		
		assertThat(rvMessages).isNotNull().hasSize(numExpectedMessages).isEqualTo(expectedMessages);
	}
}
