package com.github.nwillc.mysnipserver.controller;

import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class Controller {
	private final Map<String, Route> routeMap = new HashMap<>();

	public Map<String,Route> getRoutes() {
		return routeMap;
	};
}
