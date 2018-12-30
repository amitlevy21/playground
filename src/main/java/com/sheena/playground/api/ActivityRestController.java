package com.sheena.playground.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sheena.playground.logic.activities.ActivityNotFoundException;
import com.sheena.playground.logic.activities.ActivityService;
import com.sheena.playground.logic.activities.ActivityTypeNotAllowedException;
import com.sheena.playground.logic.activities.jpa.ActivityWithNoTypeException;

@RestController
public class ActivityRestController {
	
	private ActivityService activityService;
	
	@Autowired
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}
	
    @RequestMapping(
        method=RequestMethod.POST,
        path="/playground/activities/{userPlayground}/{email}",
        produces=MediaType.APPLICATION_JSON_VALUE,
        consumes=MediaType.APPLICATION_JSON_VALUE)
    public Object addNewActivity(
        @PathVariable("userPlayground") String userPlayground,
        @PathVariable("email") String email,
        @RequestBody ActivityTO newActivityTO) throws ActivityNotFoundException, ActivityTypeNotAllowedException, ActivityWithNoTypeException {
    	  	    	
    	return new ActivityTO(
    				this.activityService.addNewActivity(
    						newActivityTO.toActivityEntity()));
    }
}