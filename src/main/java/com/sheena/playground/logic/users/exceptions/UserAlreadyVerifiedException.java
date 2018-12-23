package com.sheena.playground.logic.users.exceptions;

public class UserAlreadyVerifiedException extends UsersException {

	private static final long serialVersionUID = 5087462533137218286L;

	public UserAlreadyVerifiedException() {
	}

	public UserAlreadyVerifiedException(String message) {
		super(message);
	}

	public UserAlreadyVerifiedException(Throwable cause) {
		super(cause);
	}

	public UserAlreadyVerifiedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserAlreadyVerifiedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
