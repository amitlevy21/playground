package com.sheena.playground.logic.users;

import javax.persistence.Id;
import javax.validation.constraints.Email;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="codes")
public class VerificationCodeEntity {
	
	//this is the verification code
	private String id;
	
	@Email
	private String userEmail;
	
	public VerificationCodeEntity() {
	}

	public VerificationCodeEntity(String code, @Email String userEmail) {
		super();
		this.id = code;
		this.userEmail = userEmail;
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String code) {
		this.id = code;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
