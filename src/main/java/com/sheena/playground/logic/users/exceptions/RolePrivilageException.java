package com.sheena.playground.logic.users.exceptions;

public class RolePrivilageException extends UsersException {

	public RolePrivilageException() {
	}

	public RolePrivilageException(String message) {
		super(message);
	}

	public RolePrivilageException(Throwable cause) {
		super(cause);
	}

	public RolePrivilageException(String message, Throwable cause) {
		super(message, cause);
	}

	public RolePrivilageException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
