package com.github.nwillc.mysnipserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.nwillc.mysnipserver.rest.Version;
import spark.Route;

import java.util.logging.Logger;

/**
 * Isolate as much Spark specific code here as possible.
 */
public interface SparkController extends Version {
	Logger LOGGER = Logger.getLogger(SparkController.class.getCanonicalName());
	ThreadLocal<ObjectMapper> mapper = new ThreadLocal<ObjectMapper>(){
		@Override
		protected ObjectMapper initialValue() {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new Jdk8Module());
			return mapper;
		}
	};

	default String toJson(Object obj) {
		try {
			String val = mapper.get().writeValueAsString(obj);
			LOGGER.info("Convert " + obj + " to " + val);
			return val;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON", e);
		}
	}

	default void get(String path, Route route) {
		spark.Spark.get(versionedPath(path), route, this::toJson);
	}

	default void post(String path, Route route) {
		spark.Spark.post(versionedPath(path), route, this::toJson);
	}

	default void delete(String path, Route route) { spark.Spark.delete(versionedPath(path), route, this::toJson); }
}
