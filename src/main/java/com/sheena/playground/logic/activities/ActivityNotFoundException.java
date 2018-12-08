package com.sheena.playground.logic.activities;

public class ActivityNotFoundException extends Exception {

	private static final long serialVersionUID = 4827164884082700002L;

	public ActivityNotFoundException() {
		super();
	}

	public ActivityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityNotFoundException(String message) {
		super(message);
	}

	public ActivityNotFoundException(Throwable cause) {
		super(cause);
	}

	

}
