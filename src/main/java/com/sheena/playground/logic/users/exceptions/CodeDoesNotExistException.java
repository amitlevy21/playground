package com.sheena.playground.logic.users.exceptions;

public class CodeDoesNotExistException extends UsersException {

	private static final long serialVersionUID = -7417409267757971699L;

	public CodeDoesNotExistException() {
	}

	public CodeDoesNotExistException(String message) {
		super(message);
	}

	public CodeDoesNotExistException(Throwable cause) {
		super(cause);
	}

	public CodeDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
