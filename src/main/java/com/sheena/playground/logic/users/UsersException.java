package com.sheena.playground.logic.users;

public class UsersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8752512713486670356L;

	public UsersException() {
	}

	public UsersException(String message) {
		super(message);
	}

	public UsersException(Throwable cause) {
		super(cause);
	}

	public UsersException(String message, Throwable cause) {
		super(message, cause);
	}

	public UsersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
