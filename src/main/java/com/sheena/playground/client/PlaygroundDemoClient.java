package com.sheena.playground.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sheena.playground.api.ActivityTO;
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.api.Location;
import com.sheena.playground.api.NewUserForm;
import com.sheena.playground.api.UserTO;
import com.sheena.playground.logic.users.Roles;

public class PlaygroundDemoClient {

	private RestTemplate rest;
	private String host;
	private int port;
	private String url;
	private Scanner s;

	// Finals resources

	// Playground
	private final String PLAYGROUND = "Sheena.2019A";

	// Users
	private final String USERS_MAIN_URL = "/playground/users";
	private final String LOGIN_URL = "/playground/users/login/{playground}/{email}";

	// Elements
	private final String ELEMENTS_GET_ALL_URL = "/playground/elements/{userPlayground}/{email}/all";
	private final String ELEMENTS_GET_NEAR_URL = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}";
	private final String ELEMENTS_BY_ATTRIBUTES = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}";
	private final String ELEMENTS_BY_ID = "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
	private final String ELEMENTS_UPDATE = "/playground/elements/{userPlayground}/{email}/{playground}/{id}";
	private final String ELEMENTS_CREATE = "/playground/elements/{userPlayground}/{email}";

	// Activities
	private final String ACTIVITIES_URL = "/playground/activities/{userPlayground}/{email}";

	public PlaygroundDemoClient() {
	}

	public PlaygroundDemoClient(String host, int port) {
		this.rest = new RestTemplate();
		this.host = host;
		this.port = port;
		this.url = "http://localhost:" + port;
		this.s = new Scanner(System.in);
	}

	// -Dplayground.port=8083 -Dplayground.host=172.20.54.120
	public static void main(String[] args) {
		String host = System.getProperty("playground.host");
		if (host == null) {
			host = "localhost";
		}

		int port;
		try {
			port = Integer.parseInt(System.getProperty("playground.port"));
		} catch (Exception e) {
			port = 8080;
		}

		PlaygroundDemoClient client = new PlaygroundDemoClient(host, port);
		System.out.println("Hello, welcome to shift management system");
		while (true) {
			client.firstScreen();
		}

	}

	private void firstScreen() {

		System.out.println("Please choose an operation to do (1 / 2)");
		System.out.println("1: Register to the system");
		System.out.println("2: Login to the system");

		String op = s.nextLine();

		switch (op) {
		case "1":
			registerNewUser();
			break;
		case "2":
			operationScreen(loginSystem());
			break;
		default:
			System.out.println("Invalid opeartion, please try again.");
			break;
		}
	}

	private void operationScreen(UserTO user) {
		int count = 0;
		String op;
		String key;
		System.out.println("Hello " + user.getUsername());
		System.out.println("Opeartion menu:");
		System.out.println("Logout: enter 'x'");
		System.out.println((++count) + ": Get all elements");
		System.out.println((++count) + ": Get elements by distance");
		System.out.println((++count) + ": Get elements by attribute");
		System.out.println((++count) + ": Get elements by id");

		if (user.getRole().equalsIgnoreCase(Roles.PLAYER.toString())) {
			System.out.println((++count) + ": Add new activity");

			op = s.nextLine();
			key = op;
			if (op.equals(count + "")) {
				key = op + "Player";
			}
		} else {
			System.out.println((++count) + ": Create element");
			System.out.println((++count) + ": Update element");

			op = s.nextLine();
			key = op;
			if (op.equals((count - 1) + "") || op.equals(count + "")) {
				key = op + "Manager";
			}
		}

		switch (key) {
		case "1":
			getAllElements(user);
			break;
		case "2":
			GetElementsByDistance(user);
			break;
		case "3":
			getElementsByAttribute(user);
			break;
		case "4":
			getElementsById(user);
			break;
		case "5Player":
			playerAddNewActivity(user);
			break;
		case "5Manager":
			managerCreateElement(user);
			break;
		case "6Manager":
			managerUpdateElement(user);
			break;
		case "X":
		case "x":
			System.out.println("Logging out. Good bye " + user.getUsername());
			break;
		default:
			System.out.println("Invalid opeartion, logging out.");
			break;
		}

	}

	private void managerUpdateElement(UserTO user) {
		System.out.println("Please enter element's id: ");
		String id = s.nextLine();
		ElementTO elementFromUser = getElementFromUser(user);
		this.rest.put(this.url + ELEMENTS_UPDATE, elementFromUser, ElementTO.class, user.getPlayground(),
				user.getEmail(), PLAYGROUND, id);

		System.out.println("Updated succesfully!");
	}

	private void managerCreateElement(UserTO user) {
		ElementTO elementFromUser = getElementFromUser(user);
		Object res = this.rest.postForObject(this.url + ELEMENTS_CREATE, elementFromUser, ElementTO.class,
				user.getPlayground(), user.getEmail());

		System.out.println(res);
	}

	private ElementTO getElementFromUser(UserTO user) {
		Location location;
		String name;
		Date creationDate = new Date();
		Date expirationDate = new Date();
		String type;
		Double x, y;
		String creatorPlayground = user.getPlayground();
		String creatorEmail = user.getEmail();
		String format = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Map<String, Object> attributes = new HashMap<>();
		boolean toContinue = true;
		String loopRes;
		String key, value;

		System.out.println("Please enter element's name:");
		name = s.nextLine();

		System.out.println("Please enter element's type:");
		type = s.nextLine();

		System.out.println("Please enter an expiration date for the element: (" + format + "):");
		String date = s.nextLine();
		try {
			expirationDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("Please enter X coordinate:");
		x = Double.parseDouble(s.nextLine());
		System.out.println("Please enter Y coordinate:");
		y = Double.parseDouble(s.nextLine());
		location = new Location(x, y);

		System.out.println("Please enter keys and values");
		do {
			System.out.println("Do you want adding to the map? (Y / N)");
			loopRes = s.nextLine();
			if (loopRes.equalsIgnoreCase("Y")) {
				System.out.println("Enter key:");
				key = s.nextLine();
				System.out.println("Enter value:");
				value = s.nextLine();
				attributes.put(key, value);
			} else {
				toContinue = false;
			}

		} while (toContinue);

		return new ElementTO(location, name, creationDate, expirationDate, type, attributes, creatorPlayground,
				creatorEmail);
	}

	private void playerAddNewActivity(UserTO user) {
		ActivityTO playerActivity = getActivityFromUser(user);
		Object res = this.rest.postForObject(this.url + ACTIVITIES_URL, playerActivity, ActivityTO.class,
				user.getPlayground(), user.getEmail());

		System.out.println(res);
	}

	private ActivityTO getActivityFromUser(UserTO user) {
		String elementPlayground;
		String elementId;
		String type;
		String playerPlayground = user.getPlayground();
		String playerEmail = user.getEmail();
		Map<String, Object> attributes = new HashMap<>();
		boolean toContinue = true;
		String loopRes;
		String key, value;

		System.out.println("Please enter element playground:");
		elementPlayground = s.nextLine();

		System.out.println("Please enter element id:");
		elementId = s.nextLine();

		System.out.println("Please enter type:");
		type = s.nextLine();

		System.out.println("Please enter keys and values");
		do {
			System.out.println("Do you want adding to the map? (Y / N)");
			loopRes = s.nextLine();
			if (loopRes.equalsIgnoreCase("Y")) {
				System.out.println("Enter key:");
				key = s.nextLine();
				System.out.println("Enter value:");
				value = s.nextLine();
				attributes.put(key, value);
			} else {
				toContinue = false;
			}

		} while (toContinue);

		return new ActivityTO(elementPlayground, elementId, type, playerPlayground, playerEmail, attributes);
	}

	private void getElementsById(UserTO user) {
		System.out.println("Please enter id:");
		String id = s.nextLine();

		ElementTO[] allElementsReturned = this.rest.getForObject(this.url + ELEMENTS_BY_ID, ElementTO[].class,
				user.getPlayground(), user.getEmail(), PLAYGROUND, id);

		Stream.of(allElementsReturned).forEach(System.out::println);

	}

	private void getElementsByAttribute(UserTO user) {
		System.out.println("Please enter attribute name key (NAME / TYPE):");
		String attributeName = s.nextLine();
		System.out.println("Please enter value to check:");
		String value = s.nextLine();

		ElementTO[] allElementsReturned = this.rest.getForObject(this.url + ELEMENTS_BY_ATTRIBUTES, ElementTO[].class,
				user.getPlayground(), user.getEmail(), attributeName, value);

		Stream.of(allElementsReturned).forEach(System.out::println);
	}

	private void GetElementsByDistance(UserTO user) {
		Double x, y, dist;

		System.out.println("Please enter X coordinate:");
		x = Double.parseDouble(s.nextLine());
		System.out.println("Please enter Y coordinate:");
		y = Double.parseDouble(s.nextLine());
		System.out.println("Please enter distance:");
		dist = Double.parseDouble(s.nextLine());

		ElementTO[] allElementsReturned = this.rest.getForObject(this.url + ELEMENTS_GET_NEAR_URL, ElementTO[].class,
				user.getPlayground(), user.getEmail(), x, y, dist);

		Stream.of(allElementsReturned).forEach(System.out::println);

	}

	private void getAllElements(UserTO user) {
		ElementTO[] allElementsReturned = this.rest.getForObject(this.url + ELEMENTS_GET_ALL_URL, ElementTO[].class,
				user.getPlayground(), user.getEmail());

		Stream.of(allElementsReturned).forEach(System.out::println);
	}

	private UserTO loginSystem() {
		String email;
		String playground;
		UserTO rvUser = null;
		System.out.println("Please enter your email:");
		email = s.nextLine();
		System.out.println("Please choose your playground:");
		playground = s.nextLine();

		try {
			rvUser = this.rest.getForObject(this.url + LOGIN_URL, UserTO.class, playground, email);
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return rvUser;
	}

	private void registerNewUser() {
		String email;
		String username;
		String avatar;
		String role;

		System.out.println("Please enter your email:");
		email = s.nextLine();
		System.out.println("Please choose an username:");
		username = s.nextLine();
		System.out.println("Please choose an avatar:");
		avatar = s.nextLine();
		System.out.println("Please choose a role (PLAYER / MANAGER):");
		role = s.nextLine();

		try {
			NewUserForm newUserForm = new NewUserForm(email, username, avatar, role);
			this.rest.postForObject(this.url + USERS_MAIN_URL, newUserForm, UserTO.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		firstScreen();
	}

}
