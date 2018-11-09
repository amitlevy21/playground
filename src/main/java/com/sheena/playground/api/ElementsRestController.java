package com.sheena.playground.api;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElementsRestController {

    private final Location DUMMY_LOCATION = new Location();
    private final Date DUMMY_DATE = new Date();
    
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
	public ElementTO getElementById(
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
    
    @RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElementById(
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
}