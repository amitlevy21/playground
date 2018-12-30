package com.sheena.playground.plugins;

import com.sheena.playground.logic.activities.ActivityEntity;

public interface PlaygroundPlugin {
	public Object invokeOperation(ActivityEntity activityEntity);
}
