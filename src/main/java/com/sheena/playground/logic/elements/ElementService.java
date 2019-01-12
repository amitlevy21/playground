package com.sheena.playground.logic.elements;

import java.util.List;

import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;

public interface ElementService {

    public ElementEntity addNewElement(String creatorEmail, ElementEntity element);

    public void updateElement(String updaterEmail, String elementId, ElementEntity element) throws ElementNotExistException;

    ElementEntity getElementById(String id) throws ElementNotExistException;

    List<ElementEntity> getAllElements(String requestorEmail, int size, int page) throws UserDoesNotExistException;

    List<ElementEntity> getElementsNearCoordinates(String requestorEmail, Double x, Double y, Double distance, int size, int page)
            throws ElementNotExistException, UserDoesNotExistException;

    List<ElementEntity> getElementsAttribute(String requestorEmail, String attributeName, Object value, int size, int page) throws ElementNotExistException, NoSuceElementAttributeException, UserDoesNotExistException;

    public void cleanup();
}