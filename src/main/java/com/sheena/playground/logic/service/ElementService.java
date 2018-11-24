package com.sheena.playground.logic.service;


import java.util.List;

import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.entity.ElementEntity;

/**
 * ElementService
 */
public interface ElementService {

    public void addNewElement(ElementEntity elementTO);

    public void updateElement(ElementEntity elementTO);

    ElementEntity getElementById(String id) throws ElementNotExistException;

    List<ElementEntity> getAllElements(int size, int page);

    List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
            throws ElementNotExistException;

    List<ElementEntity> getElementsAttribute(String attributeName, Object value) throws ElementNotExistException;
}