package com.sheena.playground.logic;

public interface ActivityService {
	
	public ActivityEntity addNewActivity (ActivityEntity activity) throws ActivityAlreadyExistsException;
	public void cleanup();
}
