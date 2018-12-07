package com.sheena.playground.logic.elements;

/**
 * ElementNotExistException
 */
public class ElementNotExistException extends ElementException {

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