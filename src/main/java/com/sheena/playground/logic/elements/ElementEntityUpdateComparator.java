package com.sheena.playground.logic.elements;

import java.util.Comparator;

public class ElementEntityUpdateComparator implements Comparator<ElementEntity> {

	public ElementEntityUpdateComparator() {
	}

	@Override
	public int compare(ElementEntity o1, ElementEntity o2) {
		boolean areEquals = true;
		
		areEquals = o1.getAttributes().equals(o2.getAttributes()) &&
				o1.getCreationDate().equals(o2.getCreationDate()) &&
				o1.getCreatorEmail().equals(o2.getCreatorEmail()) &&
				o1.getCreatorPlayground().equals(o2.getCreatorPlayground()) &&
				o1.getExpirationDate().equals(o2.getExpirationDate()) &&
				o1.getName().equals(o2.getName()) &&
				o1.getPlayground().equals(o2.getPlayground()) &&
				o1.getType().equals(o2.getType()) &&
				o1.getX().equals(o2.getX()) &&
				o1.getY().equals(o2.getY());
		
		return areEquals == true ? 0: 1;
	}
}
