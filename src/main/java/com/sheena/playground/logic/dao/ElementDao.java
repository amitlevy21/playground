package com.sheena.playground.logic.dao;

import java.util.Collection;

import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.entity.ElementEntity;

/**
 * ElementDao
 */
public interface ElementDao {

    Collection<ElementEntity> getAllElements(int size, int page);

    ElementEntity getElementById(String id) throws ElementNotExistException;

    boolean removeElementById(String id);

    void updateElement(ElementEntity element);

    void addElement(ElementEntity et);

    Collection<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance) throws ElementNotExistException;
}