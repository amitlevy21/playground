package com.sheena.playground.logic.elements;


import java.util.List;

import com.sheena.playground.logic.elements.exceptions.ElementAlreadyExistsException;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;

/**
 * ElementService
 */
public interface ElementService {

    public void addNewElement(ElementEntity element) throws ElementAlreadyExistsException;

    public void updateElement(String id, ElementEntity element) throws ElementNotExistException;

    ElementEntity getElementById(String id) throws ElementNotExistException;

    List<ElementEntity> getAllElements(int size, int page);

    List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
            throws ElementNotExistException;

    List<ElementEntity> getElementsAttribute(String attributeName, Object value) throws ElementNotExistException;

    public void cleanup();
}