package com.sheena.playground.dal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sheena.playground.logic.activity.ActivityEntity;


@RepositoryRestResource
public interface ActivityDao extends CrudRepository<ActivityEntity, String>{
//extends PagingAndSortingRepository<ActivityEntity, String> {
}

