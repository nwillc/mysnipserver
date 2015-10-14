package com.github.nwillc.mysnipserver.controller;

import com.google.gson.Gson;
import spark.Route;
import static com.github.nwillc.mysnipserver.rest.Version.API_VERSION;

/**
 * Isolate as much Spark specific code here as possible.
 */
public interface SparkController {
	ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
		@Override
		protected Gson initialValue() {
			return new Gson();
		}
	};

	default String getVersionedPath(String path) {
		return "/" + API_VERSION + "/" + path;
	}
	default void get(String path, Route route) {
		spark.Spark.get(getVersionedPath(path),route, gson.get()::toJson);
	}

	default void post(String path, Route route) {
		spark.Spark.post(getVersionedPath(path), route, gson.get()::toJson);
	}
}
