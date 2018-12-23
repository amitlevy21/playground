package com.sheena.playground.logic.elements.exceptions;

/**
 * InvalidCreationDateException
 */
public class InvalidExpirationDateException extends ElementException {

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