package com.sheena.playground.logic.elements;

import com.sheena.playground.logic.users.UsersException;

public class AttributeUpdateException extends UsersException {

	private static final long serialVersionUID = -1040556491719273659L;

	public AttributeUpdateException() {
	}

	public AttributeUpdateException(String message) {
		super(message);
	}

	public AttributeUpdateException(Throwable cause) {
		super(cause);
	}

	public AttributeUpdateException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttributeUpdateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
