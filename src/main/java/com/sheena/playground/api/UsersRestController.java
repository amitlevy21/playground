package com.sheena.playground.api;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersRestController {
	//TODO: Add dependencies once there is a logic layer
	private final String DUMMY_CODE = "code";
	private final String DUMMY_PLAYGROUND = "playground";
	private final Location DUMMY_LOCATION = new Location();
	private final Date DUMMY_DATE = new Date();
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO registerNewUser(@RequestBody NewUserForm newUserForm) {
		//TODO: use newUserForm object to build a new user entity
		return new UserTO(
				newUserForm.getEmail(), DUMMY_PLAYGROUND, 
				newUserForm.getUsername(), 
				newUserForm.getAvatar(), newUserForm.getRole());
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO verifyUserRegistration(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email, 
			@PathVariable("code") String code) {
		if (!this.DUMMY_CODE.equals(code)) {
			//TODO: throw invalid code exception (maybe a custom class?)
		}
		return new UserTO(email, playground, "username", "avatar", "player");
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userLoginRequest(
			@PathVariable("playground") String playground, 
			@PathVariable("email") String email) {
		//TODO: apply logic once one exists? (0_0)
		return new UserTO(email, playground, "username", "avatar", "player");
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUserProfile(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@RequestBody UserTO userTO) {
		//TODO: Once there is logic layer - an update to the DB will be required
	}
	
	/////////////////////////////////////////////////////////////////////
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground }/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO addNewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ElementTO newElementTO) {
		//TODO: Once there is logic layer - an update to the DB will be required
		return new ElementTO(newElementTO.getPlayground(),// OR: playground
				newElementTO.getId(),
				newElementTO.getLocation(),
				newElementTO.getName(),
				newElementTO.getCreationDate(),
				newElementTO.getExpirationDate(),
				newElementTO.getType(),
				newElementTO.getAttributes(),
				newElementTO.getCreatorPlayground(),
				newElementTO.getCreatorEmail()); // OR: playground
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("playground") String playground, 
			@PathVariable("id") String id,
			@RequestBody ElementTO ElementTO) {
		//TODO: Once there is logic layer - an update to the DB will be required
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO userElementRequest(
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("playground") String playground, 
			@PathVariable("id") String id) {
		//TODO: apply logic 
		return new ElementTO(playground,
				id,
				DUMMY_LOCATION,
				"name",
				DUMMY_DATE,
				DUMMY_DATE,
				"type", 
				null,
				userPlayground,
				email); 
	}
	
	////////////////////////////////////////////////////////////////////
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object addNewActivity(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ActivityTO newActivityTO) {
		
		//TODO: Once there is logic layer - an update to the DB will be required
		return new ActivityTO(userPlayground,/*newActivityTO.getPlayground(), // OR: userPlayground */
				newActivityTO.getId(),
				newActivityTO.getElementPlayground(),
				newActivityTO.getElementId(),
				newActivityTO.getType(),
				newActivityTO.getPlayerPlayground(),
				email,/*newActivityTO.getPlayerEmail(),// OR: email*/
				newActivityTO.getAttributes());
	}
	
	
	
	
	
	
}
