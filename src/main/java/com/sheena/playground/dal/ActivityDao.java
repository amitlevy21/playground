package com.sheena.playground.dal;

import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;

import com.sheena.playground.logic.activities.ActivityEntity;

public interface ActivityDao extends CrudRepository<ActivityEntity, String>{

}
