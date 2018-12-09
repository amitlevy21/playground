package com.sheena.playground.dal;

import org.springframework.data.repository.CrudRepository;

import com.sheena.playground.logic.users.VerificationCodeEntity;

public interface VerificationCodeDao extends CrudRepository<VerificationCodeEntity, String> {
	
	public final String SUFFIX = "code";
}
