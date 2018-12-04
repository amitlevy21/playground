package com.sheena.playground.dal;

import com.sheena.playground.logic.ActivityEntity;

import org.springframework.data.repository.CrudRepository;

public interface ActivityDao extends CrudRepository<ActivityEntity, String>{

}
