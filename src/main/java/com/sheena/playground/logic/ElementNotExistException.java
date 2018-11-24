package com.sheena.playground.logic;

/**
 * ElementNotExistException
 */
public class ElementNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

    public ElementNotExistException() {
	}

	public ElementNotExistException(String message) {
		super(message);
	}

	public ElementNotExistException(Throwable cause) {
		super(cause);
	}

	public ElementNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}