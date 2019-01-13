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
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;
import com.sheena.playground.logic.activities.ActivityWithNoTypeException;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.plugins.PostMessagePlugin;
import com.sheena.playground.plugins.RegisterShiftPlugin;
import com.sheena.playground.plugins.ViewMessagesPlugin;
import com.sheena.playground.plugins.messageBoard.BoardMessageResponse;

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
		this.managerUser = new UserEntity(userName1+emailDomain, userName1, avatar, managerRole);
		this.playerUser = new UserEntity(userName2+emailDomain, userName2, avatar, playerRole);
		
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
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.messageBoardElement.getPlayground(), 
				this.messageBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		Object rv = this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
		BoardMessageResponse rvMessage = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), BoardMessageResponse.class);
		
		BoardMessageResponse expectedMessage = new BoardMessageResponse(activityAttributes.get("text")+"", postMessageActivity.getPlayerEmail(), postMessageActivity.getPlayerPlayground());
		
		assertThat(rvMessage).isNotNull().isEqualTo(expectedMessage);
	}
	
	@Test
	public void testPostMessageSuccessfullyAffectsPlayerPoints() throws JsonParseException, JsonMappingException, IOException, UserDoesNotExistException {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes = new HashMap<>();
		activityAttributes.put("text", "text04");
		
		ActivityEntity postMessageActivity = new ActivityEntity(
				this.messageBoardElement.getPlayground(), 
				this.messageBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes);
		
		long playerPointsBefore = usersService.getUserByEmail(this.playerUser.getEmail()).getPoints();
		long expectedPointsAfter = playerPointsBefore + PostMessagePlugin.AWARD_POINTS;
		
		this.restTemplate.postForObject(this.host+targetUrl, postMessageActivity, Object.class);
		
		long playerPointsAfter = usersService.getUserByEmail(this.playerUser.getEmail()).getPoints();
		
		assertThat(playerPointsAfter).isNotNull().isEqualTo(expectedPointsAfter);
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
	public void testViewMessagesSuccessfully() throws ActivityTypeNotAllowedException, ActivityWithNoTypeException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes1 = new HashMap<>();
		activityAttributes1.put("text", "text0301");
		
		ActivityEntity postMessageActivity1 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes1);
		
		Map<String, Object> activityAttributes2 = new HashMap<>();
		activityAttributes2.put("text", "text0302");
		
		ActivityEntity postMessageActivity2 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes2);
		
		Map<String, Object> activityAttributes3 = new HashMap<>();
		activityAttributes3.put("text", "text0303");
		
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
		
		this.activityService.addNewActivity(postMessageActivity1, this.playgroundName, this.playerUser.getEmail());
		this.activityService.addNewActivity(postMessageActivity2, this.playgroundName, this.playerUser.getEmail());
		this.activityService.addNewActivity(postMessageActivity3, this.playgroundName, this.playerUser.getEmail());
		
		int numExpectedMessages = 3;
		
		BoardMessageResponse[] expectedMessages = new BoardMessageResponse[numExpectedMessages];
		
		expectedMessages[0] = new BoardMessageResponse(activityAttributes1.get("text")+"", this.playerUser.getEmail(), this.playerUser.getPlayground());
		expectedMessages[1] = new BoardMessageResponse(activityAttributes2.get("text")+"", this.playerUser.getEmail(), this.playerUser.getPlayground());
		expectedMessages[2] = new BoardMessageResponse(activityAttributes3.get("text")+"", this.playerUser.getEmail(), this.playerUser.getPlayground());
		
		Object[] rv = this.restTemplate.postForObject(this.host+targetUrl, viewMessagesActivity, Object[].class);
		BoardMessageResponse[] rvMessages = this.jsonMapper.readValue(this.jsonMapper.writeValueAsString(rv), BoardMessageResponse[].class);
		
		assertThat(rvMessages).isNotNull().hasSize(numExpectedMessages).isEqualTo(expectedMessages);
	}
	
	@Test
	public void testViewMessagesSuccessfullyAffectsPlayerPoints() throws ActivityTypeNotAllowedException, ActivityWithNoTypeException, JsonParseException, JsonMappingException, JsonProcessingException, IOException, UserDoesNotExistException {
		String targetUrl = String.format(this.url, this.playgroundName, this.playerUser.getEmail());
		
		Map<String, Object> activityAttributes1 = new HashMap<>();
		activityAttributes1.put("text", "text05");
		
		ActivityEntity postMessageActivity1 = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				POST_MESSAGE,
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				activityAttributes1);
		
		ActivityEntity viewMessagesActivity = new ActivityEntity(
				this.viewMessagesBoardElement.getPlayground(), 
				this.viewMessagesBoardElement.getId(), 
				VIEW_MESSAGES, 
				this.playerUser.getPlayground(), 
				this.playerUser.getEmail(), 
				new HashMap<>());
		
		this.activityService.addNewActivity(postMessageActivity1, this.playgroundName, this.playerUser.getEmail());
		
		long playerPointsBefore = usersService.getUserByEmail(this.playerUser.getEmail()).getPoints();
		long expectedPointsAfter = playerPointsBefore + ViewMessagesPlugin.AWARD_POINTS;
		
		this.restTemplate.postForObject(this.host+targetUrl, viewMessagesActivity, Object[].class);
		
		long playerPointsAfter = usersService.getUserByEmail(this.playerUser.getEmail()).getPoints();
		
		assertThat(playerPointsAfter).isNotNull().isEqualTo(expectedPointsAfter);
	}
}
