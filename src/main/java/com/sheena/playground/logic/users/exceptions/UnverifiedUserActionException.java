package com.sheena.playground.logic.users.exceptions;

public class UnverifiedUserActionException extends UsersException {

	private static final long serialVersionUID = 1226652502017812751L;

	public UnverifiedUserActionException() {
	}

	public UnverifiedUserActionException(String message) {
		super(message);
	}

	public UnverifiedUserActionException(Throwable cause) {
		super(cause);
	}

	public UnverifiedUserActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnverifiedUserActionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
