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

package com.github.nwillc.mysnipserver.handlers;

import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.mysnipserver.util.GoogleIdTokenUtil;
import com.github.nwillc.mysnipserver.util.http.HttpException;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.opa.Dao;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;
import ratpack.handling.Context;
import ratpack.session.Session;

import java.util.Arrays;
import java.util.Optional;

import static com.github.nwillc.mysnipserver.util.rest.Params.*;

public class AuthHandler {
    public final static String PATH = "v1/auth";
    private static final String IS_LOGGED_IN = "loggedIn.true";
    private static final String LOGIN_HTML = "login.html";
    private static final String[] NO_AUTH = {
            LOGIN_HTML,
            "js/app/login.js",
            "js/app/cookies.js",
            "js/app/main.js",
            "js/app/graphql.js",
            "js/app.js",
            "js/lib/require.js",
            "favicon.ico",
            "properties",
            PATH
    };
    private static final String OK = "ok";
    private final Dao<String, User> dao;


    @Inject
    public AuthHandler(Dao<String, User> dao) {
        this.dao = dao;
    }

    public void authRequired(Context context) {
        final String path = context.getRequest().getPath();
        Logger.debug("Auth check: " + path);

        context.get(Session.class).getData().then(sessionData -> {
            final Optional<?> o = sessionData.<Boolean>get(IS_LOGGED_IN);

            if (o.isPresent()) {
                Logger.debug("Logged in");
                context.next();
            } else {
                if (Arrays.stream(NO_AUTH).anyMatch(path::startsWith)) {
                    Logger.debug("No auth required");
                    context.next();
                } else {
                    Logger.info("Auth required on " + path);
                    context.redirect("/" + LOGIN_HTML);
                }
            }
        });
    }

    public void login(Context context) throws Exception {
        Logger.info("Login attempt: " + USERNAME.from(context));

        final Optional<User> userOptional = dao.findOne(USERNAME.from(context));
        if (!userOptional.isPresent()) {
            context.error(new HttpException(HttpStatusCode.UNAUTHORIZED));
        } else {
            final User user = userOptional.get();
            if (PASSWORD.from(context).equals(user.getPassword())) {
                Logger.info("Login passed.");
                context.get(Session.class).getData().then(sessionData -> {
                    sessionData.set(IS_LOGGED_IN, Boolean.TRUE);
                    context.render(OK);
                });
            } else {
                Logger.info("Login failed.");
                context.get(Session.class).getData().then(sessionData -> {
                    sessionData.remove(IS_LOGGED_IN);
                    context.error(new HttpException(HttpStatusCode.UNAUTHORIZED));
                });
            }
        }
    }

    public void logout(Context context) {
        context.get(Session.class).getData().then(sessionData -> {
            sessionData.remove(IS_LOGGED_IN);
            context.render(OK);
        });
    }

    public void googleAuth(Context context) throws Exception {
        final Optional<GoogleIdToken.Payload> payload = GoogleIdTokenUtil.verify(TOKEN.from(context));

//        payload.orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED, "Rejected"));
//        Logger.info("Google auth: " + payload.get().getEmail());
//        dao.findOne(payload.get().getEmail()).orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED, "Not registered user"));
//        Session session = request.session(true);
//        session.attribute(IS_LOGGED_IN, Boolean.TRUE);
    }
}
