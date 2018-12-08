package com.sheena.playground.logic.activity.jpa;

import org.springframework.data.repository.CrudRepository;

public interface IdGeneratorDao extends CrudRepository<IdGenerator, Long> {

}
