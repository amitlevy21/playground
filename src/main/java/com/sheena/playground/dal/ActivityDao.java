package com.sheena.playground.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sheena.playground.logic.activities.ActivityEntity;


@RepositoryRestResource
public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, String>{

	List<ActivityEntity> findAllByTypeLike(
			@Param("type") String type,
			Pageable pageable);

}
