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
import com.github.nwillc.opa.Dao;
import com.google.inject.Inject;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class AuthHandler implements Handler {
    public final static String PATH = "v1/auth";
    private final Dao<String, User> dao;

    @Inject
    public AuthHandler(Dao<String, User> dao) {
        this.dao = dao;
    }

    @Override
    public void handle(Context ctx) throws Exception {

    }
}
