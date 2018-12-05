package com.sheena.playground.dal;

import org.springframework.data.repository.CrudRepository;

import com.sheena.playground.logic.ActivityEntity;

public interface ActivityDao extends CrudRepository<ActivityEntity, String>{

}
