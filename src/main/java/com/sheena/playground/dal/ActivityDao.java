package com.sheena.playground.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.sheena.playground.logic.activities.ActivityEntity;

public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, String>{
	
	public List<ActivityEntity> findActivityByType(
			@Param("type") String type,
			Pageable pageable);
	
	public List<ActivityEntity> findActivityByElementId(
			@Param("id") String id,
			Pageable pageable);
	
}
