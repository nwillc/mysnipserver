package com.github.nwillc.mysnipserver.controller;

import com.google.gson.Gson;
import spark.Route;

public interface SparkController {
	Gson gson = new Gson();

	default void get(String path, Route route) {
		spark.Spark.get(path,route,gson::toJson);
	}
}
