package com.sheena.playground.plugins;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.dal.ActivityDao;
import com.sheena.playground.logic.activities.ActivityEntity;

public class RegisterShift implements Plugin {

	private ObjectMapper jackson;
	private Date currentTime;
	private ActivityDao activities;

	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
		this.currentTime = new Date();
	}

	@Autowired
	public void setActivities(ActivityDao actvities) {
		this.activities = actvities;
	}

	@Override
	public Object execute(ActivityEntity command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
