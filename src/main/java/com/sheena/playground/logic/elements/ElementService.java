package com.sheena.playground.logic.elements;


import java.util.List;

import com.sheena.playground.logic.elements.exceptions.ElementAlreadyExistsException;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;

/**
 * ElementService
 */
public interface ElementService {

    public ElementEntity addNewElement(ElementEntity element);

    public void updateElement(String id, ElementEntity element) throws ElementNotExistException;

    ElementEntity getElementById(String id) throws ElementNotExistException;

    List<ElementEntity> getAllElements(int size, int page);

    List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance, int size, int page)
            throws ElementNotExistException;

    List<ElementEntity> getElementsAttribute(String attributeName, Object value, int size, int page) throws ElementNotExistException, NoSuceElementAttributeException;

    public void cleanup();
}