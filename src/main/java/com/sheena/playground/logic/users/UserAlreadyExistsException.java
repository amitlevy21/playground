package com.sheena.playground.logic.users;

public class UserAlreadyExistsException extends UsersException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755183153587785148L;

	public UserAlreadyExistsException() {
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
}
