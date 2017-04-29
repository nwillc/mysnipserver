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
import com.github.nwillc.mysnipserver.util.http.HttpException;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.opa.Dao;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;
import ratpack.handling.Context;
import ratpack.session.Session;

import static com.github.nwillc.mysnipserver.util.rest.Params.PASSWORD;
import static com.github.nwillc.mysnipserver.util.rest.Params.USERNAME;

public class AuthHandler {
    public final static String PATH = "v1/auth";
    public static final String IS_LOGGED_IN = "loggedIn.true";
    private static final String OK = "ok";
    private final Dao<String, User> dao;

    @Inject
    public AuthHandler(Dao<String, User> dao) {
        this.dao = dao;
    }

    public void login(Context context) throws Exception {
        Logger.info("Login attempt: " + USERNAME.from(context));

        final User user = dao.findOne(USERNAME.from(context))
                .orElseThrow(() -> new HttpException(HttpStatusCode.UNAUTHORIZED));

        if (PASSWORD.from(context).equals(user.getPassword())) {
            Logger.info("Login passed.");
            context.get(Session.class).getData().then(sessionData -> {
                sessionData.set(IS_LOGGED_IN, Boolean.TRUE);
                context.render(OK);
            });
        }
    }

    public void logout(Context context) {
        context.get(Session.class).getData().then(sessionData -> {
            sessionData.remove(IS_LOGGED_IN);
            context.render(OK);
        });
    }

    public void googleAuth(Context context) throws Exception {

    }
}
