package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.rest.Version;
import com.google.gson.Gson;
import spark.Route;

/**
 * Isolate as much Spark specific code here as possible.
 */
public interface SparkController extends Version {
	ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
		@Override
		protected Gson initialValue() {
			return new Gson();
		}
	};

	default void get(String path, Route route) {
		spark.Spark.get(versionedPath(path),route, gson.get()::toJson);
	}

	default void post(String path, Route route) {
		spark.Spark.post(versionedPath(path), route, gson.get()::toJson);
	}
}
