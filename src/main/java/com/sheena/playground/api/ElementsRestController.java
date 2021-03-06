package com.sheena.playground.api;

import java.util.stream.Collectors;

import com.sheena.playground.logic.elements.exceptions.ElementAlreadyExistsException;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.aop.MyLog;
import com.sheena.playground.logic.elements.ElementService;
import com.sheena.playground.logic.elements.exceptions.InvalidExpirationDateException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElementsRestController {

	@Autowired
	private ElementService elementService;

	@MyLog
	@RequestMapping(method = RequestMethod.POST, 
	path = "/playground/elements/{userPlayground}/{email}", 
	produces = MediaType.APPLICATION_JSON_VALUE, 
	consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO addNewElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@RequestBody ElementTO newElementTO) throws ElementAlreadyExistsException, InvalidExpirationDateException {
		return new ElementTO(elementService.addNewElement(email, newElementTO.toEntity()));
	}

	@MyLog
	@RequestMapping(method = RequestMethod.PUT, 
	path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
	produces = MediaType.APPLICATION_JSON_VALUE, 
	consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("playground") String playground,
			@PathVariable("id") String id, 
			@RequestBody ElementTO elementTO) throws ElementNotExistException, InvalidExpirationDateException {
		elementService.updateElement(email, id, elementTO.toEntity());
	}

	@MyLog
	@RequestMapping(method = RequestMethod.GET, 
	path = "/playground/elements/{userPlayground}/{email}/{playground}/{id}", 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElementById(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("playground") String playground,
			@PathVariable("id") String id) throws ElementNotExistException {
		return new ElementTO(elementService.getElementById(id));
	}

	@MyLog
	@RequestMapping(method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws UserDoesNotExistException {
		return elementService.getAllElements(email, size, page).stream().map(ElementTO::new).collect(Collectors.toList())
				.toArray(new ElementTO[0]);
		
	}

	@MyLog
	@RequestMapping(method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsNearCoordinates(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, 
			@PathVariable("x") Double x, 
			@PathVariable("y") Double y,
			@PathVariable("distance") Double distance,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws ElementNotExistException, UserDoesNotExistException {
		return elementService.getElementsNearCoordinates(email, x, y, distance, size, page).stream().map(ElementTO::new).collect(Collectors.toList()).toArray(new ElementTO[0]);
	}

	@MyLog
	@RequestMapping(method = RequestMethod.GET, 
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsAttribute(
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email, @PathVariable("attributeName") String attributeName,
			@PathVariable("value") Object value,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws ElementNotExistException, NoSuceElementAttributeException, UserDoesNotExistException{
		return elementService.getElementsAttribute(email, attributeName, value, size, page).stream().map(ElementTO::new).collect(Collectors.toList()).toArray(new ElementTO[0]);
	}
}