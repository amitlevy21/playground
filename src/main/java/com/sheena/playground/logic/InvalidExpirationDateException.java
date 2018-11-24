package com.sheena.playground.logic;

/**
 * InvalidCreationDateException
 */
public class InvalidExpirationDateException extends Exception {

	private static final long serialVersionUID = 1L;

    public InvalidExpirationDateException() {
	}

	public InvalidExpirationDateException(String message) {
		super(message);
	}

	public InvalidExpirationDateException(Throwable cause) {
		super(cause);
	}

	public InvalidExpirationDateException(String message, Throwable cause) {
		super(message, cause);
	}
}