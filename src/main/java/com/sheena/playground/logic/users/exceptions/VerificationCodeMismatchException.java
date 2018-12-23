package com.sheena.playground.logic.users.exceptions;

public class VerificationCodeMismatchException extends Exception {

	private static final long serialVersionUID = 8106315077450877875L;

	public VerificationCodeMismatchException() {
	}

	public VerificationCodeMismatchException(String message) {
		super(message);
	}

	public VerificationCodeMismatchException(Throwable cause) {
		super(cause);
	}

	public VerificationCodeMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public VerificationCodeMismatchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
