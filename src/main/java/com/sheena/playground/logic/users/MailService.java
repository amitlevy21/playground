package com.sheena.playground.logic.users;

public interface MailService {

	public String VERIFICATION_SUBJECT = "verification link";

	public void sendMessage(final Mail mail);
}
