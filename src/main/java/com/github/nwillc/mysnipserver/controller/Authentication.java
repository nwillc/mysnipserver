package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.logging.Logger;

public class Authentication {
	private final static Logger LOGGER = Logger.getLogger(Authentication.class.getCanonicalName());

	public Authentication() {
		Spark.before(this::check);
	}

	private void check(Request request, Response response) {
		LOGGER.info("Check");
	}
}
