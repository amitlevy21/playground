package com.sheena.playground.logic.elements.exceptions;

/**
 * ElementException
 */
public class ElementException extends Exception{

    private static final long serialVersionUID = 1L;

    public ElementException() {
	}

	public ElementException(String message) {
		super(message);
	}

	public ElementException(Throwable cause) {
		super(cause);
	}

	public ElementException(String message, Throwable cause) {
		super(message, cause);
	}
}