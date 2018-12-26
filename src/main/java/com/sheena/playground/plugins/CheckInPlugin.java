package com.sheena.playground.plugins;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ElementDao;
import com.sheena.playground.logic.activities.ActivityEntity;


@Component
public class CheckInPlugin implements Plugin {
	private ObjectMapper jackson;
	private Date start;
	private ElementDao elements;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		
	}
	
	@Autowired
	public void setElements(ElementDao elements) {
		this.elements = elements;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		return null;
	}

}
