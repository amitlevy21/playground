package com.sheena.playground.dal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sheena.playground.logic.users.UserEntity;


public interface UserDao extends CrudRepository<UserEntity, String>{
	
	public List<UserEntity> findUserByEmail(String email);
}
