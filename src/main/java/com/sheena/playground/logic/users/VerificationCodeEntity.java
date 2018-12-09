package com.sheena.playground.logic.users;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;

@Entity
public class VerificationCodeEntity {

	private String code;
	
	@Email
	private String userEmail;
	
	public VerificationCodeEntity() {
	}

	public VerificationCodeEntity(String code, @Email String userEmail) {
		super();
		this.code = code;
		this.userEmail = userEmail;
	}



	@Id
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

}
