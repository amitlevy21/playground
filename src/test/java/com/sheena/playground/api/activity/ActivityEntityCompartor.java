package com.sheena.playground.api.activity;

import java.util.Comparator;

import com.sheena.playground.logic.activities.ActivityEntity;

public class ActivityEntityCompartor implements Comparator<ActivityEntity> {

	public ActivityEntityCompartor() {
	}
	
	@Override
	public int compare(ActivityEntity a1, ActivityEntity a2) {
		int rv = a1.getPlayground().compareTo(a2.getPlayground());
		if(rv == 0) {
			rv = a1.getType().compareTo(a2.getType());
			
			if(rv == 0) {
				rv = a1.getPlayerEmail().compareTo(a2.getPlayerEmail());
				if(rv == 0) {
					rv = a1.getId().compareTo(a2.getId());
					if(rv == 0) {
						rv = a1.getElementPlayground().compareTo(a2.getElementPlayground());
						if(rv == 0)  {
							rv = a1.getPlayerPlayground().compareTo(a2.getPlayerPlayground());
							if (rv == 0) {
								rv = a1.getElementId().compareTo(a2.getElementId());
							}
						}
					}
				}
			}
		}
		return rv;
	}

}
