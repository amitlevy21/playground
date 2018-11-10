package com.sheena.playground.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private final Map<String, Object> DUMMY_MAP = new HashMap<>();
	
	{
		DUMMY_MAP.put("1", "2");
	};
	private final ElementTO DUMMY_ELEMENT = new ElementTO("playground", "435", DUMMY_LOCATION, "name", DUMMY_DATE, DUMMY_DATE, "type", DUMMY_MAP, "", "" );
	private final ElementTO[] DUMMY_ELEMENTS = new ElementTO[] {
		DUMMY_ELEMENT,
		DUMMY_ELEMENT,
		DUMMY_ELEMENT,
	};
    
    @RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
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
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElements(
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email) {
		//TODO: apply logic 
		return DUMMY_ELEMENTS;
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsNearCoordinates(
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("x") Double x,
			@PathVariable("y") Double y,
			@PathVariable("distance") Double distance) {
		//TODO: apply logic
		return DUMMY_ELEMENTS;
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsAttribute(
			@PathVariable("userPlayground") String userPlayground, 
			@PathVariable("email") String email,
			@PathVariable("attributeName") String attributeName,
			@PathVariable("value") Double value) {
		//TODO: apply logic
		return DUMMY_ELEMENTS;
	}
}