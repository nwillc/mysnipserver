/*
 * Copyright (c) 2016,  nwillc@gmail.com
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
import com.github.nwillc.mysnipserver.util.GoogleIdTokenUtil;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.mysnipserver.util.http.error.HttpException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.nwillc.mysnipserver.util.rest.Params.*;
import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;
import static spark.Spark.before;

public class Authentication extends SparkController<User> {
    private static final String IS_LOGGED_IN = "loggedIn.true";
    private static final String LOGIN_HTML = "/public/login.html";
    private static final String[] NO_AUTH = {
            LOGIN_HTML,
            "/public/js/login.js",
            "/public/js/cookies.js",
            "/favicon.ico",
            versionedPath("ping"),
            versionedPath("properties"),
            versionedPath("auth")
    };
    private final Set<String> noAuth = new HashSet<>();

    @Inject
    public Authentication(Dao<User> dao) {
        super(dao);
        before(this::check);
        for (String path : NO_AUTH) {
            noAuth(path);
        }
        get("auth/" + USERNAME.getLabel() + "/" + PASSWORD.getLabel(), this::login);
        get("auth/" + TOKEN.getLabel(), this::googleAuth);
        delete("auth", this::logout);
    }

    private void check(Request request, Response response) {
        Session session = request.session(true);

        if (noAuth.stream().anyMatch(path -> request.pathInfo().startsWith(path))) {
            return;
        }

        if (!Boolean.TRUE.equals(session.attribute(IS_LOGGED_IN))) {
            // auth required and not logged in, so redirect to login
            Logger.warn("Access violation: " + request.pathInfo());
            response.redirect(LOGIN_HTML);
            throw new HttpException(HttpStatusCode.UNAUTHERIZED);
        }
    }

    private Boolean logout(Request request, Response response) {
        request.session(true).attribute(IS_LOGGED_IN, Boolean.FALSE);
        return Boolean.TRUE;
    }

    private Boolean login(Request request, Response response) {
        Logger.info("Login attempt: " + USERNAME.from(request));

        final User user = getDao().findOne(USERNAME.from(request))
                .orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHERIZED));

        Logger.info("Found: " + user);
        if (PASSWORD.from(request).equals(user.getPassword())) {
            Session session = request.session(true);
            session.attribute(IS_LOGGED_IN, Boolean.TRUE);
        } else {
            throw new HttpException(HttpStatusCode.UNAUTHERIZED);
        }
        return Boolean.TRUE;
    }

    private Boolean googleAuth(Request request, Response response) {
        Optional<GoogleIdToken.Payload> payload;
        try {
            payload = GoogleIdTokenUtil.verify(TOKEN.from(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(HttpStatusCode.UNAUTHERIZED, "Failed decoding payload", e);
        }

        payload.orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHERIZED, "Rejected"));
        Logger.info("Google auth: " + payload.get().getEmail());
        getDao().findOne(payload.get().getEmail()).orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHERIZED, "Not registered user"));
        Session session = request.session(true);
        session.attribute(IS_LOGGED_IN, Boolean.TRUE);
        return Boolean.TRUE;
    }


    private void noAuth(String path) {
        Logger.info("No authentication required for: " + path);
        noAuth.add(path);
    }

}
