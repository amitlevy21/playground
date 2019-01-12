package com.sheena.playground.api.elements;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.Location;
import com.sheena.playground.dal.VerificationCodeDao;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;
import com.sheena.playground.logic.users.UserEntity;
import com.sheena.playground.logic.users.UsersService;
import com.sheena.playground.logic.users.exceptions.CodeDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.RoleDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyExistsException;
import com.sheena.playground.logic.users.exceptions.UserAlreadyVerifiedException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.users.exceptions.VerificationCodeMismatchException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementRestControllerTest {

    private static final int NUM_TESTS = 30;
	private static final String ELEMENT_TYPE = "type";
	private static final String ELEMENT_NAME = "element";
	private static final String BASE_URL = "%s/playground/elements/%s/%s";
	
	@Value("${playground.name:defaultPlayground}")
    private String playgroundName;

	@LocalServerPort
    private int port;

    private String host;

    private RestTemplate restTemplate;

    private ObjectMapper jsonMapper;

    @Autowired
    private ElementService elementService;
    
    @Autowired
	private UsersService usersService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
  //Data attributes for test suite
  	private final String emailDomain = "@afeka.edu";
  	private final String userName1 = "user1";
  	private final String userName2 = "user2";
  	private final String avatar = "lion";
  	private final String playerRole = "player";
  	private final String managerRole = "manager";
  	
    private UserEntity managerUser;
    private UserEntity playerUser;
    private List<ElementTO> elementTOs;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.host = "http://localhost:" + port;
        //System.err.println(this.url);

        // Jackson init
        this.jsonMapper = new ObjectMapper();
    }

    @Before
    public void setup() throws UserAlreadyExistsException, RoleDoesNotExistException, UserDoesNotExistException, CodeDoesNotExistException, UserAlreadyVerifiedException, VerificationCodeMismatchException {
        //Manager role user and player role user for tests
    	this.managerUser = new UserEntity(userName1 + emailDomain, playgroundName, userName1, avatar, managerRole);
    	this.playerUser = new UserEntity(userName2 + emailDomain, playgroundName, userName2, avatar, playerRole);
    	
    	//Add the users to the server
    	UserEntity manager = usersService.createNewUser(this.managerUser);
    	UserEntity player = usersService.createNewUser(this.playerUser);
    	
    	//Verify the users registration
    	usersService.verifyUserRegistration(playgroundName, manager.getEmail(), manager.getEmail() + VerificationCodeDao.SUFFIX);
    	usersService.verifyUserRegistration(playgroundName, player.getEmail(), player.getEmail() + VerificationCodeDao.SUFFIX);
    	
    	//Generate ElementTOs for all tests
    	this.elementTOs = generateElementTOs(NUM_TESTS);
    }
    @After
    public void teardown() {
        this.elementService.cleanup();
        this.usersService.cleanup();
    }
    
    @Test
    public void testAddNewElementSuccessfully() {
    	ElementTO e = this.elementTOs.get(0);
    	
    	String requestUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail());
    	ElementTO returnValue = this.restTemplate.postForObject(requestUrl, e, ElementTO.class);
    	
    	assertThat(returnValue).isNotNull().isEqualToIgnoringGivenFields(e, "id", "playground");
    }

    @Test
    public void testUpdateElementSuccessfully() throws InvalidExpirationDateException, ElementNotExistException {
        	ElementTO e = this.elementTOs.get(1);
        	ElementEntity entity = elementService.addNewElement(managerUser.getEmail(), e.toEntity());
        	
        	e.setLocation(new Location(100.0, 100.0));
        	
        	String targetUrl = String.format(BASE_URL + "/%s/%s", host, playgroundName, managerUser.getEmail(), entity.getCreatorPlayground(), entity.getId());
        	
        	this.restTemplate.put(targetUrl, e);
        	
        	entity = elementService.getElementById(entity.getId());
        	
        	assertThat(entity).isNotNull().extracting("x", "y").containsExactly(100.0, 100.0);
    }
	
	@Test
    public void testGetElementByItsIDSuccessfully() throws InvalidExpirationDateException {
    	ElementTO e = this.elementTOs.get(2);
    	ElementEntity entity = elementService.addNewElement(managerUser.getEmail(), e.toEntity());
    	
    	String actualElementId = entity.getId();
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/{playground}/{id}";
    	ElementTO returned = restTemplate.getForObject(targetUrl, ElementTO.class, playgroundName, actualElementId);
    	
    	assertThat(returned).isNotNull().extracting("id").containsExactly(actualElementId);
    }

    @Test
    public void testGetElementByItsIDWhenIDDoesNotExist() throws HttpServerErrorException {
    	String nonExistentId = "id";
    	
        thrown.expect(HttpServerErrorException.class);
        thrown.expectMessage("500");

        // When
        String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/{playground}/{id}";
        this.restTemplate.getForObject(targetUrl, ElementTO.class, playgroundName, nonExistentId);
    }

    @Test
    public void testGetAllElementsSuccesfully() throws InvalidExpirationDateException {
    	ElementTO e1 = this.elementTOs.get(3);
    	ElementTO e2 = this.elementTOs.get(4);
    	ElementTO e3 = this.elementTOs.get(5);
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	ElementEntity entity3 = elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	ElementTO[] expected = new ElementTO[3];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	expected[2] = new ElementTO(entity3);
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/all";
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(3).isEqualTo(expected);
    }

    @Test
    public void testGetElementByCoordinates() throws InvalidExpirationDateException {
    	ElementTO e1 = this.elementTOs.get(6);
    	ElementTO e2 = this.elementTOs.get(7);
    	ElementTO e3 = this.elementTOs.get(8);
    	
    	e1.setLocation(new Location(10., 20.));
    	e2.setLocation(new Location(15., 25.));
    	e3.setLocation(new Location(100., 200.));
    	
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	ElementTO[] expected = new ElementTO[2];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	
    	double xGiven = 5;
    	double yGiven = 5;
    	double distance = 30;
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/near/{x}/{y}/{distance}";
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class, xGiven, yGiven, distance);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(2).isEqualTo(expected);
    }

    @Test
    public void testGetElementByNameSuccessfully() throws InvalidExpirationDateException {
    	ElementTO e1 = this.elementTOs.get(9);
    	ElementTO e2 = this.elementTOs.get(10);
    	ElementTO e3 = this.elementTOs.get(11);
    	
    	String searchedAttribute = "name";
    	String searchedValue = "foo";
    	String wrongValue = "moo";
    	
    	e1.setName(searchedValue);
    	e2.setName(searchedValue);
    	e3.setName(wrongValue);
    	
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	ElementTO[] expected = new ElementTO[2];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/search/{attributeName}/{value}";
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class, searchedAttribute, searchedValue);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(2).isEqualTo(expected);
    }
    
    @Test
    public void testGetElementByTypeSuccessfully() throws InvalidExpirationDateException {
    	ElementTO e1 = this.elementTOs.get(12);
    	ElementTO e2 = this.elementTOs.get(13);
    	ElementTO e3 = this.elementTOs.get(14);
    	
    	String searchedAttribute = "type";
    	String searchedValue = "cow";
    	String wrongValue = "chicken";
    	
    	e1.setType(searchedValue);
    	e2.setType(searchedValue);
    	e3.setType(wrongValue);
    	
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	ElementTO[] expected = new ElementTO[2];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/search/{attributeName}/{value}";
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class, searchedAttribute, searchedValue);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(2).isEqualTo(expected);
    }
    
    @Test
    public void testGetElementByAttributeThatIsNotSupported() throws InvalidExpirationDateException {
    	ElementTO e1 = this.elementTOs.get(15);
    	
    	String searchedAttribute = "horse";
    	String searchedValue = "black";
    	
    	elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	
    	thrown.expect(HttpServerErrorException.class);
    	thrown.expectMessage("500");
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, managerUser.getEmail()) + "/search/{attributeName}/{value}";
    	restTemplate.getForObject(targetUrl, ElementTO[].class, searchedAttribute, searchedValue);
    }
    
    @Test
	public void testUserWithPlayerRoleCreateElement() throws InvalidExpirationDateException {
		ElementTO e = this.elementTOs.get(16);
		
		thrown.expect(HttpServerErrorException.class);
		thrown.expectMessage("500");
		
		String targetUrl = String.format(BASE_URL, host, playgroundName, playerUser.getEmail());
		restTemplate.postForObject(targetUrl, e, ElementTO.class);
	}

	@Test
	public void testUserWithPlayerRoleUpdateElement() throws InvalidExpirationDateException, ElementNotExistException {
	    	ElementTO e = this.elementTOs.get(17);
	    	ElementEntity entity = elementService.addNewElement(managerUser.getEmail(), e.toEntity());
	    	
	    	e.setLocation(new Location(100.0, 100.0));
	    	
	    	String targetUrl = String.format(BASE_URL + "/%s/%s", host, playgroundName, playerUser.getEmail(), entity.getCreatorPlayground(), entity.getId());
	    	
	    	thrown.expect(HttpServerErrorException.class);
			thrown.expectMessage("500");
	    	
	    	this.restTemplate.put(targetUrl, e);
	}
	
	@Test
	public void testUserWithPlayerRoleDoesNotGetExpiredElementsWhenGettingAllElements() throws InvalidExpirationDateException, ParseException {
		ElementTO e1 = this.elementTOs.get(18);
    	ElementTO e2 = this.elementTOs.get(19);
    	ElementTO e3 = this.elementTOs.get(20);
    	
    	e1.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"));
    	e2.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2090-11-18"));
    	e3.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-10-09"));
    	
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	int numExpected = 2;
    	
		ElementTO[] expected = new ElementTO[numExpected];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, playerUser.getEmail()) + "/all";
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(numExpected).isEqualTo(expected);
	}
	
	@Test
	public void testUserWithPlayerRoleDoesNotGetExpiredElementsWhenGettingElementsNearCoordinates() throws InvalidExpirationDateException, ParseException {
		ElementTO e1 = this.elementTOs.get(21);
    	ElementTO e2 = this.elementTOs.get(22);
    	ElementTO e3 = this.elementTOs.get(23);
    	
    	e1.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2070-01-01"));
    	e2.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2090-11-18"));
    	e3.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-10-09"));
    	
    	e1.setLocation(new Location(10., 20.));
    	e2.setLocation(new Location(15., 25.));
    	e3.setLocation(new Location(100., 200.));
    	
    	ElementEntity entity1 = elementService.addNewElement(managerUser.getEmail(), e1.toEntity());
    	ElementEntity entity2 = elementService.addNewElement(managerUser.getEmail(), e2.toEntity());
    	elementService.addNewElement(managerUser.getEmail(), e3.toEntity());
    	
    	int numExpected = 2;
    	
    	double xGiven = 5;
    	double yGiven = 5;
    	double distance = 30;
    	
		ElementTO[] expected = new ElementTO[numExpected];
    	expected[0] = new ElementTO(entity1);
    	expected[1] = new ElementTO(entity2);
    	
    	String targetUrl = String.format(BASE_URL, host, playgroundName, playerUser.getEmail()) + "/near/{x}/{y}/{distance}";;
    	ElementTO[] allElementsReturned = restTemplate.getForObject(targetUrl, ElementTO[].class, xGiven, yGiven, distance);
    	
    	assertThat(allElementsReturned).isNotNull().hasSize(numExpected).isEqualTo(expected);
	}

	private List<ElementTO> generateElementTOs(int numCases) {
		List<ElementTO> tos = new ArrayList<>();
		
		for (int i = 0; i < numCases; i++) {
			tos.add(new ElementTO(
					new Location(), 
					ELEMENT_NAME + "_" + i, 
					new Date(), 
					new Date(), 
					ELEMENT_TYPE + "_" + i, 
					new HashMap<String, Object>(), 
					playgroundName, "dummy@email.com"));
		}
		
		return tos;
	}
}