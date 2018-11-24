package com.sheena.playground.logic.service.stubs;

import java.util.List;

import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.dao.ElementDao;
import com.sheena.playground.logic.dao.stubs.DummyElementDao;
import com.sheena.playground.logic.entity.ElementEntity;
import com.sheena.playground.logic.service.ElementService;

import org.springframework.stereotype.Service;

/**
 * ElementService
 */
@Service
public class DummyElementService implements ElementService{

	private ElementDao elementDao;

	public DummyElementService() {
		elementDao = new DummyElementDao();
	}

	public void addNewElement(ElementEntity et) {
		elementDao.addElement(et);
	}

	public void updateElement(ElementEntity et) {
		elementDao.updateElement(et);
	}

	public ElementEntity getElementById(String id) throws ElementNotExistException {
		return elementDao.getElementById(id);
	}

	public List<ElementEntity> getAllElements(int size, int page) {
		return elementDao.getAllElements(size, page);
	}

	public List<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
			throws ElementNotExistException {
		return elementDao.getElementsNearCoordinates(x, y, distance);
	}

	public List<ElementEntity> getElementsAttribute(String attributeName, Object value) throws ElementNotExistException {
		return elementDao.getElementsAttribute(attributeName, value);
	}

}