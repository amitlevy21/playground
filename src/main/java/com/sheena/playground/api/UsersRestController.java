package com.sheena.playground.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sheena.playground.logic.AttributeUpdateException;
import com.sheena.playground.logic.RoleDoesNotExistException;
import com.sheena.playground.logic.UserAlreadyExistsException;
import com.sheena.playground.logic.UserDoesNotExistException;
import com.sheena.playground.logic.UsersException;
import com.sheena.playground.logic.UsersService;
import com.sheena.playground.logic.VerificationCodeMismatchException;

@RestController
public class UsersRestController {
	private final String PLAYGROUND = "Sheena.2019A";
	private UsersService usersService;
	
	@Autowired
	public void setUserService(UsersService usersService) {
		this.usersService = usersService;
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO registerNewUser(@RequestBody NewUserForm newUserForm) throws UserAlreadyExistsException, RoleDoesNotExistException {
		return new UserTO(this.usersService.createNewUser(
				new UserTO(newUserForm, this.PLAYGROUND).toEntity()));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO verifyUserRegistration(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email, 
			@PathVariable("code") String code) throws UserDoesNotExistException, VerificationCodeMismatchException {
		this.usersService.verifyUserRegistration(email, code);
		
		return new UserTO(this.usersService.getUser(email));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO userLoginRequest(
			@PathVariable("playground") String playground, 
			@PathVariable("email") String email) throws UserDoesNotExistException {
		return new UserTO(this.usersService.login(this.usersService.getUser(email)));
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUserProfile(
			@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@RequestBody UserTO userTO) throws UserDoesNotExistException, AttributeUpdateException {
		this.usersService.updateUserDetails(email, userTO.toEntity());
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (UsersException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "There is no relevant message";
		}
		return new ErrorMessage(message);
	}
}
