package com.sheena.playground.logic.elements.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sheena.playground.logic.elements.exceptions.ElementAlreadyExistsException;
import com.sheena.playground.logic.elements.ElementEntity;
import com.sheena.playground.logic.elements.exceptions.ElementNotExistException;
import com.sheena.playground.logic.elements.exceptions.NoSuceElementAttributeException;
import com.sheena.playground.logic.users.exceptions.UserDoesNotExistException;
import com.sheena.playground.logic.elements.ElementService;

//import org.springframework.stereotype.Service;

/**
 * ElementService
 */
//@Service
public class DummyElementService implements ElementService{

	private Map<String, ElementEntity> idToElement;

	public DummyElementService() {
		idToElement = new HashMap<>();
	}

	public ElementEntity addNewElement(ElementEntity et) {
		
		return idToElement.put(et.getId(), et);
	}

	public void updateElement(String id, ElementEntity et) throws ElementNotExistException{
		idToElement.put(id, et);
	}

	public ElementEntity getElementById(String id) throws ElementNotExistException {
		ElementEntity et = idToElement.get(id);
        if (et == null)
            throw new ElementNotExistException();
        return et;
	}

	public List<ElementEntity> getAllElements(int size, int page) {
		return idToElement.values().stream().skip(size * page).limit(size).collect(Collectors.toList());
	}

	public List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
			throws ElementNotExistException {
				List<ElementEntity> rv = new ArrayList<>();
				for (ElementEntity e : idToElement.values()) {
					Double distanceX = Math.abs(e.getX() - x);
					Double distanceY = Math.abs(e.getY() - y);
					if (distance.compareTo(distanceX) >= 0 && distance.compareTo(distanceY) >= 0) {
						rv.add(e);
					}
				}
				if (rv.isEmpty())
					throw new ElementNotExistException();
				return rv;
	}

	public List<ElementEntity> getElementsAttribute(String attributeName, Object value) throws ElementNotExistException {
		List<ElementEntity> rv = new ArrayList<>();
        for (ElementEntity e : idToElement.values()) {
            if (e.getAttributes().get(attributeName).toString().equals(value.toString())) {
                rv.add(e);
            }
        }
        if (rv.isEmpty())
            throw new ElementNotExistException();
        return rv;
	}

	@Override
	public void cleanup() {
		idToElement.clear();
	}

	@Override
	public ElementEntity addNewElement(String creatorEmail, ElementEntity element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateElement(String updaterEmail, String elementId, ElementEntity element)
			throws ElementNotExistException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ElementEntity> getAllElements(String requestorEmail, int size, int page)
			throws UserDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ElementEntity> getElementsNearCoordinates(String requestorEmail, Double x, Double y, Double distance,
			int size, int page) throws ElementNotExistException, UserDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ElementEntity> getElementsAttribute(String requestorEmail, String attributeName, Object value, int size,
			int page) throws ElementNotExistException, NoSuceElementAttributeException, UserDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}
}