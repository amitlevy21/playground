package com.sheena.playground.api.users;

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
}
