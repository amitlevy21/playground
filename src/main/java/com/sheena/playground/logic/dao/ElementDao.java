package com.sheena.playground.logic.dao;


import java.util.List;

import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.entity.ElementEntity;

/**
 * ElementDao
 */
public interface ElementDao {

    List<ElementEntity> getAllElements(int size, int page);

    ElementEntity getElementById(String id) throws ElementNotExistException;

    boolean removeElementById(String id);

    void updateElement(ElementEntity element);

    void addElement(ElementEntity et);

    List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance) throws ElementNotExistException;

	List<ElementEntity> getElementsAttribute(String attributeName, Object value) throws ElementNotExistException;
}