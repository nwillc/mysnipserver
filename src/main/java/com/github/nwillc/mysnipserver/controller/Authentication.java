package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Authentication implements SparkController {
	private final static Logger LOGGER = Logger.getLogger(Authentication.class.getCanonicalName());
	private final static String IS_LOGGED_IN = "loggedIn.true";
	private static final String LOGIN_HTML = "/login.html";
	private Set<String> noAuth = new HashSet<>();

	public Authentication() {
		Spark.before(this::check);
		noAuth(LOGIN_HTML);
	}

	private void check(Request request, Response response) {

		Session session = request.session(true);

		if (noAuth.stream().anyMatch(path -> request.pathInfo().equals(path))) {
			LOGGER.info("Path " + request.pathInfo() + " is white listed");
			session.attribute(IS_LOGGED_IN, Boolean.TRUE);
		} else if (!Boolean.TRUE.equals(session.attribute(IS_LOGGED_IN))) {
			// not white listed, not logged in, so redirect to login
			LOGGER.warning("Access violation: " + request.pathInfo());
			response.redirect(LOGIN_HTML);
		}
	}

	private void noAuth(String path) {
		noAuth.add(path);
	}
}
