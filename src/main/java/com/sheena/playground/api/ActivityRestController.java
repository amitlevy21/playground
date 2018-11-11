package com.sheena.playground.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ActivityRestController {

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
    return new ActivityTO(newActivityTO.getPlayground(), // OR: userPlayground 
            newActivityTO.getId(),
            newActivityTO.getElementPlayground(),
            newActivityTO.getElementId(),
            newActivityTO.getType(),
            newActivityTO.getPlayerPlayground(),
            newActivityTO.getPlayerEmail(),// OR: email
            newActivityTO.getAttributes());
    }
}