package com.sheena.playground.dal;

import com.sheena.playground.logic.elements.ElementEntity;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String>{
	
    public List<ElementEntity> findByNameEquals(
    		@Param("name") String name, 
    		Pageable pageable);
    
    public List<ElementEntity> findByTypeEquals(
    		@Param("type") String type, 
    		Pageable pageable);
    
    public List<ElementEntity> findByXBetweenAndYBetween(
    		@Param("x1") double d,
    		@Param("x2") double e,
    		@Param("y1") double f,
    		@Param("y2") double g,
    		Pageable pageable);
}