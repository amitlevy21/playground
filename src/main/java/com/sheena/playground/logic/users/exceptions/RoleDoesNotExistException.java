package com.sheena.playground.logic.users.exceptions;

public class RoleDoesNotExistException extends UsersException {

	private static final long serialVersionUID = 5485956617160236025L;

	public RoleDoesNotExistException() {
	}

	public RoleDoesNotExistException(String message) {
		super(message);
	}

	public RoleDoesNotExistException(Throwable cause) {
		super(cause);
	}

	public RoleDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoleDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
