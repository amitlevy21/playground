package com.sheena.playground.logic.service;

import java.util.Collection;

import com.sheena.playground.api.ElementTO;
import com.sheena.playground.logic.ElementNotExistException;
import com.sheena.playground.logic.dao.ElementDao;
import com.sheena.playground.logic.dao.stubs.ElementFakeDataImp;
import com.sheena.playground.logic.entity.ElementEntity;

import org.springframework.stereotype.Service;

/**
 * ElementService
 */
@Service
public class ElementService {

	private ElementDao elementDao;

	public ElementService() {
		elementDao = new ElementFakeDataImp();
	}

	public void addNewElement(ElementTO elementTO) {
		ElementEntity et = elementTO.toEntity();
		elementDao.addElement(et);
	}

	public void updateElement(ElementTO elementTO) {
		ElementEntity et = elementTO.toEntity();
		elementDao.updateElement(et);
	}

	public ElementTO getElementById(String id) throws ElementNotExistException {
		ElementTO eTo = new ElementTO(elementDao.getElementById(id));
		return eTo;
	}

	public Collection<ElementEntity> getAllElements(int size, int page) {
		return elementDao.getAllElements(size, page);
	}

	public Collection<ElementEntity> getElementsNearCoordinates(Double x, Double y, Double distance)
			throws ElementNotExistException {
		return elementDao.getElementsNearCoordinates(x, y, distance);
	}

	public Collection<ElementTO> getElementsAttribute(String attributeName, Double value) {
		return null;
	}

}