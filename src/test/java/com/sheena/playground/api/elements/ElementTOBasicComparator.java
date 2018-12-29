package com.sheena.playground.api.elements;

import java.util.Comparator;

import com.sheena.playground.api.ElementTO;

public class ElementTOBasicComparator implements Comparator<ElementTO> {

	public ElementTOBasicComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(ElementTO o1, ElementTO o2) {
		int rv = o1.getName().compareTo(o2.getName());
		if(rv == 0) {
			rv = o1.getCreationDate().compareTo(o2.getCreationDate());
			if(rv == 0) {
				rv = o1.getCreatorEmail().compareTo(o2.getCreatorEmail());
				if(rv == 0) {
					rv = o1.getCreatorPlayground().compareTo(o2.getCreatorPlayground());
					if(rv == 0) {
						rv = o1.getExpirationDate().compareTo(o2.getExpirationDate());
						if(rv == 0) {
							rv = o1.getLocation().compareTo(o2.getLocation());
							if(rv == 0) {
								rv = o1.getPlayground().compareTo(o2.getPlayground());
								if(rv == 0) {
									rv = o1.getType().compareTo(o2.getType());
									if(rv == 0 && o1.getAttributes().equals(o2.getAttributes()))
										return 0;
								}
							}
						}
					}
				}
			}
		}
		return 1;
	}
}
