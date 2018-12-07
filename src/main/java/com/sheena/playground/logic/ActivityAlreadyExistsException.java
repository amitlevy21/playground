package com.sheena.playground.logic;

public class ActivityAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -9112491629586439424L;

	public ActivityAlreadyExistsException() {
	}

	public ActivityAlreadyExistsException(String message) {
		super(message);
	}

	public ActivityAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public ActivityAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
