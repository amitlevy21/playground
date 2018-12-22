package com.sheena.playground.logic.users.exceptions;

public class UserDoesNotExistException extends UsersException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6712997356383141272L;

	public UserDoesNotExistException() {
	}

	public UserDoesNotExistException(String message) {
		super(message);
	}

	public UserDoesNotExistException(Throwable cause) {
		super(cause);
	}

	public UserDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
