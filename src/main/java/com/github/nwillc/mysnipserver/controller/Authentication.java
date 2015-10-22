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

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.mysnipserver.rest.HttpStatusCode;
import com.github.nwillc.mysnipserver.rest.error.HttpException;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import static com.github.nwillc.mysnipserver.rest.Params.*;

public class Authentication implements SparkController {
	private final static Logger LOGGER = Logger.getLogger(Authentication.class.getCanonicalName());
	private final static String IS_LOGGED_IN = "loggedIn.true";
	private static final String LOGIN_HTML = "/login.html";
	private Set<String> noAuth = new HashSet<>();
	private final Dao<User> dao;

	public Authentication(Dao<User> dao) {
		this.dao = dao;
		Spark.before(this::check);
		noAuth(LOGIN_HTML);
		noAuth("/login.js");
		get("auth/" + USERNAME.getLabel() + "/" + PASSWORD.getLabel(), this::login);
	}

	private void check(Request request, Response response) {
		Session session = request.session(true);

		if (noAuth.stream().anyMatch(path -> request.pathInfo().equals(path))) {
			LOGGER.info("Path " + request.pathInfo() + " is white listed");
		} else if (!Boolean.TRUE.equals(session.attribute(IS_LOGGED_IN))) {
			// not white listed, not logged in, so redirect to login
			LOGGER.warning("Access violation: " + request.pathInfo());
			response.redirect(LOGIN_HTML);
		}
	}

	private Boolean login(Request request, Response response) {
		LOGGER.info("Login attempt: " + USERNAME.from(request));

		final User user = dao.findOne(USERNAME.from(request))
				.orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHERIZED));

		LOGGER.info("Found: " + user);
		if (PASSWORD.from(request).equals(user.getPassword())) {
			Session session = request.session(true);
			session.attribute(IS_LOGGED_IN, Boolean.TRUE);
		} else {
			throw new HttpException(HttpStatusCode.UNAUTHERIZED);
		}
		return Boolean.TRUE;
	}

	private void noAuth(String path) {
		noAuth.add(path);
	}
}
