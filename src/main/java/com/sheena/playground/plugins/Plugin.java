package com.sheena.playground.plugins;

import com.sheena.playground.logic.activities.ActivityEntity;

public interface Plugin {
	public Object execute (ActivityEntity command) throws Exception;
}

