package com.sheena.playground.dao;

import org.springframework.data.repository.CrudRepository;

import com.sheena.playground.logic.activity.ActivityEntity;

public interface ActivityDao extends CrudRepository<ActivityEntity, String>{

}

