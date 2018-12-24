package com.sheena.playground.dal;

import org.springframework.data.repository.CrudRepository;

import com.sheena.playground.logic.jpa.IdGenerator;

public interface IdGeneratorDao extends CrudRepository<IdGenerator, Long>{

}
