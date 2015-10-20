/*
 * Copyright (c) 2015,  nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

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
