/*
 * Copyright (c) 2017, nwillc@gmail.com
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

import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.mysnipserver.util.GoogleIdTokenUtil;
import com.github.nwillc.mysnipserver.util.JsonMapper;
import com.github.nwillc.mysnipserver.util.http.HttpException;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.opa.Dao;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static com.github.nwillc.mysnipserver.util.rest.Params.*;
import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;
import static spark.Spark.before;

public class Authentication implements JsonMapper {
    private static final String AUTH_PATH = "auth";
    private static final String IS_LOGGED_IN = "loggedIn.true";
    private static final String LOGIN_HTML = "/login.html";
    private static final String[] NO_AUTH = {
            LOGIN_HTML,
            "/scripts/login.js",
            "/scripts/cookies.js",
            "/scripts/main.js",
            "/scripts/require.js",
            "/favicon.ico",
            "/properties",
            versionedPath(AUTH_PATH)
    };
    private final Dao<String, User> dao;
    private final Collection<String> noAuth = new HashSet<>();

    @Inject
    public Authentication(Dao<String, User> dao) {
        this.dao = dao;
        before(this::check);
        for (String path : NO_AUTH) {
            noAuth(path);
        }
        Spark.get(versionedPath(AUTH_PATH + '/' + USERNAME.getLabel() + '/' + PASSWORD.getLabel()), this::login);
        Spark.get(versionedPath(AUTH_PATH + '/' + TOKEN.getLabel()), this::googleAuth);
        Spark.delete(versionedPath(AUTH_PATH), Authentication::logout);
    }

    private void check(Request request, Response response) {
        Session session = request.session(true);

        if (noAuth.stream().anyMatch(path -> request.pathInfo().startsWith(path))) {
            return;
        }

        if (!Boolean.TRUE.equals(session.attribute(IS_LOGGED_IN))) {
            // auth required and not logged in, so redirect to login
            Logger.warn("Access violation: " + request.pathInfo());
            response.type("text/html");
            response.redirect(LOGIN_HTML);
        }
    }

    private static Boolean logout(Request request, Response response) {
        request.session(true).attribute(IS_LOGGED_IN, Boolean.FALSE);
        return Boolean.TRUE;
    }

    private Boolean login(Request request, Response response) {
        Logger.info("Login attempt: " + USERNAME.from(request));

        final User user = dao.findOne(USERNAME.from(request))
                .orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED));

        Logger.info("Found: " + user);
        if (PASSWORD.from(request).equals(user.getPassword())) {
            Session session = request.session(true);
            session.attribute(IS_LOGGED_IN, Boolean.TRUE);
        } else {
            throw new HttpException(HttpStatusCode.UNAUTHORIZED);
        }
        return Boolean.TRUE;
    }

    private Boolean googleAuth(Request request, Response response) {
        Optional<Payload> payload;
        try {
            payload = GoogleIdTokenUtil.verify(TOKEN.from(request));
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.UNAUTHORIZED, "Failed decoding payload", e);
        }

        payload.orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED, "Rejected"));
        Logger.info("Google auth: " + payload.get().getEmail());
        dao.findOne(payload.get().getEmail()).orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED, "Not registered user"));
        Session session = request.session(true);
        session.attribute(IS_LOGGED_IN, Boolean.TRUE);
        return Boolean.TRUE;
    }

    private void noAuth(String path) {
        Logger.info("No authentication required for: " + path);
        noAuth.add(path);
    }

}
