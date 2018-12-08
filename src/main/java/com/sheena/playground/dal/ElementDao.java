package com.sheena.playground.dal;

import com.sheena.playground.logic.elements.ElementEntity;

import org.springframework.data.repository.CrudRepository;


/**
 * ElementDao
 */
public interface ElementDao extends CrudRepository<ElementEntity, String>{

    
}