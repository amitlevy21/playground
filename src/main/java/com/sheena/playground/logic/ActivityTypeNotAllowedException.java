package com.sheena.playground.logic;

public class ActivityTypeNotAllowedException extends Exception {

	private static final long serialVersionUID = 3963813116745432689L;

	public ActivityTypeNotAllowedException() {
	}

	public ActivityTypeNotAllowedException(String message) {
		super(message);
	}

	public ActivityTypeNotAllowedException(Throwable cause) {
		super(cause);
	}

	public ActivityTypeNotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

}
