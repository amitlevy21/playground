package com.sheena.playground.logic.activities;

public class ActivityTypeNotSupportedException extends Exception {

	private static final long serialVersionUID = 3963813116745432689L;

	public ActivityTypeNotSupportedException() {
	}

	public ActivityTypeNotSupportedException(String message) {
		super(message);
	}

	public ActivityTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

	public ActivityTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
