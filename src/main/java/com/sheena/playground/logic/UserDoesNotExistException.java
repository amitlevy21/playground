package com.sheena.playground.logic;

public class UserDoesNotExistException extends UsersException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6712997356383141272L;

	public UserDoesNotExistException() {
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
